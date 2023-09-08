package com.lhj.crpc.exceptions;

/**
 * @description：
 * @createTime：2023-09-0210:05
 * @author：banyanmei
 */
public class CompressException extends RuntimeException{
    
    public CompressException() {
    }
    
    public CompressException(String message) {
        super(message);
    }
    
    public CompressException(Throwable cause) {
        super(cause);
    }
}
