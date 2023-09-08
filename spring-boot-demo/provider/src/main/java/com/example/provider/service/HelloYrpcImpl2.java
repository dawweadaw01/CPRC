package com.example.provider.service;

import com.lhj.crpc.annotation.CrpcApi;
import com.lhj.crpc.api.HelloCrpc;


/**@createtime 2023-09-01 17:07
 * @author banyanmei
 */

@CrpcApi(group = "primary")
public class HelloYrpcImpl2 implements HelloCrpc {
    @Override
    public String sayHi(String msg) {
        return "primary" + msg;
    }
}
