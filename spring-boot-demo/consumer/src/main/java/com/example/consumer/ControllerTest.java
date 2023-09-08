package com.example.consumer;

import com.lhj.crpc.annotation.CrpcReference;
import com.lhj.crpc.api.HelloCrpc;
import org.springframework.stereotype.Component;

/**
 * @description：
 * @createTime：2023-09-0818:22
 * @author：banyanmei
 */

@Component("controllerTest")
public class ControllerTest {

    @CrpcReference
    private HelloCrpc helloCrpc;

    @CrpcReference(group = "primary")
    private HelloCrpc helloCrpc2;

    public String sayHi(String msg){
        return helloCrpc.sayHi(msg);
    }

    public String sayHi2(String msg){
        return helloCrpc2.sayHi(msg);
    }
}
