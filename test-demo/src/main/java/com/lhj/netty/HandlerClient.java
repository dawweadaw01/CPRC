package com.lhj.netty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.concurrent.EventExecutorGroup;

import java.nio.charset.StandardCharsets;

/**
 * Description：
 * CreateTime：2023-08-2614:21
 * Author：banyanmei
 */
public class HandlerClient extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf byteBuf = (ByteBuf) msg;
        System.out.println("收到服务端的消息：" + byteBuf.toString(StandardCharsets.UTF_8));
        //ctx.channel().writeAndFlush(Unpooled.copiedBuffer("hello server".getBytes(StandardCharsets.UTF_8)));
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println("出现异常");
        cause.printStackTrace();
        ctx.close();
    }
}
