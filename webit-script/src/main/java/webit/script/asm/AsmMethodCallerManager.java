// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script.asm;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.IdentityHashMap;
import java.util.Map;

/**
 *
 * @author Zqq
 */
public class AsmMethodCallerManager {

    private final static Map<Object, AsmMethodCallerBox> asmMethodCallerMap = new IdentityHashMap<Object, AsmMethodCallerBox>();

    public static AsmMethodCaller getCaller(Method method) throws Exception {
        return doGetCaller(method);
    }

    public static AsmMethodCaller getCaller(Constructor constructor) throws Exception {
        return doGetCaller(constructor);
    }

    private static AsmMethodCaller doGetCaller(Object method) throws Exception {
        AsmMethodCallerBox box;
        if ((box = asmMethodCallerMap.get(method)) == null) {
            synchronized (asmMethodCallerMap) {
                if ((box = asmMethodCallerMap.get(method)) == null) {
                    asmMethodCallerMap.put(method, box = new AsmMethodCallerBox());
                }
            }
        }
        //
        AsmMethodCaller caller;
        if ((caller = box.caller) == null) {
            synchronized (box) {
                if ((caller = box.caller) == null) {
                    if (method instanceof Method) {
                        box.caller = caller = (AsmMethodCaller) AsmMethodCallerGenerator.generateCaller((Method) method).newInstance();
                    } else if (method instanceof Constructor) {
                        box.caller = caller = (AsmMethodCaller) AsmMethodCallerGenerator.generateCaller((Constructor) method).newInstance();
                    }
                }
            }
        }
        return caller;
    }

    private static class AsmMethodCallerBox {

        AsmMethodCaller caller;
    }
}
