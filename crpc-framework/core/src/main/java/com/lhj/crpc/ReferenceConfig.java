package com.lhj.crpc;


import com.lhj.crpc.proxy.handler.RpcConsumerInvocationHandler;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

/**
 * @description：
 * @createTime：2023-09-0117:12
 * @author：banyanmei
 */
@Slf4j
public class ReferenceConfig<T> {
    
    private Class<T> interfaceRef;


    @Getter
    @Setter
    private String group = "default";
    
    public void setInterface(Class<?> interfaceRef) {
        this.interfaceRef = (Class<T>) interfaceRef;
    }
    
    /**
     * 代理设计模式，生成一个api接口的代理对象，helloYrpc.sayHi("你好");
     *
     * @return 代理对象
     */
    public T get() {
        // 此处一定是使用动态代理完成了一些工作
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        Class<T>[] classes = new Class[]{interfaceRef};
        //  代理对象的实现
        InvocationHandler handler = new RpcConsumerInvocationHandler(interfaceRef,group);
        
        //  使用动态代理生成代理对象
        Object helloProxy = Proxy.newProxyInstance(classLoader, classes, handler);
        
        return (T) helloProxy;
    }
    
    
    public Class<T> getInterface() {
        return interfaceRef;
    }
    
    public void setInterfaceRef(Class<T> interfaceRef) {
        this.interfaceRef = interfaceRef;
    }


}
