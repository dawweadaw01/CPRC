package com.lhj.crpc.proxy;


import com.lhj.crpc.CrpcBootstrap;
import com.lhj.crpc.ReferenceConfig;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author banyan
 * @createTime 2023-07-30
 */
public class CrpcProxyFactory {
    
    private static final Map<Class<?>,Object> cache = new ConcurrentHashMap<>(32);
    
    public static <T> T getProxy(Class<T> clazz, String group) {
    
        Object bean = cache.get(clazz);
        if(bean != null){
            return (T)bean;
        }
    
        ReferenceConfig<T> reference = new ReferenceConfig<>();
        reference.setInterface(clazz);
        reference.setGroup(group);
        CrpcBootstrap.getInstance().reference(reference);
        T t = reference.get();
        cache.put(clazz,t);
        return t;
    }
}
