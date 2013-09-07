// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.resolvers;

import java.util.ArrayList;
import webit.script.asm.AsmResolverManager;
import webit.script.resolvers.impl.CommonResolver;
import webit.script.util.collection.IdentityHashMap;

/**
 *
 * @author Zqq
 */
public class ResolverManager {

    private IdentityHashMap<GetResolver> getResolverMap;
    private IdentityHashMap<SetResolver> setResolverMap;
    private IdentityHashMap<ToStringResolver> toStringResolverMap;
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
        getResolverMap = new IdentityHashMap<GetResolver>();
        setResolverMap = new IdentityHashMap<SetResolver>();
        toStringResolverMap = new IdentityHashMap<ToStringResolver>();

        getResolvers = new ArrayList<GetResolver>();
        setResolvers = new ArrayList<SetResolver>();
        toStringResolvers = new ArrayList<ToStringResolver>();
        getResolverTypes = new ArrayList<Class>();
        setResolverTypes = new ArrayList<Class>();
        toStringResolverTypes = new ArrayList<Class>();

        commonResolver = new CommonResolver();
    }

    @SuppressWarnings("unchecked")
    private GetResolver getGetResolver(Object bean) {
        Class type = bean.getClass();
        GetResolver resolver = getResolverMap.unsafeGet(type);
        if (resolver == null) {
            resolver = getResolverMap.get(type);
            if (resolver == null) {
                for (int i = 0; i < getResolverTypes.size(); i++) {
                    if (getResolverTypes.get(i).isAssignableFrom(type)) {
                        resolver = getResolvers.get(i);
                        break;
                    }
                }

                if (resolver == null && enableAsm) {
                    resolver = AsmResolverManager.generateAsmResolver(type);
                }

                if (resolver == null) {
                    resolver = commonResolver;
                }

                //last
                resolver = getResolverMap.putIfAbsent(type, resolver);
            }
        }

        return resolver;
    }

    @SuppressWarnings("unchecked")
    private SetResolver getSetResolver(Object bean) {
        Class type = bean.getClass();
        SetResolver resolver = setResolverMap.unsafeGet(type);
        if (resolver == null) {
            resolver = setResolverMap.get(type);
            if (resolver == null) {
                for (int i = 0; i < setResolverTypes.size(); i++) {
                    if (setResolverTypes.get(i).isAssignableFrom(type)) {
                        resolver = setResolvers.get(i);
                        break;
                    }
                }

                if (resolver == null && enableAsm) {
                    resolver = AsmResolverManager.generateAsmResolver(type);
                }
                
                if (resolver == null) {
                    resolver = commonResolver;
                }

                resolver = setResolverMap.putIfAbsent(type, resolver);
            }
        }
        return resolver;
    }

    @SuppressWarnings("unchecked")
    private ToStringResolver getToStringResolver(Object bean) {
        Class type = bean.getClass();
        ToStringResolver resolver = toStringResolverMap.unsafeGet(type);
        if (resolver == null) {
            if (resolver == null) {
                resolver = toStringResolverMap.get(type);
                for (int i = 0; i < toStringResolverTypes.size(); i++) {
                    if (toStringResolverTypes.get(i).isAssignableFrom(type)) {
                        resolver = toStringResolvers.get(i);
                        break;
                    }
                }

                if (resolver == null) {
                    resolver = commonResolver;
                }

                resolver = toStringResolverMap.putIfAbsent(type, resolver);
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

    public final String toString(Object bean) {
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
}
