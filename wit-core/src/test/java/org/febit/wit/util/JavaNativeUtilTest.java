package org.febit.wit.util;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author zqq90
 */
public class JavaNativeUtilTest {

    Method[] methods;
    Map<String, Method> methodPool;

    {
        Method[] allMethods = Methods.class.getMethods();
        methodPool = new HashMap<>();
        for (Method method : allMethods) {
            if (!method.getName().startsWith("foo")) {
                continue;
            }
            methodPool.put(method.getName(), method);
        }
        methods = new Method[methodPool.size()];
        int i = 0;
        for (Method value : methodPool.values()) {
            methods[i++] = value;
        }
    }

    @Test
    public void test() {

        //let's go
        assertEquals(methodPool.get("fooEmpty"),
                getMatchMethod());
        assertEquals(methodPool.get("fooString"),
                getMatchMethod(String.class));
        assertEquals(methodPool.get("fooObject"),
                getMatchMethod(Integer.class));

        assertEquals(methodPool.get("fooList"),
                getMatchMethod(List.class));
        assertEquals(methodPool.get("fooArrayList"),
                getMatchMethod(ArrayList.class));
        assertEquals(methodPool.get("fooList"),
                getMatchMethod(LinkedList.class));

        assertEquals(methodPool.get("fooArrayListObject"),
                getMatchMethod(ArrayList.class, Integer.class));
        assertEquals(methodPool.get("fooListObject"),
                getMatchMethod(List.class, Integer.class));

        assertEquals(methodPool.get("fooObjectListList"),
                getMatchMethod(ArrayList.class, ArrayList.class, ArrayList.class));

        //nullable
        assertEquals(methodPool.get("fooArrayListObject"),
                getMatchMethod(ArrayList.class, null));
        assertEquals(methodPool.get("fooArrayListObjectObject"),
                getMatchMethod(ArrayList.class, null, null));
        assertEquals(methodPool.get("fooArrayListObjectObject"),
                getMatchMethod(ArrayList.class, null, ArrayList.class));

        //overflow
        assertNull(getMatchMethod(
                new Method[]{
                    methodPool.get("fooEmpty"),
                    methodPool.get("fooObject"),
                    methodPool.get("fooString")
                },
                String.class, null)
        );

        // AmbiguousMethodException
        // assertEquals(methodPool.get("fooListString"), getMatchMethod(ArrayList.class, String.class));
    }

    protected Method getMatchMethod(Class... classes) {
        return JavaNativeUtil.getMatchMethod(methods, classes);
    }

    protected Method getMatchMethod(Method[] methods, Class... classes) {
        return JavaNativeUtil.getMatchMethod(methods, classes);
    }

    public static class Methods {

        public void fooEmpty() {

        }

        public void fooString(String obj) {

        }

        public void fooObject(Object obj) {

        }

        public void fooArrayList(ArrayList list) {

        }

        public void fooList(List list) {

        }

        public void fooListObject(List list, Object obj) {

        }

        public void fooObjectListList(Object list, List obj, List obj2) {

        }

        public void fooListObjectObject(List list, Object obj, Object obj2) {

        }

        public void fooArrayListObjectObject(ArrayList list, Object obj, Object obj2) {

        }

        public void fooArrayListObject(ArrayList list, Object obj) {

        }

        public void fooListString(List list, String obj) {

        }

        public void fooObjectList(Object obj, List list) {

        }
    }
}
