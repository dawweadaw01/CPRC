package com.lhj.crpc;

import com.lhj.crpc.channelhandler.ConsumerChannelInitializer;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

/**
 * @description：
 * @createTime：2023-09-0322:27
 * @author：banyanmei
 */
@Slf4j
public class NettyBootstrapInitializer {
    private static final Bootstrap BOOTSTRAP = new Bootstrap();

    static {
        NioEventLoopGroup group = new NioEventLoopGroup();
        BOOTSTRAP.group(group)
            // 选择初始化一个什么样的channel
            .channel(NioSocketChannel.class)
                //初始化消费端的channel
            .handler(new ConsumerChannelInitializer());
    }
    private NettyBootstrapInitializer() {
    }

    public static Bootstrap getBootstrap() {
        return BOOTSTRAP;
    }

}

