// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.resolvers;

import org.febit.wit.Init;
import org.febit.wit.exceptions.ScriptRuntimeException;
import org.febit.wit.loggers.Logger;
import org.febit.wit.resolvers.impl.CommonResolver;
import org.febit.wit.util.ClassMap;

import java.lang.reflect.Modifier;
import java.util.ArrayList;

/**
 * @author zqq90
 */
public class ResolverManager {

    protected Logger logger;

    protected boolean ignoreNullPointer = true;
    protected Resolver[] resolvers;

    public final ClassMap<GetResolver> getters;
    public final ClassMap<SetResolver> setters;
    public final ClassMap<OutResolver> outers;

    protected final ArrayList<GetResolver> getResolvers;
    protected final ArrayList<SetResolver> setResolvers;
    protected final ArrayList<OutResolver> outResolvers;
    protected final ArrayList<Class> getResolverTypes;
    protected final ArrayList<Class> setResolverTypes;
    protected final ArrayList<Class> outResolverTypes;
    protected final CommonResolver commonResolver;

    public ResolverManager() {

        getters = new ClassMap<>();
        setters = new ClassMap<>();
        outers = new ClassMap<>();

        getResolvers = new ArrayList<>();
        setResolvers = new ArrayList<>();
        outResolvers = new ArrayList<>();
        getResolverTypes = new ArrayList<>();
        setResolverTypes = new ArrayList<>();
        outResolverTypes = new ArrayList<>();

        commonResolver = new CommonResolver();
    }

    @SuppressWarnings("unchecked")
    protected static int lookup(ArrayList<Class> types, final Class<?> type) {
        int i = types.size();
        while (i > 0) {
            --i;
            if (types.get(i).isAssignableFrom(type)) {
                return i;
            }
        }
        return -1;
    }

    protected GetResolver resolveGetResolverIfAbsent(final Class<?> type) {
        GetResolver resolver = getters.get(type);
        if (resolver != null) {
            return resolver;
        }
        final int index = lookup(getResolverTypes, type);
        resolver = index >= 0
                ? getResolvers.get(index)
                : resolveGetResolver(type);
        return getters.putIfAbsent(type, resolver);
    }

    protected GetResolver resolveGetResolver(final Class<?> type) {
        return commonResolver;
    }

    protected SetResolver resolveSetResolverIfAbsent(final Class<?> type) {
        SetResolver resolver;
        resolver = setters.get(type);
        if (resolver != null) {
            return resolver;
        }
        final int index = lookup(setResolverTypes, type);
        resolver = index >= 0
                ? setResolvers.get(index)
                : resolveSetResolver(type);
        return setters.putIfAbsent(type, resolver);
    }

    protected SetResolver resolveSetResolver(final Class<?> type) {
        return commonResolver;
    }

    public OutResolver resolveOutResolver(final Class<?> type) {
        OutResolver resolver;
        resolver = outers.get(type);
        if (resolver != null) {
            return resolver;
        }
        final int index = lookup(outResolverTypes, type);
        resolver = index >= 0
                ? outResolvers.get(index)
                : commonResolver;
        return outers.putIfAbsent(type, resolver);
    }

    @Init
    public void init() {
        if (resolvers != null) {
            for (Resolver resolver : resolvers) {
                resolver.register(this);
            }
            getResolvers.trimToSize();
            setResolvers.trimToSize();
            outResolvers.trimToSize();
            getResolverTypes.trimToSize();
            setResolverTypes.trimToSize();
            outResolverTypes.trimToSize();
        }
    }

    public void registerResolver(Class<?> type, Resolver resolver) {
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
                outers.putIfAbsent(type, (OutResolver) resolver);
            }
        }
    }

    @SuppressWarnings("unchecked")
    public final Object get(Object bean, Object property) {
        if (bean != null) {
            return resolveGetResolverIfAbsent(bean.getClass()).get(bean, property);
        }
        return handleNullPointer();
    }

    @SuppressWarnings("unchecked")
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
}
