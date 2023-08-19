package com.lhj.netty;


import io.netty.buffer.ByteBuf;
import io.netty.buffer.CompositeByteBuf;
import io.netty.buffer.Unpooled;

import java.util.PriorityQueue;
import java.util.Scanner;

public class Test {
    @org.junit.Test
    public void testByteBuf() {
        ByteBuf head = Unpooled.buffer();
        ByteBuf body = Unpooled.buffer();
        CompositeByteBuf byteBufs = Unpooled.compositeBuffer().addComponents(head, body);
        System.out.println(head);
        System.out.println(body);
        System.out.println(byteBufs);
    }

    @org.junit.Test
    public void testWrap() {
        byte[] bytes = new byte[10];
        ByteBuf byteBuf = Unpooled.wrappedBuffer(bytes);
        System.out.println(byteBuf);
    }

    @org.junit.Test
    public void testSlice() {
        ByteBuf byteBuf = Unpooled.buffer(1024);
        byteBuf.writeBytes("hello world".getBytes());
        System.out.println(new String(byteBuf.array()));
        ByteBuf slice = byteBuf.slice(0, 5);
        ByteBuf slice1 = byteBuf.slice(5, 10);
        // 得到byte数组转换为字符串
        System.out.println(new String(slice.array()));
        System.out.println(new String(slice1.array()));
        slice.setByte(0, 'H');
        System.out.println(new String(slice.array()));
        System.out.println(new String(slice1.array()));
    }

    @org.junit.Test
    public void name() {
        Scanner scanner = new Scanner(System.in);
        PriorityQueue<Integer> queue = new PriorityQueue<>();
        queue.add(0);
        queue.add(0);
        queue.add(1);
        queue.add(1);
        queue.add(1);
        System.out.println(queue.peek());
        queue.poll();
        System.out.println(queue.peek());
        queue.poll();
        System.out.println(queue.peek());
    }
}
