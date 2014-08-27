// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script.resolvers;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import webit.script.Engine;
import webit.script.Initable;
import webit.script.exceptions.ScriptRuntimeException;
import webit.script.io.Out;
import webit.script.loggers.Logger;
import webit.script.resolvers.impl.CommonResolver;
import webit.script.util.ClassEntry;
import webit.script.util.ClassMap;

/**
 *
 * @author Zqq
 */
public class ResolverManager implements Initable {

    protected Logger logger;

    protected final ClassMap<GetResolver> getResolverMap;
    protected final ClassMap<SetResolver> setResolverMap;
    protected final ClassMap<OutResolver> outResolverMap;

    protected final ArrayList<GetResolver> getResolvers;
    protected final ArrayList<SetResolver> setResolvers;
    protected final ArrayList<OutResolver> outResolvers;
    protected final ArrayList<Class> getResolverTypes;
    protected final ArrayList<Class> setResolverTypes;
    protected final ArrayList<Class> outResolverTypes;

    protected final CommonResolver commonResolver;
    //settings
    protected boolean ignoreNullPointer;
    protected ClassEntry[] resolvers;

    public ResolverManager() {
        this.ignoreNullPointer = true;

        getResolverMap = new ClassMap<GetResolver>();
        setResolverMap = new ClassMap<SetResolver>();
        outResolverMap = new ClassMap<OutResolver>();

        getResolvers = new ArrayList<GetResolver>();
        setResolvers = new ArrayList<SetResolver>();
        outResolvers = new ArrayList<OutResolver>();
        getResolverTypes = new ArrayList<Class>();
        setResolverTypes = new ArrayList<Class>();
        outResolverTypes = new ArrayList<Class>();

        commonResolver = new CommonResolver();
    }

    protected GetResolver getGetResolver(final Class type) {
        GetResolver resolver;
        return (resolver = getResolverMap.unsafeGet(type)) != null
                ? resolver
                : resolveGetResolverIfAbsent(type);
    }

    @SuppressWarnings("unchecked")
    protected GetResolver resolveGetResolverIfAbsent(final Class type) {
        GetResolver resolver = getResolverMap.get(type);
        if (resolver == null) {
            ArrayList<Class> types = getResolverTypes;
            for (int i = 0, len = types.size(); i < len; i++) {
                if (types.get(i).isAssignableFrom(type)) {
                    resolver = getResolvers.get(i);
                    break;
                }
            }
            if (resolver == null) {
                resolver = resolveGetResolver(type);
            }
            resolver = getResolverMap.putIfAbsent(type, resolver);
        }
        return resolver;
    }

    protected GetResolver resolveGetResolver(final Class type) {
        return commonResolver;
    }

    protected SetResolver getSetResolver(final Class type) {
        SetResolver resolver;
        return (resolver = setResolverMap.unsafeGet(type)) != null
                ? resolver
                : resolveSetResolverIfAbsent(type);
    }

    @SuppressWarnings("unchecked")
    protected SetResolver resolveSetResolverIfAbsent(final Class type) {
        SetResolver resolver;
        resolver = setResolverMap.get(type);
        if (resolver == null) {
            ArrayList<Class> types = setResolverTypes;
            for (int i = 0, len = types.size(); i < len; i++) {
                if (types.get(i).isAssignableFrom(type)) {
                    resolver = setResolvers.get(i);
                    break;
                }
            }
            if (resolver == null) {
                resolver = resolveSetResolver(type);
            }
            resolver = setResolverMap.putIfAbsent(type, resolver);
        }
        return resolver;
    }

    protected SetResolver resolveSetResolver(final Class type) {
        return commonResolver;
    }

    protected OutResolver getOutResolver(final Class type) {
        OutResolver resolver;
        return (resolver = outResolverMap.unsafeGet(type)) != null
                ? resolver
                : resolveOutResolver(type);
    }

    @SuppressWarnings("unchecked")
    protected OutResolver resolveOutResolver(final Class type) {
        OutResolver resolver;
        resolver = outResolverMap.get(type);
        if (resolver == null) {
            ArrayList<Class> types = outResolverTypes;
            for (int i = 0, len = types.size(); i < len; i++) {
                if (types.get(i).isAssignableFrom(type)) {
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
        if (resolvers != null) {
            for (ClassEntry item : resolvers) {
                Resolver resolver = (Resolver) engine.getComponent(item);
                registResolver(resolver.getMatchClass(), resolver);
                if (resolver instanceof RegistModeResolver) {
                    ((RegistModeResolver) resolver).regist(this);
                }
            }
            getResolvers.trimToSize();
            setResolvers.trimToSize();
            outResolvers.trimToSize();
            getResolverTypes.trimToSize();
            setResolverTypes.trimToSize();
            outResolverTypes.trimToSize();
        }
    }

    public void registResolver(Class type, Resolver resolver) {
        if (type == null) {
            return;
        }

        int modifier = type.getModifiers();
        boolean notAbstract = !Modifier.isAbstract(modifier) || type.isArray();
        boolean notFinal = !Modifier.isFinal(modifier) || Object[].class.isAssignableFrom(type);

        if (resolver instanceof GetResolver) {
            if (notFinal) {
                getResolverTypes.add(type);
                getResolvers.add((GetResolver) resolver);
            }
            if (notAbstract) {
                getResolverMap.putIfAbsent(type, (GetResolver) resolver);
            }
        }
        if (resolver instanceof SetResolver) {
            if (notFinal) {
                setResolverTypes.add(type);
                setResolvers.add((SetResolver) resolver);
            }
            if (notAbstract) {
                setResolverMap.putIfAbsent(type, (SetResolver) resolver);
            }
        }
        if (resolver instanceof OutResolver) {
            if (notFinal) {
                outResolverTypes.add(type);
                outResolvers.add((OutResolver) resolver);
            }
            if (notAbstract) {
                outResolverMap.putIfAbsent(type, (OutResolver) resolver);
            }
        }
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
        } else if (!ignoreNullPointer) {
            throw new ScriptRuntimeException("Null pointer.");
        }
    }

    public void render(final Out out, final Object bean) {
        getOutResolver(bean.getClass()).render(out, bean);
    }

    public void setIgnoreNullPointer(boolean ignoreNullPointer) {
        this.ignoreNullPointer = ignoreNullPointer;
    }

    public void setResolvers(ClassEntry[] resolvers) {
        this.resolvers = resolvers;
    }
}
