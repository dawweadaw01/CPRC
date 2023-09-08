package com.lhj.crpc.exceptions;

/**
 * @description：
 * @createTime：2023-09-0210:05
 * @author：banyanmei
 */
public class NetworkException extends RuntimeException{
    
    public NetworkException() {
    }
    
    public NetworkException(String message) {
        super(message);
    }
    
    public NetworkException(Throwable cause) {
        super(cause);
    }
}
