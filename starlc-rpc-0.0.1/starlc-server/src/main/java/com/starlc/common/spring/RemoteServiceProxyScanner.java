package com.starlc.common.spring;

import com.starlc.common.annotation.RemoteService;

import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.filter.AnnotationTypeFilter;

import java.util.Set;


public class RemoteServiceProxyScanner {
    private final BeanDefinitionRegistry registry;

    public RemoteServiceProxyScanner(BeanDefinitionRegistry registry) {
        this.registry = registry;
    }

    public void scan(String basePackage){
        //扫描remoteService注解
        ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false){
            @Override
            protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition)  {
                // 忽略 metadata.isConcrete() 检查，允许扫描接口和抽象类
                AnnotationMetadata metadata = beanDefinition.getMetadata();
                return metadata.hasAnnotation(RemoteService.class.getName());
            }
        };
        scanner.addIncludeFilter(new AnnotationTypeFilter(RemoteService.class));

        Set<BeanDefinition> components = scanner.findCandidateComponents(basePackage);
        if (components.isEmpty()) {
            System.out.println("No components found.");
        }
        components.forEach(beanDefinition -> {
                    String className = beanDefinition.getBeanClassName();
            try {
                Class<?> clazz = Class.forName(className);
                BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(
                        RemoteServiceFactoryBean.class);
                builder.addConstructorArgValue(clazz);
                registry.registerBeanDefinition(clazz.getSimpleName(),builder.getBeanDefinition());

            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
