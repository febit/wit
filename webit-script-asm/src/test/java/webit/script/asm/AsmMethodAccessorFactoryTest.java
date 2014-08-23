// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script.asm;

import java.util.ArrayList;
import java.util.List;
import static org.junit.Assert.*;
import org.junit.Test;
import webit.script.Context;
import webit.script.exceptions.ScriptRuntimeException;

/**
 *
 * @author Zqq
 */
public class AsmMethodAccessorFactoryTest {

    @Test
    @SuppressWarnings("unchecked")
    public void test() throws NoSuchMethodException, InstantiationException, IllegalAccessException {

        Class currentTimeMillisClass = AsmMethodAccessorFactory.createAccessorClass(System.class.getMethod("currentTimeMillis", new Class[0]));
        Class arraycopyClass = AsmMethodAccessorFactory.createAccessorClass(System.class.getMethod("arraycopy", new Class[]{Object.class, int.class, Object.class, int.class, int.class}));

        Class newListClass = AsmMethodAccessorFactory.createAccessorClass(ArrayList.class.getConstructor());
        Class newListWithInitSizeClass = AsmMethodAccessorFactory.createAccessorClass(ArrayList.class.getConstructor(new Class[]{int.class}));
        
        Class listSizeClass = AsmMethodAccessorFactory.createAccessorClass(ArrayList.class.getMethod("size", new Class[0]));
        Class listAddClass = AsmMethodAccessorFactory.createAccessorClass(List.class.getMethod("add", new Class[]{Object.class}));
        Class listAddToIndexClass = AsmMethodAccessorFactory.createAccessorClass(List.class.getMethod("add", new Class[]{int.class, Object.class}));
        
        
        AsmMethodAccessor currentTimeMillis = (AsmMethodAccessor) currentTimeMillisClass.newInstance();
        AsmMethodAccessor arraycopy = (AsmMethodAccessor) arraycopyClass.newInstance();
        
        AsmMethodAccessor newList = (AsmMethodAccessor) newListClass.newInstance();
        AsmMethodAccessor newListWithInitSize = (AsmMethodAccessor) newListWithInitSizeClass.newInstance();

        AsmMethodAccessor listSize = (AsmMethodAccessor) listSizeClass.newInstance();
        AsmMethodAccessor listAdd = (AsmMethodAccessor) listAddClass.newInstance();
        AsmMethodAccessor listAddToIndex = (AsmMethodAccessor) listAddToIndexClass.newInstance();
        

        assertEquals(currentTimeMillis.execute(null).getClass(), Long.class);
        assertEquals(currentTimeMillis.execute(new Object[0]).getClass(), Long.class);
        assertEquals(currentTimeMillis.execute(new Object[]{1, 2, 3}).getClass(), Long.class);

        int[] array1 = new int[]{1, 2, 3, 4, 6};
        int[] array2 = new int[array1.length];


        assertEquals(arraycopy.execute(new Object[]{array1, 0, array2, 0, array1.length}), Context.VOID);
        assertArrayEquals(array2, array2);


        List list = (List) newList.execute(null);
        
        List list2 = (List) newListWithInitSize.execute(new Object[]{2});
        assertNotNull(list2);
        
        list.add("i1");
        list.add("i2");

        

        Exception exception = null;
        try {
            assertEquals(listSize.execute(new Object[0]), 2);
        } catch (ScriptRuntimeException e) {
            exception = e;
        }
        assertNotNull(exception);
        

        assertEquals(listSize.execute(new Object[]{list}), 2);
        assertEquals(listSize.execute(new Object[]{list, 2, 3}), 2);


        assertEquals(listAdd.execute(new Object[]{list, "i3"}), true);
        assertEquals(list.size(), 3);
        assertEquals(list.get(2), "i3");


        assertEquals(listAdd.execute(new Object[]{list}), true);
        assertNull(list.get(3));


        assertEquals(listAddToIndex.execute(new Object[]{list, 2, "a3"}), Context.VOID);
        assertEquals(list.get(2), "a3");


        assertEquals(listAddToIndex.execute(new Object[]{list, 2}), Context.VOID);
        assertNull(list.get(2));

    }
}
