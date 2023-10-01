package com.lhj.crpc.proxy;


import com.lhj.crpc.CrpcBootstrap;
import com.lhj.crpc.ReferenceConfig;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author banyan
 * @createTime 2023-07-30
 */
public class CrpcProxyFactory {

    private static final Map<String,Object> cache = new ConcurrentHashMap<>(32);

    public static <T> T getProxy(Class<T> clazz, String group) {

        String key = clazz.getName() + group;
        Object bean = cache.get(key);
        if (bean != null) {
            return (T) bean;
        }

        ReferenceConfig<T> reference = new ReferenceConfig<>();
        reference.setInterface(clazz);
        reference.setGroup(group);
        CrpcBootstrap.getInstance().reference(reference, group);
        T t = reference.get();
        cache.put(key, t);
        return t;
    }
}
