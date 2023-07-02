package com.gao.lrpc.server;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.lang.reflect.Method;

/**
 * 表示一个具体的服务
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ServiceInstance {

    /**
     *  这个服务由那个对象提供的
     */
    private Object target;
    /**
     * 这个对象的那个方法暴露成一个服务
     */
    private Method method;

}
