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

import org.febit.wit.engine.accessor.Getter;
import org.febit.wit.engine.accessor.Setter;
import org.febit.wit.exception.ScriptEvaluateException;
import org.febit.wit.runtime.accessor.ReflectBeanAccessor;
import org.febit.wit.runtime.accessor.ToStringRenderer;
import org.febit.wit.util.bean.model.Bar;
import org.febit.wit.util.bean.model.ModelSupport;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AsmBeanAccessorFactoryTest {

    @Test
    @SuppressWarnings("unchecked")
    void internalBean() throws Exception {
        var bean = ModelSupport.internalBean();
        var cls = bean.getClass();

        assertThrows(Exception.class,
                () -> AsmBeanAccessorFactory.constructAccessorClass(cls)
                        .getConstructor().newInstance());

        var factory = AsmBeanAccessorFactory.get();
        var getter = (Getter<Object>) assertDoesNotThrow(() -> factory.getter(cls));
        var setter = (Setter<Object>) assertDoesNotThrow(() -> factory.setter(cls));
        var renderer = assertDoesNotThrow(() -> factory.renderer(cls));

        assertInstanceOf(ReflectBeanAccessor.class, getter);
        assertInstanceOf(ReflectBeanAccessor.class, setter);
        assertInstanceOf(ToStringRenderer.class, renderer);

        assertEquals("f1", getter.get(bean, "f1"));
        assertEquals("f2", getter.get(bean, "f2"));
        assertEquals(4, getter.get(bean, "f4"));

        setter.set(bean, "f1", "new:f1");
        setter.set(bean, "f2", "new:f2");
        setter.set(bean, "f4", 8);

        assertEquals("new:f1", getter.get(bean, "f1"));
        assertEquals("new:f2", getter.get(bean, "f2"));
        assertEquals(8, getter.get(bean, "f4"));
    }

    @Test
    @SuppressWarnings("SpellCheckingInspection")
    void bar() {
        Bar bar = new Bar();

        var factory = AsmBeanAccessorFactory.get();

        var getter = factory.getter(Bar.class);
        var setter = factory.setter(Bar.class);
        var renderer = factory.renderer(Bar.class);

        assertInstanceOf(AsmBeanAccessor.class, getter);
        assertInstanceOf(AsmBeanAccessor.class, setter);
        assertInstanceOf(ToStringRenderer.class, renderer);

        int i = 0;
        assertEquals("foo:f1", getter.get(bar, "f" + (i + 1)));
        assertEquals("foo:f2", getter.get(bar, "f" + (i + 2)));

        setter.set(bar, "f1", "new:f1");
        setter.set(bar, "f2", "new:f2");
        setter.set(bar, "f4", 8);
        setter.set(bar, "f5", 8);
        setter.set(bar, "bG", "new:bG");
        setter.set(bar, "af", "new:af");

        assertEquals("new:f1", getter.get(bar, "f1"));
        assertEquals("new:f2", getter.get(bar, "f2"));
        assertEquals("foo:f3", getter.get(bar, "f3"));
        assertEquals(8, getter.get(bar, "f4"));
        assertEquals(8, getter.get(bar, "f5"));
        assertEquals(true, getter.get(bar, "bool"));
        assertEquals(false, getter.get(bar, "boolean"));

        assertEquals("bG".hashCode(), "af".hashCode());
        assertEquals("new:bG", getter.get(bar, "bG"));
        assertEquals("new:af", getter.get(bar, "af"));

        Exception exception;

        exception = assertThrows(ScriptEvaluateException.class,
                () -> getter.get(bar, "unreadable"));
        assertEquals("property is not readable: " + Bar.class.getName() + "#unreadable", exception.getMessage());

        exception = assertThrows(ScriptEvaluateException.class,
                () -> setter.set(bar, "unwriteable", "unwriteable"));
        assertEquals("property is not writable: " + Bar.class.getName() + "#unwriteable", exception.getMessage());

        exception = assertThrows(ScriptEvaluateException.class,
                () -> setter.set(bar, "unXable", "unXable"));
        assertEquals("no such property: " + Bar.class.getName() + "#unXable", exception.getMessage());

        exception = assertThrows(ScriptEvaluateException.class,
                () -> getter.get(bar, "unXable"));
        assertEquals("no such property: " + Bar.class.getName() + "#unXable", exception.getMessage());
    }
}
