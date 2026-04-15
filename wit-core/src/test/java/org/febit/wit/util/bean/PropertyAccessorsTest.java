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
import org.febit.wit.util.bean.model.ModelSupport;
import org.febit.wit.util.bean.model.User;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings({
        "java:S5961", // Test methods should not contain too many assertions
})
class PropertyAccessorsTest {

    @Test
    void clazz() {
        var cls = String.class;
        var accessors = PropertyAccessors.of(Class.class);

        var accessor = accessors.get("name");
        assertNotNull(accessor);
        var getter = accessor.getter();
        assertNotNull(getter);
        assertNull(accessor.setter());
        assertSame(cls.getName(), getter.get(cls));
    }

    @Test
    void internalBean() {
        var bean = ModelSupport.internalBean();
        var cls = bean.getClass();

        var accessors = PropertyAccessors.of(cls);

        var assessor = accessors.get("f1");
        assertNotNull(assessor);
        var getter = assessor.getter();
        var setter = assessor.setter();
        assertNotNull(getter);
        assertNotNull(setter);
        assertEquals("f1", getter.get(bean));
        setter.set(bean, "new:f1");
        assertEquals("new:f1", getter.get(bean));
    }

    @Test
    void user() {
        var accessors = PropertyAccessors.of(User.class);
        var user = User.builder()
                .name("foo")
                .age(18)
                .enabledRef(new AtomicBoolean(true))
                .statusRef(new AtomicReference<>("active"))
                .build();

        var accessor = accessors.get("enabled");
        assertNotNull(accessor);
        var getter = accessor.getter();
        var setter = accessor.setter();
        assertNotNull(getter);
        assertNotNull(setter);
        assertEquals(true, getter.get(user));
        setter.set(user, false);
        assertEquals(false, getter.get(user));

        accessor = accessors.get("status");
        assertNull(accessor);

        accessor = accessors.get("name");
        assertNotNull(accessor);
        assertNull(accessor.setter());
        getter = accessor.getter();
        assertNotNull(getter);
        assertSame(user.name(), getter.get(user));

        accessor = accessors.get("age");
        assertNotNull(accessor);
        assertNull(accessor.setter());
        getter = accessor.getter();
        assertNotNull(getter);
        assertEquals(user.age(), getter.get(user));

        accessor = accessors.get("statusRef");
        assertNotNull(accessor);
        assertNull(accessor.setter());
        getter = accessor.getter();
        assertNotNull(getter);
        assertSame(user.statusRef(), getter.get(user));
    }

    @Test
    void foo() {
        var foo = new Foo();
        var accessors = PropertyAccessors.of(Foo.class);

        // field - final public
        var accessor = accessors.get("public0");
        assertNotNull(accessor);
        var getter = accessor.getter();
        var setter = accessor.setter();
        assertNotNull(getter);
        assertNull(setter);
        assertEquals(foo.public0, getter.get(foo));

        // field - public
        accessor = accessors.get("publicWithGetterSetter0");
        assertNotNull(accessor);
        getter = accessor.getter();
        setter = accessor.setter();
        assertNotNull(getter);
        assertNotNull(setter);
        assertEquals(foo.publicWithGetterSetter0, getter.get(foo));
        setter.set(foo, "new:public1");
        assertEquals("new:public1", getter.get(foo));

        // Method - privateWithGetter0
        accessor = accessors.get("privateWithGetter0");
        assertNotNull(accessor);
        getter = accessor.getter();
        setter = accessor.setter();
        assertNotNull(getter);
        assertNull(setter);
        assertEquals(foo.getPrivateWithGetter0(), getter.get(foo));

        // Method - privateWithSetter0
        accessor = accessors.get("privateWithSetter0");
        assertNotNull(accessor);
        getter = accessor.getter();
        setter = accessor.setter();
        assertNull(getter);
        assertNotNull(setter);
        setter.set(foo, "new:privateWithSetter0");

        // Method - privateWithGetterSetter0
        accessor = accessors.get("privateWithGetterSetter0");
        assertNotNull(accessor);
        getter = accessor.getter();
        setter = accessor.setter();
        assertNotNull(getter);
        assertNotNull(setter);
        assertEquals(foo.getPrivateWithGetterSetter0(), getter.get(foo));
        setter.set(foo, "new:privateWithGetterSetter0");
        assertEquals("new:privateWithGetterSetter0", getter.get(foo));

        // Method - protected1
        accessor = accessors.get("protected1");
        assertNotNull(accessor);
        getter = accessor.getter();
        setter = accessor.setter();
        assertNotNull(getter);
        assertNull(setter);
        assertEquals(foo.getProtected1(), getter.get(foo));

        // Method - methodField0
        accessor = accessors.get("methodField0");
        assertNotNull(accessor);
        getter = accessor.getter();
        setter = accessor.setter();
        assertNotNull(getter);
        assertNull(setter);
        assertEquals(foo.getMethodField0(), getter.get(foo));

        // Method - methodField3
        accessor = accessors.get("methodField3");
        assertNotNull(accessor);
        getter = accessor.getter();
        setter = accessor.setter();
        assertNull(getter);
        assertNotNull(setter);

        // Method - methodField4
        accessor = accessors.get("methodField4");
        assertNotNull(accessor);
        getter = accessor.getter();
        setter = accessor.setter();
        assertNotNull(getter);
        assertNull(setter);
    }

}
