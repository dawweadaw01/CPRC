package com.lhj.crpc.exceptions;

/**
 * @description：
 * @createTime：2023-09-0210:05
 * @author：banyanmei
 */
public class SerializeException extends RuntimeException{
    
    public SerializeException() {
    }
    
    public SerializeException(String message) {
        super(message);
    }
    
    public SerializeException(Throwable cause) {
        super(cause);
    }
}
