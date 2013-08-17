// Copyright (c) 2013, Webit Team. All Rights Reserved.

package webit.script.asm;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.junit.Test;
import static org.junit.Assert.*;
import webit.script.Context;
import webit.script.exceptions.ScriptRuntimeException;

/**
 *
 * @author Zqq
 */
public class ASMMethodCallerGeneratorTest {

    @Test
    public void test() throws NoSuchMethodException, InstantiationException, IllegalAccessException {
        AsmMethodCallerGenerator generator = new AsmMethodCallerGenerator();
        //System.currentTimeMillis();
        Class currentTimeMillisClass = generator.generateCaller(System.class.getMethod("currentTimeMillis", new Class[0]));
        Class arraycopyClass = generator.generateCaller(System.class.getMethod("arraycopy", new Class[]{Object.class, int.class, Object.class, int.class, int.class}));

        Class newListClass = generator.generateCaller(ArrayList.class.getConstructor());
        Class newListWithInitSizeClass = generator.generateCaller(ArrayList.class.getConstructor(new Class[]{int.class}));
        
        Class listSizeClass = generator.generateCaller(ArrayList.class.getMethod("size", new Class[0]));
        Class listAddClass = generator.generateCaller(List.class.getMethod("add", new Class[]{Object.class}));
        Class listAddToIndexClass = generator.generateCaller(List.class.getMethod("add", new Class[]{int.class, Object.class}));
        
        
        AsmMethodCaller currentTimeMillis = (AsmMethodCaller) currentTimeMillisClass.newInstance();
        AsmMethodCaller arraycopy = (AsmMethodCaller) arraycopyClass.newInstance();
        
        AsmMethodCaller newList = (AsmMethodCaller) newListClass.newInstance();
        AsmMethodCaller newListWithInitSize = (AsmMethodCaller) newListWithInitSizeClass.newInstance();

        AsmMethodCaller listSize = (AsmMethodCaller) listSizeClass.newInstance();
        AsmMethodCaller listAdd = (AsmMethodCaller) listAddClass.newInstance();
        AsmMethodCaller listAddToIndex = (AsmMethodCaller) listAddToIndexClass.newInstance();
        

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
        
        //list.size();
        list.add("i1");
        list.add("i2");

        

        Exception exception = null;
        try {
            //exception
            assertEquals(listSize.execute(new Object[0]), 2);
        } catch (Exception e) {
            exception = e;
        }

        assertNotNull(exception);
        assertEquals(exception.getClass(), ScriptRuntimeException.class);


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
