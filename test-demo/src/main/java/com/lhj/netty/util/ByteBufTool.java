package com.lhj.netty.util;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;

/**
 * @description：
 * @createTime：2023-08-2812:33
 * @author：banyanmei
 */
public class ByteBufTool {
    public static void printAsBinary(ByteBuf byteBuf){

        byte[] bytes = new byte[byteBuf.readableBytes()];
        byteBuf.getBytes(byteBuf.readerIndex(),bytes);
        String string = ByteBufUtil.hexDump(bytes);

        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < string.length(); i+=2) {
            stringBuilder.append(string, i, i+2).append(" ");
        }
        System.out.println(stringBuilder.toString());
    }
}
