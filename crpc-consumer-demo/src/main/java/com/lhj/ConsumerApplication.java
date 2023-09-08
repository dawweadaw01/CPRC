package com.lhj;

import com.lhj.crpc.ReferenceConfig;
import com.lhj.crpc.api.HelloCrpc;
import com.lhj.crpc.discovery.RegistryConfig;
import lombok.extern.slf4j.Slf4j;
import com.lhj.crpc.CrpcBootstrap;

/**
 * @author banyan
 * @createTime 2023-06-28
 */
@Slf4j
public class ConsumerApplication {
    
    public static void main(String[] args) {
        // 想尽一切办法获取代理对象,使用ReferenceConfig进行封装
        // reference一定用生成代理的模板方法，get()
        ReferenceConfig<HelloCrpc> reference = new ReferenceConfig<>();
        reference.setInterface(HelloCrpc.class);
        reference.setGroup("default");


        // 代理做了些什么，
        // 1、连接注册中心
        // 2、拉取服务列表
        // 3、选择一个服务并建立连接
        // 4、发送请求，携带一些信息（接口名，参数列表，方法的名字），获得结果
        CrpcBootstrap.getInstance()
            .application("first-yrpc-consumer")
            .registry(new RegistryConfig("zookeeper://127.0.0.1:2181"));
            //.serialize("hessian")
            //.compress("gzip")
            //.reference(reference);
        
        
        System.out.println("-----------------------------------");
    
        System.out.println("++------->>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        HelloCrpc helloYrpc = reference.get();
        System.out.println(helloYrpc.sayHi("default"));

        ReferenceConfig<HelloCrpc> reference2 = new ReferenceConfig<>();
        reference2.setInterface(HelloCrpc.class);
        reference2.setGroup("primary");
        HelloCrpc helloCrpc = reference2.get();
        System.out.println(helloCrpc.sayHi("primary"));
        //helloYrpc.sayHi("你好crpc");

        while (true){

        }
     
//        while (true) {
//            try {
//                Thread.sleep(10000);
//                System.out.println("++------->>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
//            } catch (InterruptedException e) {
//                throw new RuntimeException(e);
//            }
//
//            for (int i = 0; i < 5; i++) {
//                String sayHi = helloYrpc.sayHi("你好crpc");
//                log.info("sayHi-->{}", sayHi);
//            }
//        }
    }
}
