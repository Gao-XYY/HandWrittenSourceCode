package com.spring;

public interface BeanPostProcessor {

    Object postProcessBeforeInitialization(Object bean, String beanName);

    Object psotProcessAfterInitialization(Object bean, String beanName);

}
