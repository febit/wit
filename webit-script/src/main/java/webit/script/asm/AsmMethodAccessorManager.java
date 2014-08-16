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
public class AsmMethodAccessorManager {

    private final static Map<Object, AsmMethodAccessorBox> accessorCache = new IdentityHashMap<Object, AsmMethodAccessorBox>();

    public static AsmMethodAccessor getAccessor(Method method) throws Exception {
        return doGetAccessor(method);
    }

    public static AsmMethodAccessor getAccessor(Constructor constructor) throws Exception {
        return doGetAccessor(constructor);
    }

    private static AsmMethodAccessor doGetAccessor(Object method) throws Exception {
        AsmMethodAccessorBox box;
        if ((box = accessorCache.get(method)) == null) {
            synchronized (accessorCache) {
                if ((box = accessorCache.get(method)) == null) {
                    accessorCache.put(method, box = new AsmMethodAccessorBox());
                }
            }
        }
        AsmMethodAccessor accessor;
        if ((accessor = box.accessor) == null) {
            synchronized (box) {
                if ((accessor = box.accessor) == null) {
                    if (method instanceof Method) {
                        box.accessor = accessor = (AsmMethodAccessor) AsmMethodAccessorFactory.createAccessorClass((Method) method).newInstance();
                    } else if (method instanceof Constructor) {
                        box.accessor = accessor = (AsmMethodAccessor) AsmMethodAccessorFactory.createAccessorClass((Constructor) method).newInstance();
                    }
                }
            }
        }
        return accessor;
    }

    private static class AsmMethodAccessorBox {

        AsmMethodAccessor accessor;
    }
}
