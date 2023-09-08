package com.lhj.netty;

/**
 * @description：
 * @createTime：2023-08-2913:25
 * @author：banyanmei
 */
public interface Compress {
    byte[] compress(byte[] bytes);

    byte[] decompress(byte[] bytes);

    byte getCode();
}
