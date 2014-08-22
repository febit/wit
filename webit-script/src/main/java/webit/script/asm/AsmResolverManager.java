// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script.asm;

import webit.script.util.ClassMap;

/**
 *
 * @author Zqq
 */
public class AsmResolverManager {

    private static final ClassMap<AsmResolverBox> asmResolversMap = new ClassMap<AsmResolverBox>();

    public static AsmResolver getAsmResolver(Class type) throws Exception {
        AsmResolverBox box;
        if ((box = asmResolversMap.get(type)) == null) {
            box = asmResolversMap.putIfAbsent(type, new AsmResolverBox());
        }
        //
        AsmResolver resolver;
        if ((resolver = box.resolver) == null) {
            synchronized (box) {
                if ((resolver = box.resolver) == null) {
                    box.resolver = resolver = (AsmResolver) AsmResolverFactory.createResolverClass(type).newInstance();
                }
            }
        }
        return resolver;
    }

    private static final class AsmResolverBox {

        AsmResolver resolver;

        AsmResolverBox() {
        }
    }
}
