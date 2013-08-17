// Copyright (c) 2013, Webit Team. All Rights Reserved.

package webit.script.resolvers;

import webit.script.asm.AsmResolverGenerator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import webit.script.resolvers.impl.CommonResolver;

/**
 *
 * @author Zqq
 */
public class ResolverManager {

    private ConcurrentMap<Class, GetResolver> getResolverMap;
    private ConcurrentMap<Class, SetResolver> setResolverMap;
    private ConcurrentMap<Class, ToStringResolver> toStringResolverMap;
    //
    private ArrayList<GetResolver> getResolvers;
    private ArrayList<SetResolver> setResolvers;
    private ArrayList<ToStringResolver> toStringResolvers;
    private ArrayList<Class> getResolverTypes;
    private ArrayList<Class> setResolverTypes;
    private ArrayList<Class> toStringResolverTypes;
    //
    private CommonResolver commonResolver;
    //settings
    private boolean enableAsm = true;

    public ResolverManager() {
        getResolverMap = new ConcurrentHashMap<Class, GetResolver>();
        setResolverMap = new ConcurrentHashMap<Class, SetResolver>();
        toStringResolverMap = new ConcurrentHashMap<Class, ToStringResolver>();

        getResolvers = new ArrayList<GetResolver>();
        setResolvers = new ArrayList<SetResolver>();
        toStringResolvers = new ArrayList<ToStringResolver>();
        getResolverTypes = new ArrayList<Class>();
        setResolverTypes = new ArrayList<Class>();
        toStringResolverTypes = new ArrayList<Class>();

        commonResolver = new CommonResolver();
    }
    private Map<Class, AsmResolverBox> asmResolversMap = new HashMap<Class, AsmResolverBox>();
    private AsmResolverGenerator asmResolverGenerator = new AsmResolverGenerator();

    private AsmResolver generateAsmResolver(Class type) {
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
        AsmResolver resolver = box.getResolver();
        if (resolver == null) {
            synchronized (box) {
                resolver = box.getResolver();
                if (resolver == null) {
                    try {
                        resolver = (AsmResolver) asmResolverGenerator.generateResolver(type).newInstance();
                        box.setResolver(resolver);
                    } catch (Exception ex) {
                        //TODO: 记录生成失败
                    }
                }
            }
        }
        return resolver;
    }

    private GetResolver getGetResolver(Object bean) {
        Class type = bean.getClass();
        GetResolver resolver = getResolverMap.get(type);
        if (resolver == null) {
            for (int i = 0; i < getResolverTypes.size(); i++) {
                if (getResolverTypes.get(i).isAssignableFrom(type)) {
                    resolver = getResolvers.get(i);
                    break;
                }
            }

            if (resolver == null && enableAsm) {
                resolver = generateAsmResolver(type);
            }

            if (resolver == null) {
                resolver = commonResolver;
            }

            GetResolver old = getResolverMap.putIfAbsent(type, resolver);
            if (old != null) {
                resolver = old;
            }
        }

        return resolver;
    }

    private SetResolver getSetResolver(Object bean) {
        Class type = bean.getClass();
        SetResolver resolver = setResolverMap.get(type);
        if (resolver == null) {
            for (int i = 0; i < setResolverTypes.size(); i++) {
                if (setResolverTypes.get(i).isAssignableFrom(type)) {
                    resolver = setResolvers.get(i);
                    break;
                }
            }

            if (resolver == null && enableAsm) {
                resolver = generateAsmResolver(type);
            }
            if (resolver == null) {
                resolver = commonResolver;
            }

            SetResolver old = setResolverMap.putIfAbsent(type, resolver);
            if (old != null) {
                resolver = old;
            }
        }
        return resolver;
    }

    private ToStringResolver getToStringResolver(Object bean) {
        Class type = bean.getClass();
        ToStringResolver resolver = toStringResolverMap.get(type);
        if (resolver == null) {
            for (int i = 0; i < toStringResolverTypes.size(); i++) {
                if (toStringResolverTypes.get(i).isAssignableFrom(type)) {
                    resolver = toStringResolvers.get(i);
                    break;
                }
            }

            if (resolver == null) {
                resolver = commonResolver;
            }

            ToStringResolver old = toStringResolverMap.putIfAbsent(type, resolver);
            if (old != null) {
                resolver = old;
            }
        }
        return resolver;
    }

    public void init(Resolver[] resolvers) {
        for (int i = 0; i < resolvers.length; i++) {
            Resolver resolver = resolvers[i];
            if (resolver.getMatchMode() == MatchMode.REGIST) {
                ((RegistModeResolver) resolver).regist(this);
            } else {
                registResolver(resolver.getMatchClass(), resolver, resolver.getMatchMode());
            }
        }

        getResolvers.trimToSize();
        setResolvers.trimToSize();
        toStringResolvers.trimToSize();
        getResolverTypes.trimToSize();
        setResolverTypes.trimToSize();
        toStringResolverTypes.trimToSize();
        //
    }

    public final boolean registResolver(Class type, Resolver resolver, MatchMode matchMode) {
        switch (matchMode) {
            case INSTANCEOF:
                if (resolver instanceof GetResolver) {
                    getResolverTypes.add(type);
                    getResolvers.add((GetResolver) resolver);
                }
                if (resolver instanceof SetResolver) {
                    setResolverTypes.add(type);
                    setResolvers.add((SetResolver) resolver);
                }
                if (resolver instanceof ToStringResolver) {
                    toStringResolverTypes.add(type);
                    toStringResolvers.add((ToStringResolver) resolver);
                }
                break;

            case EQUALS:
                if (resolver instanceof GetResolver) {
                    getResolverMap.putIfAbsent(type, (GetResolver) resolver);
                }
                if (resolver instanceof SetResolver) {
                    setResolverMap.putIfAbsent(type, (SetResolver) resolver);
                }
                if (resolver instanceof ToStringResolver) {
                    toStringResolverMap.putIfAbsent(type, (ToStringResolver) resolver);
                }
                break;
            case REGIST:
            default:
                //EXCEPTION??
                return false;
        }

        return true;
    }

    public final Object get(Object bean, Object property) {
        if (bean != null) {
            return getGetResolver(bean).get(bean, property);
        } else {
            return null;
        }
    }

    public final boolean set(Object bean, Object property, Object value) {
        if (bean != null) {
            return getSetResolver(bean).set(bean, property, value);
        } else {
            return false;
        }
    }

    public final String toString(Object bean){
        if (bean != null) {
            return getToStringResolver(bean).toString(bean);
        } else {
            return null;
        }
    }

    public boolean isEnableAsm() {
        return enableAsm;
    }

    public void setEnableAsm(boolean enableAsm) {
        this.enableAsm = enableAsm;
    }

    private static class AsmResolverBox {

        private final Class type;
        private AsmResolver resolver;

        public AsmResolverBox(Class type) {
            this.type = type;
        }

        public AsmResolver getResolver() {
            return resolver;
        }

        public void setResolver(AsmResolver resolver) {
            this.resolver = resolver;
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
