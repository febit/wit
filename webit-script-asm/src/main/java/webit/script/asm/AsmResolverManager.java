// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script.asm;

import webit.script.resolvers.GetResolver;
import webit.script.resolvers.ResolverManager;
import webit.script.resolvers.SetResolver;
import webit.script.util.ClassMap;

/**
 *
 * @author Zqq
 */
public class AsmResolverManager extends ResolverManager{

    private static final ClassMap<AsmResolverBox> asmResolversMap = new ClassMap<AsmResolverBox>();

    @Override
    protected SetResolver resolveSetResolver(Class type) {
        try {
            return getAsmResolver(type);
        } catch (Exception e) {
            logger.warn("Failed to generate AsmResolver for type '{}', use default resolver instead.", type.getName());
        }
        return super.resolveSetResolver(type);
    }

    @Override
    protected GetResolver resolveGetResolver(Class type) {
        try {
            return getAsmResolver(type);
        } catch (Exception e) {
            logger.warn("Failed to generate AsmResolver for type '{}', use default resolver instead.", type.getName());
        }
        return super.resolveGetResolver(type);
    }
    
    public static AsmResolver getAsmResolver(Class type) throws Exception {
        AsmResolverBox box;
        if ((box = asmResolversMap.get(type)) == null) {
            box = asmResolversMap.putIfAbsent(type, new AsmResolverBox());
        }
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
