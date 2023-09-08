package com.lhj.impl;

import com.lhj.crpc.annotation.CrpcApi;
import com.lhj.crpc.api.HelloCrpc;


/**@createtime 2023-09-01 17:07
 * @author banyanmei
 */

@CrpcApi
public class HelloYrpcImpl implements HelloCrpc {
    @Override
    public String sayHi(String msg) {
        return "default" + msg;
    }
}
