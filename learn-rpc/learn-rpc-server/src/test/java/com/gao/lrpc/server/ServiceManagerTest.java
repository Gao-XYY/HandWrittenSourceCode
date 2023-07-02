package com.gao.lrpc.server;

import com.gao.lrpc.Request;
import com.gao.lrpc.ServiceDescriptor;
import com.gao.lrpc.common.utils.ReflectionUtils;
import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Method;

import static org.junit.Assert.assertNotNull;

public class ServiceManagerTest  {

    ServiceManager sm;

    @Before
    public void init(){
        sm = new ServiceManager();
//        TestInterface bean = new TestClass();
//
//        sm.register(TestInterface.class, bean);
    }

    @Test
    public void testRegister() {
        TestInterface bean = new TestClass();

        sm.register(TestInterface.class, bean);

    }

    public void testLookUp() {

        Method method = ReflectionUtils.getPublicMethods(TestInterface.class)[0];
        ServiceDescriptor sdp = ServiceDescriptor.from(TestInterface.class, method);
        Request request = new Request();
        request.setService(sdp);
        ServiceInstance sis = sm.lookUp(request);
        assertNotNull(sis);

    }
}