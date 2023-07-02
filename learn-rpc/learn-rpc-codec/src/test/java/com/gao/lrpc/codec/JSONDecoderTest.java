package com.gao.lrpc.codec;

import junit.framework.TestCase;
import org.junit.Test;

public class JSONDecoderTest extends TestCase {

    public void testDecode() {

        JSONEncoder encoder = new JSONEncoder();

        TestBean bean = new TestBean();
        bean.setName("高高");
        bean.setAge(18);

        byte[] bytes = encoder.encode(bean);

        Decoder decoder = new JSONDecoder();
        TestBean bean2 = decoder.decode(bytes, TestBean.class);

        assertEquals(bean.getName(), bean2.getName());
        assertEquals(bean.getAge(), bean2.getAge());


    }
}