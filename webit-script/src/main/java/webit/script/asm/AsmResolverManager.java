// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.asm;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Zqq
 */
public class AsmResolverManager {

    private final static Map<Class, AsmResolverBox> asmResolversMap = new HashMap<Class, AsmResolverBox>();
    private final static AsmResolverGenerator asmResolverGenerator = new AsmResolverGenerator();

    public static AsmResolver generateAsmResolver(Class type) throws Throwable {
        AsmResolverBox box = asmResolversMap.get(type);
        if (box == null) {
            synchronized (asmResolversMap) {
                box = asmResolversMap.get(type);
                if (box == null) {
                    box = new AsmResolverBox(type);
                    asmResolversMap.put(type, box);
                }
            }
        }
        //
        AsmResolver resolver = box.resolver;
        if (resolver == null) {
            synchronized (box) {
                resolver = box.resolver;
                if (resolver == null) {
                    resolver = (AsmResolver) asmResolverGenerator.generateResolver(type).newInstance();
                    box.resolver = resolver;
                }
            }
        }
        return resolver;
    }

    private static class AsmResolverBox {

        final Class type;
        AsmResolver resolver;

        public AsmResolverBox(Class type) {
            this.type = type;
        }

        @Override
        public int hashCode() {
            return this.type != null ? this.type.hashCode() : 0;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final AsmResolverBox other = (AsmResolverBox) obj;
            if (this.type != other.type && (this.type == null || !this.type.equals(other.type))) {
                return false;
            }
            return true;
        }
    }
}
