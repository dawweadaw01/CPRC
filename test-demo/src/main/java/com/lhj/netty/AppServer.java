package com.lhj.netty;


import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;


/**
 * Description：
 * CreateTime：2023-08-2613:51
 * Author：banyanmei
 */
public class AppServer {
    private final int port;

    public AppServer(int port) {
        this.port = port;
    }

    public static void main(String[] args) {
        new AppServer(8080).run();
    }
    public void run(){
        EventLoopGroup boos = new NioEventLoopGroup();
        NioEventLoopGroup worker = new NioEventLoopGroup();
        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(boos,worker)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new HandlerServer());
                        }
                    });
            ChannelFuture channelFuture = serverBootstrap.bind(port).sync();
            System.out.println("开启监听在" + channelFuture.channel().localAddress() + port + "上");
            //阻塞操作，closeFuture()开启了一个channel的监听器（这期间channel在进行各项工作），直到链路断开
            // closeFuture().sync()会阻塞当前线程，直到通道关闭操作完成。这可以用于确保在关闭通道之前，程序不会提前退出。
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            boos.shutdownGracefully();
            worker.shutdownGracefully();
        }
    }
}
