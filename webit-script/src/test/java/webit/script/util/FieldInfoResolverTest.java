// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.util;

import org.junit.Test;

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
    public void test(){
        FieldInfoResolver resolver = new FieldInfoResolver(Foo.class);
        
        FieldInfo[] fieldInfos = resolver.resolver();
    }
}
