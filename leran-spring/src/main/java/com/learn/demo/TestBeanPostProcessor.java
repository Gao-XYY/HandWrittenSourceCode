package com.learn.demo;

import com.learn.service.UserService;
import com.spring.BeanPostProcessor;
import com.spring.Component;

@Component
public class TestBeanPostProcessor implements BeanPostProcessor {


    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) {
        System.out.println("初始化前");
        if (beanName.equals("userService")){
            ((UserService)bean).setName("高高");
        }
        return bean;
    }

    @Override
    public Object psotProcessAfterInitialization(Object bean, String beanName) {
        System.out.println("初始化后");
        return bean;
    }
}
