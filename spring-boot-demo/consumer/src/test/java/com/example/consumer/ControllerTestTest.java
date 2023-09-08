package com.example.consumer;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @description：
 * @createTime：2023-09-0818:24
 * @author：banyanmei
 */

@SpringBootTest
class ControllerTestTest {

    @Autowired
    private ControllerTest controllerTest;

    @Test
    void test() {
        System.out.println(controllerTest.sayHi("banyanmei"));
    }

    @Test
    void test2(){
        System.out.println(controllerTest.sayHi2("banyanmei"));
    }
}