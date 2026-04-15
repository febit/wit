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

import org.febit.wit.util.bean.model.Foo;
import org.febit.wit.util.bean.model.User;
import org.junit.jupiter.api.Test;

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

}
