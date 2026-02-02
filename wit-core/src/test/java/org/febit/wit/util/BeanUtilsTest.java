// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.util;

import org.febit.wit.util.bean.BeanUtils;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BeanUtilsTest {

    public class Foo {

        public String public0 = "public0";
        public final String public1 = "public1";
        public String public2 = "public2";
        private String private0 = "private0";
        private Class<?> private1 = String.class;
        private boolean bool = false;

        public String getPrivate0() {
            return private0;
        }

        public void setPrivate0(String private0) {
            this.private0 = private0;
        }

        public Class<?> getPrivate1() {
            return private1;
        }

        public void setPrivate1(Class<?> private1) {
            this.private1 = private1;
        }

        public void setPublic2(String public2) {
            this.public2 = "set:" + public2;
        }

        public String getPublic2() {
            return "get:" + public2;
        }

        public boolean isBool() {
            return bool;
        }

        public void setBool(boolean bool) {
            this.bool = bool;
        }
    }

    @Test
    void test() {
        Foo foo = new Foo();

        assertEquals(foo.public0, BeanUtils.get(foo, "public0"));
        assertEquals(foo.public1, BeanUtils.get(foo, "public1"));
        assertEquals(foo.getPublic2(), BeanUtils.get(foo, "public2"));
        assertEquals(foo.getPrivate0(), BeanUtils.get(foo, "private0"));

        String newStringValue = "new public";
        BeanUtils.set(foo, "public0", newStringValue);
        //BeanUtil.set(foo, "public1", "new public"); Exception
        BeanUtils.set(foo, "private0", newStringValue);

        assertEquals(newStringValue, foo.public0);
        assertEquals(newStringValue, foo.getPrivate0());

        assertEquals(foo.isBool(), BeanUtils.get(foo, "bool"));

        foo.setBool(false);
        BeanUtils.set(foo, "bool", true);
        assertTrue(foo.isBool());

    }
}
