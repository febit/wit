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

import org.febit.wit.engine.nativex.support.MethodInvoker;
import org.febit.wit.exception.ScriptEvaluateException;
import org.febit.wit.exception.UncheckedException;
import org.febit.wit.runtime.Undefined;
import org.febit.wit.util.bean.model.Bar;
import org.febit.wit.util.bean.model.Foo;
import org.febit.wit.util.bean.model.ModelSupport;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.febit.wit.extern.asm.AsmNativeFunctionFactory.construct;
import static org.febit.wit.ir.IRTestSupport.DUMMY_CONTEXT;
import static org.febit.wit.ir.IRTestSupport.args;
import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings({
        "unchecked",
        "java:S117", // naming convention
})
class AsmNativeFunctionFactoryTest {

    final AsmNativeFunctionFactory factory = AsmNativeFunctionFactory.create();

    @Test
    void testFactory() throws NoSuchMethodException {
        assertNotNull(factory);

        var str_toString = factory.method(String.class.getMethod("toString"));
        assertInstanceOf(AsmWitFunction.class, str_toString);
        assertEquals("aaa", str_toString.apply(DUMMY_CONTEXT, args("aaa")));

        var newList = factory.constructor(ArrayList.class.getConstructor());
        assertInstanceOf(AsmWitFunction.class, newList);

        assertThat((List<Object>) newList.apply(DUMMY_CONTEXT, null))
                .isNotNull()
                .isInstanceOf(ArrayList.class)
                .isEmpty();
    }

    @Test
    void testFactoryWithInternalBean() throws NoSuchMethodException {
        var bean = ModelSupport.internalBean();
        var beanClass = bean.getClass();

        var newBean = factory.constructor(beanClass.getConstructor());
        assertInstanceOf(MethodInvoker.class, newBean);
        assertThat(newBean.apply(DUMMY_CONTEXT, null))
                .isNotNull()
                .isInstanceOf(beanClass);

        var toStringMethod = beanClass.getMethod("toString");
        var toString = factory.method(toStringMethod);
        assertEquals(Object.class, toStringMethod.getDeclaringClass());
        assertInstanceOf(AsmWitFunction.class, toString);
        assertEquals(bean.toString(), toString.apply(DUMMY_CONTEXT, args(bean)));

        var m1Method = beanClass.getMethod("m1");
        var m1 = factory.method(m1Method);
        assertEquals(beanClass, m1Method.getDeclaringClass());
        assertInstanceOf(MethodInvoker.class, m1);
        assertEquals("m1", m1.apply(DUMMY_CONTEXT, args(bean)));
    }

    @Test
    void testFactoryWithInternalMethod() throws NoSuchMethodException {
        var getPrivate2Method = Foo.class.getDeclaredMethod("getPrivate2");
        assertThatThrownBy(() -> factory.method(getPrivate2Method))
                .isInstanceOf(UncheckedException.class)
                .cause()
                .isInstanceOf(IllegalAccessException.class);
    }

    @Test
    void testToString() throws Exception {
        var str_toString = construct(String.class.getMethod("toString"));
        assertInstanceOf(AsmWitFunction.class, str_toString);

        assertEquals("aaa", str_toString.apply(args("aaa")));
    }

    @Test
    void currentTimeMillis() throws Exception {
        var currentTimeMillis = construct(System.class.getMethod("currentTimeMillis"));

        assertInstanceOf(AsmWitFunction.class, currentTimeMillis);

        assertInstanceOf(Long.class, currentTimeMillis.apply(null));
        assertInstanceOf(Long.class, currentTimeMillis.apply(new Object[0]));
        assertInstanceOf(Long.class, currentTimeMillis.apply(args(1, 2, 3)));
    }

    @Test
    void arraycopy() throws Exception {
        var arraycopy = construct(
                System.class.getMethod("arraycopy", Object.class, int.class, Object.class, int.class, int.class));

        assertInstanceOf(AsmWitFunction.class, arraycopy);

        int[] array1 = {1, 2, 3, 4, 6};
        int[] array2 = new int[array1.length];

        assertEquals(Undefined.UNDEFINED,
                arraycopy.apply(args(array1, 0, array2, 0, array1.length)));
        assertArrayEquals(array1, array2);
    }

    @Test
    void list() throws Exception {
        var newList = construct(ArrayList.class.getConstructor());
        var newListWithInitSize = construct(ArrayList.class.getConstructor(int.class));

        var listAdd = construct(List.class.getMethod("add", Object.class));
        var listAddToIndex = construct(List.class.getMethod("add", int.class, Object.class));
        var listSize = construct(ArrayList.class.getMethod("size"));

        assertInstanceOf(AsmWitFunction.class, newList);
        assertInstanceOf(AsmWitFunction.class, newListWithInitSize);
        assertInstanceOf(AsmWitFunction.class, listAdd);
        assertInstanceOf(AsmWitFunction.class, listAddToIndex);
        assertInstanceOf(AsmWitFunction.class, listSize);

        var list = (List<Object>) newList.apply(null);
        var list2 = (List<Object>) newListWithInitSize.apply(args(2));

        assertNotNull(list);
        assertNotNull(list2);

        list.add("i1");
        list.add("i2");

        assertThrows(ScriptEvaluateException.class,
                () -> listSize.apply(new Object[0]));

        assertEquals(2, listSize.apply(args(list)));
        assertEquals(2, listSize.apply(args(list, 2, 3)));

        assertEquals(true, listAdd.apply(args(list, "i3")));
        assertEquals(3, list.size());
        assertEquals("i3", list.get(2));

        assertEquals(true, listAdd.apply(args(list)));
        assertNull(list.get(3));

        assertEquals(Undefined.UNDEFINED, listAddToIndex.apply(args(list, 2, "a3")));
        assertEquals("a3", list.get(2));

        assertEquals(Undefined.UNDEFINED, listAddToIndex.apply(args(list, 2)));
        assertNull(list.get(2));
    }

    @Test
    void cannotConstructField() throws NoSuchFieldException {
        var field = Foo.class.getField("publicStatic");
        assertThatThrownBy(() -> construct(field))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Unsupported member type:");
    }

    @Test
    void constructInvokeVirtual0() throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        var method = Foo.class.getMethod("getProtected1");
        var func = construct(method);
        assertInstanceOf(AsmWitFunction.class, func);
        var bean = new Foo();
        assertEquals(bean.getProtected1(), func.apply(args(bean)));
    }

    @Test
    void constructInvokeVirtual1() throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        var method = Bar.class.getMethod("setF2", String.class);
        var func = construct(method);
        assertInstanceOf(AsmWitFunction.class, func);
        var bar = new Bar();
        assertEquals(Undefined.UNDEFINED, func.apply(args(bar, "v2")));
        assertEquals("v2", bar.getF2());
    }

    @Test
    void constructInvokeInterface0() throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        var method = List.class.getMethod("size");
        var func = construct(method);
        assertInstanceOf(AsmWitFunction.class, func);
        var list = new ArrayList<>();
        assertEquals(0, func.apply(args(list)));
    }
}
