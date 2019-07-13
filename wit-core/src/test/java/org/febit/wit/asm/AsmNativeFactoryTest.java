// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.asm;

import org.febit.wit.Context;
import org.febit.wit.exceptions.ScriptRuntimeException;
import org.febit.wit.lang.MethodDeclare;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author zqq90
 */
class AsmNativeFactoryTest {

    @Test
    @SuppressWarnings("unchecked")
    void test() throws Exception {

        MethodDeclare str_toString = AsmNativeFactory.createAccessor(String.class.getMethod("toString"));

        MethodDeclare currentTimeMillis = AsmNativeFactory.createAccessor(System.class.getMethod("currentTimeMillis"));
        MethodDeclare newList = AsmNativeFactory.createAccessor(ArrayList.class.getConstructor());
        MethodDeclare listSize = AsmNativeFactory.createAccessor(ArrayList.class.getMethod("size"));

        MethodDeclare listAdd = AsmNativeFactory.createAccessor(List.class.getMethod("add", Object.class));
        MethodDeclare newListWithInitSize = AsmNativeFactory.createAccessor(ArrayList.class.getConstructor(int.class));
        MethodDeclare listAddToIndex = AsmNativeFactory.createAccessor(List.class.getMethod("add", int.class, Object.class));
        MethodDeclare arraycopy = AsmNativeFactory.createAccessor(System.class.getMethod("arraycopy", Object.class, int.class, Object.class, int.class, int.class));

        assertEquals("aaa", str_toString.invoke(null, new Object[]{"aaa"}));

        assertEquals(currentTimeMillis.invoke(null, null).getClass(), Long.class);
        assertEquals(currentTimeMillis.invoke(null, new Object[0]).getClass(), Long.class);
        assertEquals(currentTimeMillis.invoke(null, new Object[]{1, 2, 3}).getClass(), Long.class);

        int[] array1 = {1, 2, 3, 4, 6};
        int[] array2 = new int[array1.length];

        assertEquals(arraycopy.invoke(null, new Object[]{array1, 0, array2, 0, array1.length}), Context.VOID);
        assertArrayEquals(array2, array2);

        List list = (List) newList.invoke(null, null);

        List list2 = (List) newListWithInitSize.invoke(null, new Object[]{2});
        assertNotNull(list2);

        list.add("i1");
        list.add("i2");

        assertThrows(ScriptRuntimeException.class,
                () -> listSize.invoke(null, new Object[0]));

        assertEquals(listSize.invoke(null, new Object[]{list}), 2);
        assertEquals(listSize.invoke(null, new Object[]{list, 2, 3}), 2);

        assertEquals(listAdd.invoke(null, new Object[]{list, "i3"}), true);
        assertEquals(list.size(), 3);
        assertEquals(list.get(2), "i3");

        assertEquals(listAdd.invoke(null, new Object[]{list}), true);
        assertNull(list.get(3));

        assertEquals(listAddToIndex.invoke(null, new Object[]{list, 2, "a3"}), Context.VOID);
        assertEquals(list.get(2), "a3");

        assertEquals(listAddToIndex.invoke(null, new Object[]{list, 2}), Context.VOID);
        assertNull(list.get(2));

    }
}
