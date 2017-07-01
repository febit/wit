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

    Method[] fooMethods;
    Method[] mixMethods;
    Map<String, Method> methodPool;

    {
        List<Method> foos = new ArrayList<>();
        List<Method> mixes = new ArrayList<>();

        methodPool = new HashMap<>();
        for (Method method : Methods.class.getMethods()) {
            String methodName = method.getName();
            if (methodName.startsWith("foo")) {
                foos.add(method);
                methodPool.put(methodName, method);
            } else if (methodName.startsWith("mix")) {
                mixes.add(method);
                methodPool.put(methodName, method);
            }
        }
        fooMethods = foos.toArray(new Method[foos.size()]);
        mixMethods = mixes.toArray(new Method[mixes.size()]);
    }

    @Test
    public void test() {

        //let's go
        assertEquals(methodPool.get("fooEmpty"),
                matchFoo());
        assertEquals(methodPool.get("fooString"),
                matchFoo(String.class));
        assertEquals(methodPool.get("fooObject"),
                matchFoo(StringBuilder.class));
        assertEquals(methodPool.get("fooInt"),
                matchFoo(Integer.class));

        assertEquals(methodPool.get("fooList"),
                matchFoo(List.class));
        assertEquals(methodPool.get("fooArrayList"),
                matchFoo(ArrayList.class));
        assertEquals(methodPool.get("fooList"),
                matchFoo(LinkedList.class));

        assertEquals(methodPool.get("fooArrayListObject"),
                matchFoo(ArrayList.class, Integer.class));
        assertEquals(methodPool.get("fooListObject"),
                matchFoo(List.class, Integer.class));

        assertEquals(methodPool.get("fooObjectListList"),
                matchFoo(ArrayList.class, ArrayList.class, ArrayList.class));

        //nullable
        assertEquals(methodPool.get("fooArrayListObject"),
                matchFoo(ArrayList.class, null));
        assertEquals(methodPool.get("fooArrayListObjectObject"),
                matchFoo(ArrayList.class, null, null));
        assertEquals(methodPool.get("fooArrayListObjectObject"),
                matchFoo(ArrayList.class, null, ArrayList.class));

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
        // assertEquals(methodPool.get("fooListString"), matchFoo(ArrayList.class, String.class));
    }

    @Test
    public void testMix() {

        assertEquals(methodPool.get("mixStaticEmpty"),
                matchMix());
        assertEquals(methodPool.get("mixEmpty"),
                matchMix(Methods.class));

        assertEquals(methodPool.get("mixStaticString"),
                matchMix(Methods.class, String.class));

        assertEquals(methodPool.get("mixObject"),
                matchMix(Methods.class, StringBuilder.class));
        assertEquals(methodPool.get("mixInt"),
                matchMix(Methods.class, Integer.class));

        assertEquals(methodPool.get("mixStaticList"),
                matchMix(Methods.class, List.class));
        assertEquals(methodPool.get("mixArrayList"),
                matchMix(Methods.class, ArrayList.class));
        assertEquals(methodPool.get("mixStaticList"),
                matchMix(Methods.class, LinkedList.class));

        assertEquals(methodPool.get("mixArrayListInteger"),
                matchMix(Methods.class, ArrayList.class, Integer.class));
        assertEquals(methodPool.get("mixStaticArrayListString"),
                matchMix(Methods.class, ArrayList.class, String.class));
        assertEquals(methodPool.get("mixStaticListInteger"),
                matchMix(Methods.class, LinkedList.class, Integer.class));
    }

    protected Method matchFoo(Class... classes) {
        return JavaNativeUtil.getMatchMethod(fooMethods, classes);
    }

    protected Method matchMix(Class... classes) {
        return JavaNativeUtil.getMatchMethod(mixMethods, classes, true);
    }

    protected Method getMatchMethod(Method[] methods, Class... classes) {
        return JavaNativeUtil.getMatchMethod(methods, classes);
    }

    public static class Methods {

        public static void mixStaticEmpty() {
        }

        public static void mixStaticString(Methods methods, String str) {
        }

        public static void mixStaticList(Methods methods, List list) {
        }

        public static void mixStaticArrayListString(Methods methods, ArrayList list, String str) {
        }

        public static void mixStaticListInteger(Methods methods, List list, Integer i) {
        }

        public void mixEmpty() {
        }

        public void mixObject(Object obj) {
        }

        public void mixInt(int obj) {
        }

        public void mixArrayList(ArrayList list) {
        }

        public void mixArrayListInteger(ArrayList list, Integer i) {
        }

        public void fooEmpty() {

        }

        public void fooString(String obj) {

        }

        public void fooInt(int i) {

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
