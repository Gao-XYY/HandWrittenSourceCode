package com.learn;

import com.spring.LearnApplicationContext;

public class Test {

    public static void main(String[] args) {
        LearnApplicationContext applicationContext = new LearnApplicationContext(AppConfig.class);


        System.out.println(applicationContext.getBean("userService"));
        System.out.println(applicationContext.getBean("userService"));
        System.out.println(applicationContext.getBean("userService"));

    }

}
