/*
 * Copyright 2013-present febit.org (support@febit.org)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.febit.wit.util.bean;

import lombok.Getter;
import lombok.Setter;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.febit.wit.util.bean.BeanProperties.resolveNameFromMethod;
import static org.junit.jupiter.api.Assertions.*;

class BeanPropertiesTest {

    @Test
    void testResolveNameFromMethod() {
        assertEquals("a", resolveNameFromMethod("getA", 3));
        assertEquals("ab", resolveNameFromMethod("getAb", 3));
        assertEquals("AB", resolveNameFromMethod("getAB", 3));
        assertEquals("ABc", resolveNameFromMethod("getABc", 3));
        assertEquals("ABC", resolveNameFromMethod("getABC", 3));
        assertEquals("aB", resolveNameFromMethod("getaB", 3));
    }

    @Test
    void user() {
        var mapped = BeanProperties.introspect(User.class)
                .collect(Collectors.toMap(
                        BeanProperty::name,
                        property -> property
                ));

        assertThat(mapped)
                .containsKeys(
                        "name",
                        "age",
                        "enabledRef",
                        "statusRef"
                )
                .containsKeys(
                        "enabled"
                )
                .doesNotContainKeys(
                        "isEnabled",
                        "setEnabled",
                        "getId",
                        "code",
                        "status"
                )
        ;

        assertThat(mapped.get("name"))
                .isNotNull()
                .satisfies(info -> {
                    assertNull(info.field());
                    assertNotNull(info.getterMethod());
                    assertNull(info.setterMethod());
                });

        assertThat(mapped.get("age"))
                .isNotNull()
                .satisfies(info -> {
                    assertNull(info.field());
                    assertNotNull(info.getterMethod());
                    assertNull(info.setterMethod());
                });

        assertThat(mapped.get("enabledRef"))
                .isNotNull()
                .satisfies(info -> {
                    assertNull(info.field());
                    assertNotNull(info.getterMethod());
                    assertNull(info.setterMethod());
                });

        assertThat(mapped.get("statusRef"))
                .isNotNull()
                .satisfies(info -> {
                    assertNull(info.field());
                    assertNotNull(info.getterMethod());
                    assertNull(info.setterMethod());
                });

        assertThat(mapped.get("enabled"))
                .isNotNull()
                .satisfies(info -> {
                    assertNull(info.field());
                    assertNotNull(info.getterMethod());
                    assertNotNull(info.setterMethod());
                });
    }

    @Test
    void foo() {
        var mapped = BeanProperties.introspect(Foo.class)
                .collect(Collectors.toMap(
                        BeanProperty::name,
                        property -> property
                ));

        assertThat(mapped)
                .doesNotContainKeys(
                        "publicStatic",
                        "publicStatic0",
                        "privateStatic0",
                        "private0",
                        "private2",
                        "protected0",
                        "methodField1",
                        "methodField2"
                )
        ;

        assertThat(mapped.get("public0"))
                .isNotNull()
                .satisfies(info -> {
                    assertNotNull(info.field());
                    assertNull(info.getterMethod());
                    assertNull(info.setterMethod());
                });

        assertThat(mapped.get("publicWithGetterSetter0"))
                .isNotNull()
                .satisfies(info -> {
                    assertNotNull(info.field());
                    assertNotNull(info.getterMethod());
                    assertNotNull(info.setterMethod());
                });

        assertThat(mapped.get("privateWithGetter0"))
                .isNotNull()
                .satisfies(info -> {
                    assertNull(info.field());
                    assertNotNull(info.getterMethod());
                    assertNull(info.setterMethod());
                });

        assertThat(mapped.get("privateWithSetter0"))
                .isNotNull()
                .satisfies(info -> {
                    assertNull(info.field());
                    assertNull(info.getterMethod());
                    assertNotNull(info.setterMethod());
                });

        assertThat(mapped.get("privateWithGetterSetter0"))
                .isNotNull()
                .satisfies(info -> {
                    assertNull(info.field());
                    assertNotNull(info.getterMethod());
                    assertNotNull(info.setterMethod());
                });

        assertThat(mapped.get("protected1"))
                .isNotNull()
                .satisfies(info -> {
                    assertNull(info.field());
                    assertNotNull(info.getterMethod());
                    assertNull(info.setterMethod());
                });

        assertThat(mapped.get("methodField0"))
                .isNotNull()
                .satisfies(info -> {
                    assertNull(info.field());
                    assertNotNull(info.getterMethod());
                    assertNull(info.setterMethod());
                });

        assertThat(mapped.get("methodField3"))
                .isNotNull()
                .satisfies(info -> {
                    assertNull(info.field());
                    assertNull(info.getterMethod());
                    assertNotNull(info.setterMethod());
                });

        assertThat(mapped.get("methodField4"))
                .isNotNull()
                .satisfies(info -> {
                    assertNull(info.field());
                    assertNotNull(info.getterMethod());
                    assertNull(info.setterMethod());
                });
    }

    @SuppressWarnings("unused")
    public record User(
            String name,
            int age,
            AtomicBoolean enabledRef,
            AtomicReference<String> statusRef
    ) {

        public String code() {
            return name.toUpperCase();
        }

        public boolean isEnabled() {
            return enabledRef.get();
        }

        public void setEnabled(boolean enabled) {
            enabledRef.set(enabled);
        }

        public String status() {
            return statusRef.get();
        }

        public void status(String status) {
            statusRef.set(status);
        }
    }

    @SuppressWarnings("unused")
    public static class FooParent {

        public static String publicStatic0;
        private static String privateStatic0;
        public final String public0 = "";
        private String private0;
        protected String protected0;
        protected String protected1;

        @Setter
        @Getter
        public String publicWithGetterSetter0;
        @Getter
        private String privateWithGetter0;
        @Setter
        private String privateWithSetter0;
        @Setter
        @Getter
        private String privateWithGetterSetter0;

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
            // do nothing
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

}
