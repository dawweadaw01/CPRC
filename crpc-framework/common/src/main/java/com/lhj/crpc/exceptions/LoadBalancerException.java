package com.lhj.crpc.exceptions;

/**
 * @description：
 * @createTime：2023-09-0210:05
 * @author：banyanmei
 */
public class LoadBalancerException extends RuntimeException {
    
    public LoadBalancerException(String message) {
        super(message);
    }
    
    public LoadBalancerException() {
    }
}
