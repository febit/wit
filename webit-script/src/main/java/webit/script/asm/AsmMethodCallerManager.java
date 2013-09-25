// Copyright (c) 2013, Webit Team. All Rights Reserved.
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
    private final static AsmMethodCallerGenerator asmMethodCallerGenerator = new AsmMethodCallerGenerator();

    public static AsmMethodCaller getCaller(Method method) throws Exception {
        return doGetCaller(method);
    }

    public static AsmMethodCaller getCaller(Constructor constructor) throws Exception {
        return doGetCaller(constructor);
    }

    private static AsmMethodCaller doGetCaller(Object method) throws Exception {
        AsmMethodCallerBox box = asmMethodCallerMap.get(method);
        if (box == null) {
            synchronized (asmMethodCallerMap) {
                box = asmMethodCallerMap.get(method);
                if (box == null) {
                    box = new AsmMethodCallerBox(method);
                    asmMethodCallerMap.put(method, box);
                }
            }
        }
        //
        AsmMethodCaller caller = box.caller;
        if (caller == null) {
            synchronized (box) {
                caller = box.caller;
                if (caller == null) {
                    if (method instanceof Method) {
                        caller = (AsmMethodCaller) asmMethodCallerGenerator.generateCaller((Method) method).newInstance();
                    } else if (method instanceof Constructor) {
                        caller = (AsmMethodCaller) asmMethodCallerGenerator.generateCaller((Constructor) method).newInstance();
                    }
                    box.caller = caller;
                }
            }
        }
        return caller;
    }

    private static class AsmMethodCallerBox {

        final Object key;
        AsmMethodCaller caller;

        AsmMethodCallerBox(Object method) {
            this.key = method;
        }

        @Override
        public int hashCode() {
            return this.key != null ? this.key.hashCode() : 0;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final AsmMethodCallerBox other = (AsmMethodCallerBox) obj;
            if (this.key != other.key && (this.key == null || !this.key.equals(other.key))) {
                return false;
            }
            return true;
        }
    }
}
