package com.example.consumer;

import com.lhj.crpc.annotation.CrpcScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * @author banyanmei
 */
@SpringBootApplication
@CrpcScan(basePackage = {"com.example.consumer"})
public class ConsumerApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext run = SpringApplication.run(ConsumerApplication.class, args);
        // 得到上下文
        ControllerTest controllerTest = (ControllerTest)run.getBean("controllerTest");
        System.out.println(controllerTest.sayHi("hello"));
        System.out.println(controllerTest.sayHi2("hello"));
    }

}
