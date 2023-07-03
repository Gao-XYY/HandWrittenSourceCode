package com.learn.service;

import com.spring.Autowired;
import com.spring.BeanNameAware;
import com.spring.Component;
import com.spring.Scope;

@Component("userService")
//@Scope("prototype")
public class UserService implements BeanNameAware {

    @Autowired
    private OrderService orderService;

    private String beanName;

    public void test(){
        System.out.println(orderService);
        System.out.println(beanName);
    }

    @Override
    public void setBeanName(String name) {
        beanName = name;
    }
}
