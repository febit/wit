// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.resolvers;

import java.util.ArrayList;
import webit.script.Initable;
import webit.script.Engine;
import webit.script.asm.AsmResolverManager;
import webit.script.io.Out;
import webit.script.loggers.Logger;
import webit.script.resolvers.impl.CommonResolver;
import webit.script.util.collection.IdentityHashMap;

/**
 *
 * @author Zqq
 */
public final class ResolverManager implements Initable {

    private Logger logger;
    //
    private final IdentityHashMap<GetResolver> getResolverMap;
    private final IdentityHashMap<SetResolver> setResolverMap;
    private final IdentityHashMap<OutResolver> outResolverMap;
    //
    private final ArrayList<GetResolver> getResolvers;
    private final ArrayList<SetResolver> setResolvers;
    private final ArrayList<OutResolver> outResolvers;
    private final ArrayList<Class> getResolverTypes;
    private final ArrayList<Class> setResolverTypes;
    private final ArrayList<Class> outResolverTypes;
    //
    private final CommonResolver commonResolver;
    //settings
    private boolean enableAsm = true;
    //

    public ResolverManager() {
        getResolverMap = new IdentityHashMap<GetResolver>();
        setResolverMap = new IdentityHashMap<SetResolver>();
        outResolverMap = new IdentityHashMap<OutResolver>();

        getResolvers = new ArrayList<GetResolver>();
        setResolvers = new ArrayList<SetResolver>();
        outResolvers = new ArrayList<OutResolver>();
        getResolverTypes = new ArrayList<Class>();
        setResolverTypes = new ArrayList<Class>();
        outResolverTypes = new ArrayList<Class>();

        commonResolver = new CommonResolver();
    }

    @SuppressWarnings("unchecked")
    private GetResolver getGetResolver(Object bean) {
        final Class type = bean.getClass();
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
                    try {
                        resolver = AsmResolverManager.generateAsmResolver(type);
                    } catch (Throwable e) {
                        logger.error(null, e);
                    }
                    if (resolver == null) {
                        logger.warn("Failed to generate AsmResolver for type '{}', use CommonResolver instead.", type.getName());
                    }
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
        final Class type = bean.getClass();
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
                    try {
                        resolver = AsmResolverManager.generateAsmResolver(type);
                    } catch (Throwable e) {
                        logger.error(null, e);
                    }
                    if (resolver == null) {
                        logger.warn("Failed to generate AsmResolver for type '{}', use CommonResolver instead.", type.getName());
                    }
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
    private OutResolver getOutResolver(Object bean) {
        final Class type = bean.getClass();
        OutResolver resolver = outResolverMap.unsafeGet(type);
        if (resolver == null) {
            if (resolver == null) {
                resolver = outResolverMap.get(type);
                for (int i = 0; i < outResolverTypes.size(); i++) {
                    if (outResolverTypes.get(i).isAssignableFrom(type)) {
                        resolver = outResolvers.get(i);
                        break;
                    }
                }

                if (resolver == null) {
                    resolver = commonResolver;
                }

                resolver = outResolverMap.putIfAbsent(type, resolver);
            }
        }
        return resolver;
    }

    public void init(Resolver[] resolvers) {
        Resolver resolver;
        for (int i = 0; i < resolvers.length; i++) {
            resolver = resolvers[i];
            if (resolver.getMatchMode() == MatchMode.REGIST) {
                ((RegistModeResolver) resolver).regist(this);
            } else {
                registResolver(resolver.getMatchClass(), resolver, resolver.getMatchMode());
            }
        }

        getResolvers.trimToSize();
        setResolvers.trimToSize();
        outResolvers.trimToSize();
        getResolverTypes.trimToSize();
        setResolverTypes.trimToSize();
        outResolverTypes.trimToSize();
        //
    }

    public boolean registResolver(Class type, Resolver resolver, MatchMode matchMode) {
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
                if (resolver instanceof OutResolver) {
                    outResolverTypes.add(type);
                    outResolvers.add((OutResolver) resolver);
                }
                break;

            case EQUALS:
                if (resolver instanceof GetResolver) {
                    getResolverMap.putIfAbsent(type, (GetResolver) resolver);
                }
                if (resolver instanceof SetResolver) {
                    setResolverMap.putIfAbsent(type, (SetResolver) resolver);
                }
                if (resolver instanceof OutResolver) {
                    outResolverMap.putIfAbsent(type, (OutResolver) resolver);
                }
                break;
            case REGIST:
            default:
                //EXCEPTION??
                return false;
        }

        return true;
    }

    public Object get(Object bean, Object property) {
        return bean != null ? getGetResolver(bean).get(bean, property) : null;
    }

    public boolean set(Object bean, Object property, Object value) {
        return bean != null ? getSetResolver(bean).set(bean, property, value) : false;
    }

    public void render(final Out out, final Object bean) {
        getOutResolver(bean).render(out, bean);
    }

    public void setEnableAsm(boolean enableAsm) {
        this.enableAsm = enableAsm;
    }

    public void init(Engine engine) {
        logger = engine.getLogger();
    }
}
