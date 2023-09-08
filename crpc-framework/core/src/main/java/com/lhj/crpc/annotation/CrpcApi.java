package com.lhj.crpc.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @description：
 * @createTime：2023-09-0210:04
 * @author：banyanmei
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface CrpcApi {

    String group() default "default";

}
