// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script.asm;

import java.util.ArrayList;
import java.util.List;
import static org.junit.Assert.*;
import org.junit.Test;
import webit.script.Context;
import webit.script.exceptions.ScriptRuntimeException;
import webit.script.lang.MethodDeclare;

/**
 *
 * @author Zqq
 */
public class AsmNativeFactoryTest {

    @Test
    @SuppressWarnings("unchecked")
    public void test() throws Exception {

        MethodDeclare str_toString = AsmNativeFactory.createAccessor(String.class.getMethod("toString", new Class[0]));
        
        MethodDeclare currentTimeMillis = AsmNativeFactory.createAccessor(System.class.getMethod("currentTimeMillis", new Class[0]));
        MethodDeclare newList = AsmNativeFactory.createAccessor(ArrayList.class.getConstructor());
        MethodDeclare listSize = AsmNativeFactory.createAccessor(ArrayList.class.getMethod("size", new Class[0]));
        
        MethodDeclare listAdd = AsmNativeFactory.createAccessor(List.class.getMethod("add", new Class[]{Object.class}));
        MethodDeclare newListWithInitSize = AsmNativeFactory.createAccessor(ArrayList.class.getConstructor(new Class[]{int.class}));
        MethodDeclare listAddToIndex = AsmNativeFactory.createAccessor(List.class.getMethod("add", new Class[]{int.class, Object.class}));
        MethodDeclare arraycopy = AsmNativeFactory.createAccessor(System.class.getMethod("arraycopy", new Class[]{Object.class, int.class, Object.class, int.class, int.class}));

        assertEquals("aaa", str_toString.invoke(null, new Object[]{"aaa"}));
        
        assertEquals(currentTimeMillis.invoke(null, null).getClass(), Long.class);
        assertEquals(currentTimeMillis.invoke(null, new Object[0]).getClass(), Long.class);
        assertEquals(currentTimeMillis.invoke(null, new Object[]{1, 2, 3}).getClass(), Long.class);

        int[] array1 = new int[]{1, 2, 3, 4, 6};
        int[] array2 = new int[array1.length];

        assertEquals(arraycopy.invoke(null, new Object[]{array1, 0, array2, 0, array1.length}), Context.VOID);
        assertArrayEquals(array2, array2);

        List list = (List) newList.invoke(null, null);

        List list2 = (List) newListWithInitSize.invoke(null, new Object[]{2});
        assertNotNull(list2);

        list.add("i1");
        list.add("i2");

        Exception exception = null;
        try {
            assertEquals(listSize.invoke(null, new Object[0]), 2);
        } catch (ScriptRuntimeException e) {
            exception = e;
        }
        assertNotNull(exception);

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
