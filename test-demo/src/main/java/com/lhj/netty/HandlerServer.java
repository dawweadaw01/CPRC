package com.lhj.netty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.nio.charset.StandardCharsets;

/**
 * Description：
 * CreateTime：2023-08-2614:12
 * Author：banyanmei
 */
public class HandlerServer extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf byteBuf = (ByteBuf) msg;
        System.out.println("收到客户端的消息：" + byteBuf.toString(StandardCharsets.UTF_8));
        ctx.channel().writeAndFlush(Unpooled.copiedBuffer("hello client".getBytes(StandardCharsets.UTF_8)));
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        //出现异常的时候执行的动作（打印并关闭通道）
        System.out.println("出现异常");
        cause.printStackTrace();
        ctx.close();
    }
}
