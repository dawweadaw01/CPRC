package com.lhj.crpc.serialize.impl;

import com.alibaba.fastjson2.JSON;
import com.lhj.crpc.message.RequestPayload;
import lombok.extern.slf4j.Slf4j;
import com.lhj.crpc.serialize.Serializer;

import java.util.Arrays;

/**
 * @author banyan
 * @createTime 2023-07-04
 */
@Slf4j
public class JsonSerializer implements Serializer {
    @Override
    public byte[] serialize(Object object) {
        if (object == null) {
            return null;
        }
        
        byte[] result = JSON.toJSONBytes(object);
        if (log.isDebugEnabled()) {
            log.debug("对象【{}】已经完成了序列化操作，序列化后的字节数为【{}】", object, result.length);
        }
        return result;
        
    }
    
    @Override
    public <T> T deserialize(byte[] bytes, Class<T> clazz) {
        if (bytes == null || clazz == null) {
            return null;
        }
        T t = JSON.parseObject(bytes, clazz);
        if (log.isDebugEnabled()) {
            log.debug("类【{}】已经完成了反序列化操作.", clazz);
        }
        return t;
    }
}
