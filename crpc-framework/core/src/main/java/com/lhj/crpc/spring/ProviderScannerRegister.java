package com.lhj.crpc.spring;

import com.lhj.crpc.annotation.CrpcApi;
import com.lhj.crpc.annotation.CrpcScan;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.StandardAnnotationMetadata;
import org.springframework.stereotype.Component;

/**
 * @author banyan
 * @createTime：2023-09-01
 **/
@Slf4j
public class ProviderScannerRegister implements ImportBeanDefinitionRegistrar, ResourceLoaderAware {
    private static final String CRPC_BEAN_LOCAL = "com.lhj.crpc";
    private static final String BASE_PACKAGE = "basePackage";
    private ResourceLoader resourceLoader;

    @Override
    public void registerBeanDefinitions(AnnotationMetadata annotationMetadata, BeanDefinitionRegistry beanDefinitionRegistry) {
        AnnotationAttributes rpcScanAnnotationAttributes = AnnotationAttributes
                .fromMap(annotationMetadata.getAnnotationAttributes(CrpcScan.class.getName()));
        String[] rpcScanBasePackages = new String[0];
        if (rpcScanAnnotationAttributes != null) {
            rpcScanBasePackages = rpcScanAnnotationAttributes.getStringArray(BASE_PACKAGE);
        }
        //没有指定就直接使用类路径
        if (rpcScanBasePackages.length == 0) {
            rpcScanBasePackages = new String[]{((StandardAnnotationMetadata) annotationMetadata).
                    getIntrospectedClass().getPackage().getName()};
        }
        BeanProviderScanner rpcServiceScanner = new BeanProviderScanner(beanDefinitionRegistry, CrpcApi.class);
        BeanProviderScanner springBeanScanner = new BeanProviderScanner(beanDefinitionRegistry, Component.class);
        if (resourceLoader != null) {
            rpcServiceScanner.setResourceLoader(resourceLoader);
            springBeanScanner.setResourceLoader(resourceLoader);
        }
        int springBeanScan = springBeanScanner.scan(CRPC_BEAN_LOCAL);
        int rpcServices = rpcServiceScanner.scan(rpcScanBasePackages);
        log.info("框架注入的bean为:{}个", springBeanScan);
        log.info("开启远程服务的bean的数量{}", rpcServices);
    }
    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;

    }
}
