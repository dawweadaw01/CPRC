package com.lhj.crpc.annotation;

import com.lhj.crpc.spring.ProviderScannerRegister;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @description：
 * @createTime：2023-09-0816:59
 * @author：banyanmei
 */

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(ProviderScannerRegister.class)
public @interface CrpcScan {
    String[] basePackage();
}
