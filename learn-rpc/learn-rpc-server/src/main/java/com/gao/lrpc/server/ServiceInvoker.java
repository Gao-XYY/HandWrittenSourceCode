package com.gao.lrpc.server;

import com.gao.lrpc.Request;
import com.gao.lrpc.common.utils.ReflectionUtils;
import com.sun.deploy.util.ReflectionUtil;

/**
 * 调用具体服务
 */
public class ServiceInvoker {

    public Object invoke(ServiceInstance service, Request request) {

        return ReflectionUtils.invoke(service.getTarget(), service.getMethod(), request.getParameters());
    }

}
