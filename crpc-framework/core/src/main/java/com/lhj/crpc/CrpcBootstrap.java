package com.lhj.crpc;

import com.lhj.crpc.annotation.CrpcApi;
import com.lhj.crpc.channelhandler.handler.CrpcRequestDecoder;
import com.lhj.crpc.channelhandler.handler.CrpcResponseEncoder;
import com.lhj.crpc.channelhandler.handler.MethodCallHandler;
import com.lhj.crpc.config.Configuration;
import com.lhj.crpc.config.XmlResolver;
import com.lhj.crpc.core.HeartbeatDetector;
import com.lhj.crpc.core.CrpcShutdownHook;
import com.lhj.crpc.discovery.RegistryConfig;
import com.lhj.crpc.loadbalancer.LoadBalancer;
import com.lhj.crpc.message.CrpcRequest;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.net.InetSocketAddress;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @description：
 * @createTime：2023-09-0117:12
 * @author：banyanmei
 */
@Slf4j
public class CrpcBootstrap {

    // 用来缓存和服务端建立的连接
    public static final Map<InetSocketAddress, Channel> CHANNEL_CACHE = new ConcurrentHashMap<>(16);

    // CrpcBootstrap是个单例，我们希望每个应用程序只有一个实例
    public static final CrpcBootstrap CrpcBootstrap = new CrpcBootstrap();

    // 保存request对象，可以到当前线程中随时获取
    public static final ThreadLocal<CrpcRequest> REQUEST_THREAD_LOCAL = new ThreadLocal<>();

    public final static TreeMap<Long, Channel> ANSWER_TIME_CHANNEL_CACHE = new TreeMap<>();

    ConcurrentHashMap<String, TreeMap<Long, Channel>> ANSWER_TIME_CHANNEL_CACHE2 = new ConcurrentHashMap<>(16);

    // 全局配置
    public final Configuration configuration;

    // 服务列表
    public static final Map<String, ServiceConfig<?>> SERVERS_LIST = new ConcurrentHashMap<>(16);

    // 定义全局的对外挂起的 completableFuture
    public final static Map<Long, CompletableFuture<Object>> PENDING_REQUEST = new ConcurrentHashMap<>(128);

    private CrpcBootstrap() {
        // 构造启动引导程序，时需要做一些什么初始化的事
        configuration = new Configuration();
    }

    public static CrpcBootstrap getInstance() {
        return CrpcBootstrap;
    }

    /**
     * 用来定义当前应用的名字
     *
     * @param appName 应用的名字
     * @return this当前实例
     */
    public CrpcBootstrap application(String appName) {
        configuration.setAppName(appName);
        return this;
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    /**
     * 用来配置一个注册中心
     *
     * @param registryConfig 注册中心
     * @return this当前实例
     */
    public CrpcBootstrap registry(RegistryConfig registryConfig) {
        configuration.setRegistryConfig(registryConfig);
        return this;
    }

    public CrpcBootstrap loadBalancer(LoadBalancer loadBalancer) {
        configuration.setLoadBalancer(loadBalancer);
        return this;
    }

    /**
     * 配置当前暴露的服务使用的协议
     * @param protocolConfig 协议的封装
     * @return this当前实例
     */
//    public CrpcBootstrap protocol(ProtocolConfig protocolConfig) {
//        if(log.isDebugEnabled()){
//            log.debug("当前工程使用了：{}协议进行序列化",protocolConfig.toString());
//        }
//        return this;
//    }


    /**
     * ---------------------------服务提供方的相关api---------------------------------
     */

    /**
     * 发布服务，将接口-》实现，注册到服务中心
     *
     * @param service 封装的需要发布的服务
     * @return this当前实例
     */
    public CrpcBootstrap publish(ServiceConfig<?> service) {
        // 1、注册服务
        configuration.getRegistryConfig().getRegistry().register(service);
        // 2.将服务放入服务列表
        SERVERS_LIST.put(service.getInterface().getName() + "/" + service.getGroup(), service);
        // 3.打印日志
        if (log.isDebugEnabled()) {
            log.debug("服务{},已经被注册,添加列表成功参数值为key:{},value:{}", service.getInterface().getName(),
                    service.getInterface().getName(), service.getRef());
        }
        return this;
    }

    /**
     * 批量发布
     *
     * @param services 封装的需要发布的服务集合 服务集合
     * @return this当前实例
     */
    public CrpcBootstrap publish(List<ServiceConfig<?>> services) {
        for (ServiceConfig<?> service : services) {
            this.publish(service);
        }
        return this;
    }

    /**
     * 配置序列化的方式
     *
     * @param serializeType 序列化的方式
     */
    public CrpcBootstrap serialize(String serializeType) {
        //  配置序列化的方式
        configuration.setSerializeType(serializeType);
        if (log.isDebugEnabled()) {
            log.debug("我们配置了使用的序列化的方式为【{}】.", serializeType);
        }
        return this;
    }

    /**
     * 配置压缩的方式
     *
     * @param compressType
     * @return
     */
    public CrpcBootstrap compress(String compressType) {
        //  配置压缩的方式
        configuration.setCompressType(compressType);
        if (log.isDebugEnabled()) {
            log.debug("我们配置了使用的压缩算法为【{}】.", compressType);
        }
        return this;
    }

    /**
     * 启动netty服务
     */
    public void start() {
        log.info("开始启动crpc服务");
        // 路径扫描
        String scanPackage = XmlResolver.SCAN_PACKAGE;
        if (scanPackage != null) {
            scan(scanPackage);
        }
        // 注册关闭应用程序的钩子函数
        Runtime.getRuntime().addShutdownHook(new CrpcShutdownHook());

        // 1、创建eventLoop，老板只负责处理请求，之后会将请求分发至worker
        EventLoopGroup boss = new NioEventLoopGroup(2);
        EventLoopGroup worker = new NioEventLoopGroup(10);
        try {

            // 2、需要一个服务器引导程序
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            // 3、配置服务器
            serverBootstrap = serverBootstrap.group(boss, worker)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) {
                            // 是核心，我们需要添加很多入站和出站的handler
                            socketChannel.pipeline().addLast(new LoggingHandler())
                                    .addLast(new CrpcRequestDecoder())
                                    // 根据请求进行方法调用
                                    .addLast(new MethodCallHandler())
                                    .addLast(new CrpcResponseEncoder());
                        }
                    });

            // 4、绑定端口
            ChannelFuture channelFuture = serverBootstrap.bind(configuration.getPort()).sync();
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            try {
                boss.shutdownGracefully().sync();
                worker.shutdownGracefully().sync();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * 扫描包，进行批量注册
     *
     * @param packageName 包名
     * @return this本身
     */
    public CrpcBootstrap scan(String packageName) {
        // 1、需要通过packageName获取其下的所有的类的权限定名称
        List<String> classNames = getAllClassNames(packageName);
        // 2、通过反射获取他的接口，构建具体实现
        List<Class<?>> classes = classNames.stream()
                .map(className -> {
                    try {
                        return Class.forName(className);
                    } catch (ClassNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                }).filter(clazz -> clazz.getAnnotation(CrpcApi.class) != null)
                .collect(Collectors.toList());

        for (Class<?> clazz : classes) {
            // 获取他的接口
            Class<?>[] interfaces = clazz.getInterfaces();
            Object instance;
            try {
                instance = clazz.getConstructor().newInstance();
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                     NoSuchMethodException e) {
                throw new RuntimeException(e);
            }

            // 得到组
            CrpcApi crpcApi = clazz.getAnnotation(CrpcApi.class);
            String group = crpcApi.group();

            for (Class<?> anInterface : interfaces) {
                ServiceConfig<?> serviceConfig = ServiceConfig.builder()
                        .interfaceProvider(anInterface)
                        .ref(instance)
                        .group(group)
                        .build();
                if (log.isDebugEnabled()) {
                    log.debug("---->已经通过包扫描，将服务【{}】发布.", anInterface);
                }
                // 3、发布
                publish(serviceConfig);
            }

        }
        return this;
    }

    private List<String> getAllClassNames(String packageName) {
        // 1、通过packageName获得绝对路径
        // com.ydlclass.xxx.yyy -> E://xxx/xww/sss/com/ydlclass/xxx/yyy
        String basePath = packageName.replaceAll("\\.", "/");
        URL url = ClassLoader.getSystemClassLoader().getResource(basePath);
        if (url == null) {
            throw new RuntimeException("包扫描时，发现路径不存在.");
        }
        String absolutePath = url.getPath();
        //
        List<String> classNames = new ArrayList<>();
        classNames = recursionFile(absolutePath, classNames, basePath);

        return classNames;
    }

    private List<String> recursionFile(String absolutePath, List<String> classNames, String basePath) {
        // 获取文件
        File file = new File(absolutePath);
        // 判断文件是否是文件夹
        if (file.isDirectory()) {
            // 找到文件夹的所有的文件
            File[] children = file.listFiles(pathname -> pathname.isDirectory() || pathname.getPath().contains(".class"));
            if (children == null) {
                return classNames;
            }
            for (File child : children) {
                if (child.isDirectory()) {
                    // 递归调用
                    recursionFile(child.getAbsolutePath(), classNames, basePath);
                } else {
                    // 文件 --> 类的权限定名称
                    String className = getClassNameByAbsolutePath(child.getAbsolutePath(), basePath);
                    classNames.add(className);
                }
            }

        } else {
            // 文件 --> 类的权限定名称
            String className = getClassNameByAbsolutePath(absolutePath, basePath);
            classNames.add(className);
        }
        return classNames;
    }

    private String getClassNameByAbsolutePath(String absolutePath, String basePath) {
        // E:\project\ydlclass-yrpc\yrpc-framework\yrpc-core\target\classes\com\ydlclass\serialize\Serializer.class
        // com\ydlclass\serialize\Serializer.class --> com.ydlclass.serialize.Serializer
        String fileName = absolutePath
                .substring(absolutePath.indexOf(basePath.replaceAll("/", "\\\\")))
                .replaceAll("\\\\", ".");

        fileName = fileName.substring(0, fileName.indexOf(".class"));
        return fileName;
    }

    /**
     * ---------------------------服务调用方的相关api---------------------------------
     */
    public CrpcBootstrap reference(ReferenceConfig<?> reference, String group) {

        // 开启对这个服务的心跳检测
        HeartbeatDetector.detectHeartbeat(reference.getInterface().getName(), group);

        // 在这个方法里我们是否可以拿到相关的配置项-注册中心
        // 配置reference，将来调用get方法时，方便生成代理对象
        // 1、reference需要一个注册中心
        //reference.setRegistry(configuration.getRegistryConfig().getRegistry());
        //reference.setGroup(this.getConfiguration().getGroup());
        return this;
    }

    public CrpcBootstrap group(String group) {
        this.getConfiguration().setGroup(group);
        return this;
    }
}

