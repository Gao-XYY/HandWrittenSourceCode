package com.gao.lrpc.codec;

import junit.framework.TestCase;

public class JSONEncoderTest extends TestCase {

    public void testEncode() {

        JSONEncoder encoder = new JSONEncoder();

        TestBean bean = new TestBean();
        bean.setName("高高");
        bean.setAge(18);

        byte[] bytes = encoder.encode(bean);

        assertNotNull(bytes);

    }
}