// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script.resolvers;

import java.util.ArrayList;
import webit.script.Engine;
import webit.script.Initable;
import webit.script.asm.AsmResolverManager;
import webit.script.exceptions.ScriptRuntimeException;
import webit.script.io.Out;
import webit.script.loggers.Logger;
import webit.script.resolvers.impl.CommonResolver;
import webit.script.util.ClassEntry;
import webit.script.util.collection.ClassIdentityHashMap;

/**
 *
 * @author Zqq
 */
public final class ResolverManager implements Initable {

    private Logger logger;
    //
    private final ClassIdentityHashMap<GetResolver> getResolverMap;
    private final ClassIdentityHashMap<SetResolver> setResolverMap;
    private final ClassIdentityHashMap<OutResolver> outResolverMap;
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
    private boolean ignoreNullPointer = false;
    private ClassEntry[] resolvers;
    //

    public ResolverManager() {
        getResolverMap = new ClassIdentityHashMap<GetResolver>();
        setResolverMap = new ClassIdentityHashMap<SetResolver>();
        outResolverMap = new ClassIdentityHashMap<OutResolver>();

        getResolvers = new ArrayList<GetResolver>();
        setResolvers = new ArrayList<SetResolver>();
        outResolvers = new ArrayList<OutResolver>();
        getResolverTypes = new ArrayList<Class>();
        setResolverTypes = new ArrayList<Class>();
        outResolverTypes = new ArrayList<Class>();

        commonResolver = new CommonResolver();
    }

    private GetResolver getGetResolver(final Class type) {
        GetResolver resolver;
        return (resolver = getResolverMap.unsafeGet(type)) != null
                ? resolver
                : resolveGetResolver(type);
    }

    @SuppressWarnings("unchecked")
    private GetResolver resolveGetResolver(final Class type) {
        GetResolver resolver;

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
                    resolver = AsmResolverManager.getAsmResolver(type);
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
        return resolver;
    }

    private SetResolver getSetResolver(final Class type) {
        SetResolver resolver;
        return (resolver = setResolverMap.unsafeGet(type)) != null
                ? resolver
                : resolveSetResolver(type);
    }

    @SuppressWarnings("unchecked")
    private SetResolver resolveSetResolver(final Class type) {
        SetResolver resolver;
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
                    resolver = AsmResolverManager.getAsmResolver(type);
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
        return resolver;
    }

    private OutResolver getOutResolver(final Class type) {
        OutResolver resolver;
        return (resolver = outResolverMap.unsafeGet(type)) != null
                ? resolver
                : resolveOutResolver(type);
    }

    @SuppressWarnings("unchecked")
    private OutResolver resolveOutResolver(final Class type) {
        OutResolver resolver;
        resolver = outResolverMap.get(type);
        if (resolver == null) {
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
        return resolver;
    }

    public void init(Engine engine) {
        this.logger = engine.getLogger();
        for (int i = 0, len = resolvers.length; i < len; i++) {
            Resolver resolver = (Resolver) engine.getComponent(resolvers[i]);
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
        if (bean != null) {
            return getGetResolver(bean.getClass()).get(bean, property);
        } else if (ignoreNullPointer) {
            return null;
        } else {
            throw new ScriptRuntimeException("Null pointer.");
        }
    }

    public void set(Object bean, Object property, Object value) {
        if (bean != null) {
            getSetResolver(bean.getClass()).set(bean, property, value);
            return;
        } else if (ignoreNullPointer == false) {
            throw new ScriptRuntimeException("Null pointer.");
        }
    }

    public void render(final Out out, final Object bean) {
        getOutResolver(bean.getClass()).render(out, bean);
    }

    public void setEnableAsm(boolean enableAsm) {
        this.enableAsm = enableAsm;
    }

    public void setIgnoreNullPointer(boolean ignoreNullPointer) {
        this.ignoreNullPointer = ignoreNullPointer;
    }

    public void setResolvers(ClassEntry[] resolvers) {
        this.resolvers = resolvers;
    }
}
