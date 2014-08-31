// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script.util.bean;

import static org.junit.Assert.assertEquals;
import org.junit.Test;
import webit.script.util.bean.FieldInfoResolver;

/**
 *
 * @author Zqq
 */
public class FieldInfoResolverTest {

    public static class FooParent {

        public static String publicStatic0;
        private static String privateStatic0;
        public final String public0 = "";
        private String private0;
        protected String protected0;

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
    }

    public static class Foo extends FooParent {

        public static String publicStatic;

    }

    @Test
    public void test() {
        FieldInfoResolver.resolve(Foo.class);
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
