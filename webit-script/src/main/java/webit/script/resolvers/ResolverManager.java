// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script.resolvers;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import webit.script.Engine;
import webit.script.Initable;
import webit.script.exceptions.ScriptRuntimeException;
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

    public final ClassMap<GetResolver> getters;
    public final ClassMap<SetResolver> setters;
    public final ClassMap<OutResolver> outters;

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

        getters = new ClassMap<GetResolver>();
        setters = new ClassMap<SetResolver>();
        outters = new ClassMap<OutResolver>();

        getResolvers = new ArrayList<GetResolver>();
        setResolvers = new ArrayList<SetResolver>();
        outResolvers = new ArrayList<OutResolver>();
        getResolverTypes = new ArrayList<Class>();
        setResolverTypes = new ArrayList<Class>();
        outResolverTypes = new ArrayList<Class>();

        commonResolver = new CommonResolver();
    }

    @SuppressWarnings("unchecked")
    protected GetResolver resolveGetResolverIfAbsent(final Class type) {
        GetResolver resolver = getters.get(type);
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
            resolver = getters.putIfAbsent(type, resolver);
        }
        return resolver;
    }

    protected GetResolver resolveGetResolver(final Class type) {
        return commonResolver;
    }

    @SuppressWarnings("unchecked")
    protected SetResolver resolveSetResolverIfAbsent(final Class type) {
        SetResolver resolver;
        resolver = setters.get(type);
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
            resolver = setters.putIfAbsent(type, resolver);
        }
        return resolver;
    }

    protected SetResolver resolveSetResolver(final Class type) {
        return commonResolver;
    }

    @SuppressWarnings("unchecked")
    public OutResolver resolveOutResolver(final Class type) {
        OutResolver resolver;
        resolver = outters.get(type);
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
            resolver = outters.putIfAbsent(type, resolver);
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
                getters.putIfAbsent(type, (GetResolver) resolver);
            }
        }
        if (resolver instanceof SetResolver) {
            if (notFinal) {
                setResolverTypes.add(type);
                setResolvers.add((SetResolver) resolver);
            }
            if (notAbstract) {
                setters.putIfAbsent(type, (SetResolver) resolver);
            }
        }
        if (resolver instanceof OutResolver) {
            if (notFinal) {
                outResolverTypes.add(type);
                outResolvers.add((OutResolver) resolver);
            }
            if (notAbstract) {
                outters.putIfAbsent(type, (OutResolver) resolver);
            }
        }
    }

    public final Object get(Object bean, Object property) {
        if (bean != null) {
            return resolveGetResolverIfAbsent(bean.getClass()).get(bean, property);
        }
        return handleNullPointer();
    }

    public final void set(Object bean, Object property, Object value) {
        if (bean != null) {
            resolveSetResolverIfAbsent(bean.getClass()).set(bean, property, value);
            return;
        }
        handleNullPointer();
    }

    protected final Object handleNullPointer() {
        if (this.ignoreNullPointer) {
            return null;
        }
        throw new ScriptRuntimeException("Null pointer.");
    }

    public void setIgnoreNullPointer(boolean ignoreNullPointer) {
        this.ignoreNullPointer = ignoreNullPointer;
    }

    public void setResolvers(ClassEntry[] resolvers) {
        this.resolvers = resolvers;
    }
}
