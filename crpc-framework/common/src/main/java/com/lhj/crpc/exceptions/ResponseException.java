package com.lhj.crpc.exceptions;

/**
 * @description：
 * @createTime：2023-09-0210:05
 * @author：banyanmei
 */
public class ResponseException extends RuntimeException {
    
    private byte code;
    private String msg;
    
    public ResponseException(byte code, String msg) {
        super(msg);
        this.code = code;
        this.msg = msg;
    }
}
