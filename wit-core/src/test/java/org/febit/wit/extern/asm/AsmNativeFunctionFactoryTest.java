// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.extern.asm;

import org.febit.wit.exception.ScriptEvaluateException;
import org.febit.wit.runtime.InternalContext;
import org.febit.wit.runtime.Undefined;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

@SuppressWarnings({
        "unchecked",
        "java:S117", // naming convention
})
class AsmNativeFunctionFactoryTest {

    private static final String PKG_ASM = AsmNativeFunctionFactory.class.getPackageName();

    @Test
    void testToString() throws Exception {
        var context = mock(InternalContext.class);
        var str_toString = AsmNativeFunctionFactory.construct(String.class.getMethod("toString"));
        assertEquals(PKG_ASM, str_toString.getClass().getPackageName());

        assertEquals("aaa", str_toString.apply(context, new Object[]{"aaa"}));
    }

    @Test
    void currentTimeMillis() throws Exception {
        var context = mock(InternalContext.class);
        var currentTimeMillis = AsmNativeFunctionFactory.construct(System.class.getMethod("currentTimeMillis"));

        assertEquals(PKG_ASM, currentTimeMillis.getClass().getPackageName());

        assertInstanceOf(Long.class, currentTimeMillis.apply(context, null));
        assertInstanceOf(Long.class, currentTimeMillis.apply(context, new Object[0]));
        assertInstanceOf(Long.class, currentTimeMillis.apply(context, new Object[]{1, 2, 3}));
    }

    @Test
    void arraycopy() throws Exception {
        var context = mock(InternalContext.class);
        var arraycopy = AsmNativeFunctionFactory.construct(
                System.class.getMethod("arraycopy", Object.class, int.class, Object.class, int.class, int.class));

        assertEquals(PKG_ASM, arraycopy.getClass().getPackageName());

        int[] array1 = {1, 2, 3, 4, 6};
        int[] array2 = new int[array1.length];

        assertEquals(Undefined.UNDEFINED,
                arraycopy.apply(context, new Object[]{array1, 0, array2, 0, array1.length}));
        assertArrayEquals(array1, array2);
    }

    @Test
    void list() throws Exception {
        var context = mock(InternalContext.class);

        var newList = AsmNativeFunctionFactory.construct(ArrayList.class.getConstructor());
        var newListWithInitSize = AsmNativeFunctionFactory.construct(ArrayList.class.getConstructor(int.class));

        var listAdd = AsmNativeFunctionFactory.construct(List.class.getMethod("add", Object.class));
        var listAddToIndex = AsmNativeFunctionFactory.construct(List.class.getMethod("add", int.class, Object.class));
        var listSize = AsmNativeFunctionFactory.construct(ArrayList.class.getMethod("size"));

        assertEquals(PKG_ASM, newList.getClass().getPackageName());
        assertEquals(PKG_ASM, newListWithInitSize.getClass().getPackageName());
        assertEquals(PKG_ASM, listAdd.getClass().getPackageName());
        assertEquals(PKG_ASM, listAddToIndex.getClass().getPackageName());
        assertEquals(PKG_ASM, listSize.getClass().getPackageName());

        var list = (List<Object>) newList.apply(context, null);
        var list2 = (List<Object>) newListWithInitSize.apply(context, new Object[]{2});

        assertNotNull(list);
        assertNotNull(list2);

        list.add("i1");
        list.add("i2");

        assertThrows(ScriptEvaluateException.class,
                () -> listSize.apply(context, new Object[0]));

        assertEquals(2, listSize.apply(context, new Object[]{list}));
        assertEquals(2, listSize.apply(context, new Object[]{list, 2, 3}));

        assertEquals(true, listAdd.apply(context, new Object[]{list, "i3"}));
        assertEquals(3, list.size());
        assertEquals("i3", list.get(2));

        assertEquals(true, listAdd.apply(context, new Object[]{list}));
        assertNull(list.get(3));

        assertEquals(Undefined.UNDEFINED, listAddToIndex.apply(context, new Object[]{list, 2, "a3"}));
        assertEquals("a3", list.get(2));

        assertEquals(Undefined.UNDEFINED, listAddToIndex.apply(context, new Object[]{list, 2}));
        assertNull(list.get(2));

    }
}
