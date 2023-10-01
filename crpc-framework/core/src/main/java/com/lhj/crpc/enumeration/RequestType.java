package com.lhj.crpc.enumeration;

/**
 * 用来标记请求类型
 *
 * @author banyan
 * @createTime 2023-07-22
 */
public enum RequestType {

    // 请求类型
    
    REQUEST((byte)1,"普通请求"), HEART_BEAT((byte)2,"心跳检测请求");
    
    private final byte id;
    private final String type;
    
    RequestType(byte id, String type) {
        this.id = id;
        this.type = type;
    }

    public byte getId() {
        return id;
    }

    public String getType() {
        return type;
    }
}
