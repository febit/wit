// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.asm;

import webit.script.util.collection.ClassIdentityHashMap;

/**
 *
 * @author Zqq
 */
public class AsmResolverManager {

    private final static ClassIdentityHashMap<AsmResolverBox> asmResolversMap = new ClassIdentityHashMap<AsmResolverBox>();

    public static AsmResolver getAsmResolver(Class type) throws Throwable {
        AsmResolverBox box;
        if ((box = asmResolversMap.get(type)) == null) {
            box = asmResolversMap.putIfAbsent(type, new AsmResolverBox());
        }
        //
        AsmResolver resolver;
        if ((resolver = box.resolver) == null) {
            synchronized (box) {
                if ((resolver = box.resolver) == null) {
                    box.resolver = resolver = (AsmResolver) AsmResolverGenerator.generateResolver(type).newInstance();
                }
            }
        }
        return resolver;
    }

    private static class AsmResolverBox {
        AsmResolver resolver;
    }
}
