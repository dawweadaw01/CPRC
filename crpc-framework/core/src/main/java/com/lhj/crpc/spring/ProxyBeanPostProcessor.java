package com.lhj.crpc.spring;

import com.lhj.crpc.CrpcBootstrap;
import com.lhj.crpc.ReferenceConfig;
import com.lhj.crpc.ServiceConfig;
import com.lhj.crpc.annotation.CrpcApi;
import com.lhj.crpc.annotation.CrpcReference;
import com.lhj.crpc.proxy.CrpcProxyFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;

/**
 * @description：
 * @createTime：2023-09-0817:22
 * @author：banyanmei
 */
@Component
public class ProxyBeanPostProcessor implements BeanPostProcessor {



    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if(bean.getClass().isAnnotationPresent(CrpcApi.class)){
            CrpcApi annotation = bean.getClass().getAnnotation(CrpcApi.class);
            String group = annotation.group();
            ServiceConfig<?> server = ServiceConfig.builder().
                    group(group).
                    ref(bean).
                    interfaceProvider(bean.getClass().getInterfaces()[0]).build();
            CrpcBootstrap.getInstance().publish(server);
        }
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Class<?> targetClass = bean.getClass();
        Field[] declaredFields = targetClass.getDeclaredFields();
        for (Field declaredField : declaredFields) {
            CrpcReference annotation = declaredField.getAnnotation(CrpcReference.class);
            if(annotation!= null){
                Class<?> type = declaredField.getType();
                String group = annotation.group();
                Object proxy = CrpcProxyFactory.getProxy(type, group);
                declaredField.setAccessible(true);
                try {
                    declaredField.set(bean, proxy);
                }catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        return bean;
    }
}
