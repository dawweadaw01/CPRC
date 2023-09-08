package com.lhj.crpc.discovery;

import com.lhj.crpc.Constant;
import com.lhj.crpc.discovery.impl.ZookeeperRegistry;
import com.lhj.crpc.exceptions.DiscoveryException;

/**
 * @description：注册中心的简单工厂
 * @createTime：2023-09-0122:46
 * @author：banyanmei
 */
public class RegistryConfig {
    private final String connectString;

    public RegistryConfig(String connectString) {
        this.connectString = connectString;
    }

    public Registry getRegistry() {
        // 1、获取注册中心的类型
        String registryType = getRegistryType(connectString,true).toLowerCase().trim();
        // 2、通过类型获取具体注册中心
        if( "zookeeper".equals(registryType) ){
            String host = getRegistryType(connectString, false);
            //  zookeeper注册中心
            return new ZookeeperRegistry(host, Constant.TIME_OUT);
        } else if ("nacos".equals(registryType)){
            String host = getRegistryType(connectString, false);
            //t todo nacos注册中心
            //return new NacosRegistry(host, Constant.TIME_OUT);
        }
        throw new DiscoveryException("未发现合适的注册中心。");
    }


    private String getRegistryType(String connectString,boolean ifType){
        String[] typeAndHost = connectString.split("://");
        if(typeAndHost.length != 2){
            throw new RuntimeException("给定的注册中心连接url不合法");
        }
        if(ifType){
            return typeAndHost[0];
        } else {
            return typeAndHost[1];
        }
    }

}
