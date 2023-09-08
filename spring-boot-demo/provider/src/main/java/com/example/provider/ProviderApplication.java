package com.example.provider;

import com.lhj.crpc.CrpcBootstrap;
import com.lhj.crpc.annotation.CrpcScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author banyanmei
 */
@SpringBootApplication
@CrpcScan(basePackage = {"com.example.provider.service"})
public class ProviderApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProviderApplication.class, args);
        CrpcBootstrap.getInstance().start();
    }
}
