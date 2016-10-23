// Copyright (c) 2013-2016, febit.org. All Rights Reserved.
package org.febit.wit.util.bean;

import java.util.HashMap;
import java.util.Map;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author zqq90
 */
public class FieldInfoResolverTest {

    public static class FooParent {

        public static String publicStatic0;
        private static String privateStatic0;
        public final String public0 = "";
        private String private0;
        protected String protected0;
        protected String protected1;

        public String publicWithGetterSetter0;

        private String privateWithGetter0;
        private String privateWithSetter0;
        private String privateWithGetterSetter0;

        public String getPrivateWithGetter0() {
            return privateWithGetter0;
        }

        public String getPrivateWithGetterSetter0() {
            return privateWithGetterSetter0;
        }

        public void setPrivateWithSetter0(String privateWithSetter0) {
            this.privateWithSetter0 = privateWithSetter0;
        }

        public void setPrivateWithGetterSetter0(String privateWithGetterSetter0) {
            this.privateWithGetterSetter0 = privateWithGetterSetter0;
        }

        public String getPublicWithGetterSetter0() {
            return publicWithGetterSetter0;
        }

        public void setPublicWithGetterSetter0(String publicWithGetterSetter0) {
            this.publicWithGetterSetter0 = publicWithGetterSetter0;
        }

        public String getMethodField0() {
            return null;
        }

        private String getMethodField1() {
            return null;
        }

        protected String getMethodField2() {
            return null;
        }

        protected String getMethodField3() {
            return null;
        }

        public void setMethodField3(Object obj) {
        }
    }

    public static class Foo extends FooParent {

        public static String publicStatic;
        private String private2;

        public String getProtected1() {
            return protected1;
        }

        protected String getPrivate2() {
            return private2;
        }

        void setPrivate2(String private2) {
            this.private2 = private2;
        }

        public String getMethodField4() {
            return null;
        }
    }

    @Test
    public void test() {

        FieldInfo[] infos = FieldInfoResolver.resolve(Foo.class);

        Map<String, FieldInfo> infoMap = new HashMap<>();
        for (FieldInfo fieldInfo : infos) {
            infoMap.put(fieldInfo.name, fieldInfo);
        }
        assertNull(infoMap.get("publicStatic"));
        assertNull(infoMap.get("publicStatic0"));
        assertNull(infoMap.get("privateStatic0"));
        assertNull(infoMap.get("private0"));
        assertNull(infoMap.get("private2"));
        assertNull(infoMap.get("protected0"));
        assertNull(infoMap.get("methodField1"));
        assertNull(infoMap.get("methodField2"));

        assertNotNull(infoMap.get("public0"));
        assertNotNull(infoMap.get("protected1"));
        assertNotNull(infoMap.get("publicWithGetterSetter0"));
        assertNotNull(infoMap.get("privateWithGetter0"));
        assertNotNull(infoMap.get("privateWithSetter0"));
        assertNotNull(infoMap.get("privateWithGetterSetter0"));
        assertNotNull(infoMap.get("methodField0"));
        assertNotNull(infoMap.get("methodField3"));
        assertNotNull(infoMap.get("methodField4"));

        FieldInfo info;

        info = infoMap.get("public0");
        assertNotNull(info.getField());
        assertNull(info.getGetter());
        assertNull(info.getSetter());

        info = infoMap.get("publicWithGetterSetter0");
        assertNotNull(info.getField());
        assertNotNull(info.getGetter());
        assertNotNull(info.getSetter());

        info = infoMap.get("privateWithGetter0");
        assertNull(info.getField());
        assertNotNull(info.getGetter());
        assertNull(info.getSetter());

        info = infoMap.get("privateWithSetter0");
        assertNull(info.getField());
        assertNull(info.getGetter());
        assertNotNull(info.getSetter());

        info = infoMap.get("privateWithGetterSetter0");
        assertNull(info.getField());
        assertNotNull(info.getGetter());
        assertNotNull(info.getSetter());

        info = infoMap.get("protected1");
        assertNull(info.getField());
        assertNotNull(info.getGetter());
        assertNull(info.getSetter());

        info = infoMap.get("methodField0");
        assertNull(info.getField());
        assertNotNull(info.getGetter());
        assertNull(info.getSetter());

        info = infoMap.get("methodField3");
        assertNull(info.getField());
        assertNull(info.getGetter());
        assertNotNull(info.getSetter());

        info = infoMap.get("methodField4");
        assertNull(info.getField());
        assertNotNull(info.getGetter());
        assertNull(info.getSetter());
    }

    @Test
    public void cutFieldNameTest() {
        assertEquals("a", FieldInfoResolver.cutFieldName("getA", 3));
        assertEquals("ab", FieldInfoResolver.cutFieldName("getAb", 3));
        assertEquals("AB", FieldInfoResolver.cutFieldName("getAB", 3));
        assertEquals("ABc", FieldInfoResolver.cutFieldName("getABc", 3));
        assertEquals("ABC", FieldInfoResolver.cutFieldName("getABC", 3));
        assertEquals("aB", FieldInfoResolver.cutFieldName("getaB", 3));
    }
}
