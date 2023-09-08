package com.lhj.crpc.spring;

import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.core.type.filter.AnnotationTypeFilter;

import java.lang.annotation.Annotation;

/**
 * @author banyan
 * @createTime：2023-09-01
 **/
public class BeanProviderScanner extends ClassPathBeanDefinitionScanner {

    public BeanProviderScanner(BeanDefinitionRegistry registry, Class<? extends Annotation> annoType) {
        super(registry);
        super.addIncludeFilter(new AnnotationTypeFilter(annoType));
    }
    @Override
    public int scan(String... basePackages) {
        return super.scan(basePackages);
    }
}
