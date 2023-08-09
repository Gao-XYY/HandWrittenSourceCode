package com.learn.service;

import com.spring.*;

@Component("userService")
//@Scope("prototype")
public class UserService /*implements BeanNameAware*/ implements InitializingBean {

    @Autowired
    private OrderService orderService;

    private String beanName;

    private String name;

    public void setOrderService(OrderService orderService) {
        this.orderService = orderService;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void test(){
        System.out.println(orderService);
//        System.out.println(beanName);
        System.out.println(name);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        System.out.println("初始化.....");
    }

//    @Override
//    public void setBeanName(String name) {
//        beanName = name;
//    }
}
