package com.lhj.crpc.channelhandler;


import com.lhj.crpc.channelhandler.handler.CrpcRequestEncoder;
import com.lhj.crpc.channelhandler.handler.CrpcResponseDecoder;
import com.lhj.crpc.channelhandler.handler.MySimpleChannelInboundHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;


/**
 * @description：
 * @createTime：2023-09-0322:34
 * @author：banyanmei
 */
public class ConsumerChannelInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        //  消费者的出栈handler的实现
        socketChannel.pipeline()
                .addLast(new LoggingHandler(LogLevel.DEBUG))
                .addLast(new CrpcRequestEncoder())
                .addLast(new CrpcResponseDecoder())
                .addLast(new MySimpleChannelInboundHandler());
    }
}
