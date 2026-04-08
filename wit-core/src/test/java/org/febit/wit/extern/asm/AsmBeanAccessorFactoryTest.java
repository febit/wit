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
package org.febit.wit.extern.asm;

import lombok.Getter;
import lombok.Setter;
import org.febit.wit.exception.ScriptEvaluateException;
import org.febit.wit.runtime.accessor.impl.ReflectBeanAccessor;
import org.febit.wit.runtime.accessor.impl.ToStringRenderer;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AsmBeanAccessorFactoryTest {

    @SuppressWarnings({"unused", "FieldMayBeFinal", "SpellCheckingInspection"})
    public static class Foo {

        public String f1 = "foo:f1";
        @Setter
        @Getter
        private String f2 = "foo:f2";
        public final String f3 = "foo:f3";
        public int f4 = 4;
        @Setter
        @Getter
        private int f5 = 5;
        public String bG = "foo:bG"; // hashcode 3109
        public String af = "foo:af"; // hashcode 3109
        private String unXable = "Unreadable & Unwriteable";
        public final String unwriteable = "Unwriteable";
        @Setter
        private String unreadable = "Unreadable";

    }

    @SuppressWarnings({"unused"})
    private static class PrivateBean {

        public String f1 = "f1";
        @Getter
        @Setter
        private String f2 = "f2";
        public int f4 = 4;
    }

    @Test
    void privateBean() {
        assertThrows(Exception.class,
                () -> AsmBeanAccessorFactory.constructAccessorClass(PrivateBean.class)
                        .getConstructor().newInstance());

        var factory = AsmBeanAccessorFactory.get();
        var getter = assertDoesNotThrow(() -> factory.getter(PrivateBean.class));
        var setter = assertDoesNotThrow(() -> factory.setter(PrivateBean.class));
        var renderer = assertDoesNotThrow(() -> factory.renderer(PrivateBean.class));

        assertInstanceOf(ReflectBeanAccessor.class, getter);
        assertInstanceOf(ReflectBeanAccessor.class, setter);
        assertInstanceOf(ToStringRenderer.class, renderer);

        var bean = new PrivateBean();
        assertEquals("f1", getter.get(new PrivateBean(), "f1"));
        assertEquals("f2", getter.get(new PrivateBean(), "f2"));
        assertEquals(4, getter.get(new PrivateBean(), "f4"));

        setter.set(bean, "f1", "new:f1");
        setter.set(bean, "f2", "new:f2");
        setter.set(bean, "f4", 8);

        assertEquals("new:f1", getter.get(bean, "f1"));
        assertEquals("new:f2", getter.get(bean, "f2"));
        assertEquals(8, getter.get(bean, "f4"));
    }

    @Test
    @SuppressWarnings("SpellCheckingInspection")
    void foo() {
        Foo foo = new Foo();

        var factory = AsmBeanAccessorFactory.get();

        var getter = factory.getter(Foo.class);
        var setter = factory.setter(Foo.class);
        var renderer = factory.renderer(Foo.class);

        assertInstanceOf(AsmBeanAccessor.class, getter);
        assertInstanceOf(AsmBeanAccessor.class, setter);
        assertInstanceOf(ToStringRenderer.class, renderer);

        int i = 0;
        assertEquals("foo:f1", getter.get(foo, "f" + (i + 1)));
        assertEquals("foo:f2", getter.get(foo, "f" + (i + 2)));

        setter.set(foo, "f1", "new:f1");
        setter.set(foo, "f2", "new:f2");
        setter.set(foo, "f4", 8);
        setter.set(foo, "f5", 8);
        setter.set(foo, "bG", "new:bG");
        setter.set(foo, "af", "new:af");

        assertEquals("new:f1", getter.get(foo, "f1"));
        assertEquals("new:f2", getter.get(foo, "f2"));
        assertEquals("foo:f3", getter.get(foo, "f3"));
        assertEquals(8, getter.get(foo, "f4"));
        assertEquals(8, getter.get(foo, "f5"));
        assertEquals("new:bG", getter.get(foo, "bG"));
        assertEquals("new:af", getter.get(foo, "af"));

        Exception exception;

        exception = assertThrows(ScriptEvaluateException.class,
                () -> getter.get(foo, "unreadable"));
        assertEquals("property is not readable: " + Foo.class.getName() + "#unreadable", exception.getMessage());

        exception = assertThrows(ScriptEvaluateException.class,
                () -> setter.set(foo, "unwriteable", "unwriteable"));
        assertEquals("property is not writable: " + Foo.class.getName() + "#unwriteable", exception.getMessage());

        exception = assertThrows(ScriptEvaluateException.class,
                () -> setter.set(foo, "unXable", "unXable"));
        assertEquals("no such property: " + Foo.class.getName() + "#unXable", exception.getMessage());

        exception = assertThrows(ScriptEvaluateException.class,
                () -> getter.get(foo, "unXable"));
        assertEquals("no such property: " + Foo.class.getName() + "#unXable", exception.getMessage());
    }
}
