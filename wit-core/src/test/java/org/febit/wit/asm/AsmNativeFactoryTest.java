// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.asm;

import org.febit.wit.exceptions.ScriptRuntimeException;
import org.febit.wit.runtime.FunctionDeclare;
import org.febit.wit.runtime.Undefined;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AsmNativeFactoryTest {

    @Test
    @SuppressWarnings("unchecked")
    void test() throws Exception {

        FunctionDeclare str_toString = AsmNativeFactory.createAccessor(String.class.getMethod("toString"));

        FunctionDeclare currentTimeMillis = AsmNativeFactory.createAccessor(System.class.getMethod("currentTimeMillis"));
        FunctionDeclare newList = AsmNativeFactory.createAccessor(ArrayList.class.getConstructor());
        FunctionDeclare listSize = AsmNativeFactory.createAccessor(ArrayList.class.getMethod("size"));

        FunctionDeclare listAdd = AsmNativeFactory.createAccessor(List.class.getMethod("add", Object.class));
        FunctionDeclare newListWithInitSize = AsmNativeFactory.createAccessor(ArrayList.class.getConstructor(int.class));
        FunctionDeclare listAddToIndex = AsmNativeFactory.createAccessor(List.class.getMethod("add", int.class, Object.class));
        FunctionDeclare arraycopy = AsmNativeFactory.createAccessor(System.class.getMethod("arraycopy", Object.class, int.class, Object.class, int.class, int.class));

        assertEquals("aaa", str_toString.apply(null, new Object[]{"aaa"}));

        assertEquals(currentTimeMillis.apply(null, null).getClass(), Long.class);
        assertEquals(currentTimeMillis.apply(null, new Object[0]).getClass(), Long.class);
        assertEquals(currentTimeMillis.apply(null, new Object[]{1, 2, 3}).getClass(), Long.class);

        int[] array1 = {1, 2, 3, 4, 6};
        int[] array2 = new int[array1.length];

        assertEquals(arraycopy.apply(null, new Object[]{array1, 0, array2, 0, array1.length}), Undefined.UNDEFINED);
        assertArrayEquals(array2, array2);

        List list = (List) newList.apply(null, null);

        List list2 = (List) newListWithInitSize.apply(null, new Object[]{2});
        assertNotNull(list2);

        list.add("i1");
        list.add("i2");

        assertThrows(ScriptRuntimeException.class,
                () -> listSize.apply(null, new Object[0]));

        assertEquals(listSize.apply(null, new Object[]{list}), 2);
        assertEquals(listSize.apply(null, new Object[]{list, 2, 3}), 2);

        assertEquals(listAdd.apply(null, new Object[]{list, "i3"}), true);
        assertEquals(list.size(), 3);
        assertEquals(list.get(2), "i3");

        assertEquals(listAdd.apply(null, new Object[]{list}), true);
        assertNull(list.get(3));

        assertEquals(listAddToIndex.apply(null, new Object[]{list, 2, "a3"}), Undefined.UNDEFINED);
        assertEquals(list.get(2), "a3");

        assertEquals(listAddToIndex.apply(null, new Object[]{list, 2}), Undefined.UNDEFINED);
        assertNull(list.get(2));

    }
}
