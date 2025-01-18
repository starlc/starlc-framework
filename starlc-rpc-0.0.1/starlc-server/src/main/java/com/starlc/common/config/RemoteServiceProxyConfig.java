package com.starlc.common.config;

import com.starlc.common.spring.RemoteServiceProxyScanner;

import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

@Configuration
public class RemoteServiceProxyConfig implements ImportBeanDefinitionRegistrar {

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        RemoteServiceProxyScanner scanner = new RemoteServiceProxyScanner(registry);
        scanner.scan("com.starlc.service");
    }
}
