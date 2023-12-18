// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.resolvers;

import jakarta.annotation.Nullable;
import org.febit.wit.Init;
import org.febit.wit.exceptions.ScriptRuntimeException;
import org.febit.wit.lang.Out;
import org.febit.wit.loggers.Logger;
import org.febit.wit.resolvers.impl.CommonSerializer;
import org.febit.wit.util.ClassMap;

import java.lang.reflect.Modifier;
import java.util.ArrayList;

/**
 * @author zqq90
 */
public class ResolverManager implements Accessor {

    protected Logger logger;

    protected boolean ignoreNullPointer = true;
    protected Resolver[] resolvers;

    protected final ClassMap<GetResolver> getterMapping;
    protected final ClassMap<SetResolver> setterMapping;
    protected final ClassMap<Serializer> serializerMapping;

    protected final ArrayList<GetResolver> getResolvers;
    protected final ArrayList<SetResolver> setResolvers;
    protected final ArrayList<Serializer> serializers;
    protected final ArrayList<Class> getResolverTypes;
    protected final ArrayList<Class> setResolverTypes;
    protected final ArrayList<Class> outResolverTypes;
    protected final CommonSerializer commonResolver;

    public ResolverManager() {

        getterMapping = new ClassMap<>();
        setterMapping = new ClassMap<>();
        serializerMapping = new ClassMap<>();

        getResolvers = new ArrayList<>();
        setResolvers = new ArrayList<>();
        serializers = new ArrayList<>();
        getResolverTypes = new ArrayList<>();
        setResolverTypes = new ArrayList<>();
        outResolverTypes = new ArrayList<>();

        commonResolver = new CommonSerializer();
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
        var resolver = getterMapping.get(type);
        if (resolver != null) {
            return resolver;
        }
        final int index = lookup(getResolverTypes, type);
        resolver = index >= 0
                ? getResolvers.get(index)
                : resolveGetResolver(type);
        return getterMapping.putIfAbsent(type, resolver);
    }

    protected GetResolver resolveGetResolver(final Class<?> type) {
        return commonResolver;
    }

    protected SetResolver resolveSetResolverIfAbsent(final Class<?> type) {
        SetResolver resolver;
        resolver = setterMapping.get(type);
        if (resolver != null) {
            return resolver;
        }
        final int index = lookup(setResolverTypes, type);
        resolver = index >= 0
                ? setResolvers.get(index)
                : resolveSetResolver(type);
        return setterMapping.putIfAbsent(type, resolver);
    }

    protected SetResolver resolveSetResolver(final Class<?> type) {
        return commonResolver;
    }

    @Override
    public void write(Out out, @Nullable Object obj) {
        if (obj == null) {
            return;
        }
        var type = obj.getClass();
        if (type == String.class) {
            out.write((String) obj);
            return;
        }
        var resolver = this.serializerMapping.unsafeGet(type);
        if (resolver != null) {
            resolver.render(out, obj);
            return;
        }
        resolveOutResolver(type).render(out, obj);
    }

    public Serializer resolveOutResolver(final Class<?> type) {
        Serializer resolver;
        resolver = serializerMapping.get(type);
        if (resolver != null) {
            return resolver;
        }
        final int index = lookup(outResolverTypes, type);
        resolver = index >= 0
                ? serializers.get(index)
                : commonResolver;
        return serializerMapping.putIfAbsent(type, resolver);
    }

    @Init
    public void init() {
        if (resolvers != null) {
            for (var resolver : resolvers) {
                resolver.register(this);
            }
            getResolvers.trimToSize();
            setResolvers.trimToSize();
            serializers.trimToSize();
            getResolverTypes.trimToSize();
            setResolverTypes.trimToSize();
            outResolverTypes.trimToSize();
        }
    }

    @SuppressWarnings({
            "squid:S3776" // Cognitive Complexity of methods should not be too high
    })
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
                getterMapping.putIfAbsent(type, (GetResolver) resolver);
            }
        }
        if (resolver instanceof SetResolver) {
            if (notFinal) {
                setResolverTypes.add(type);
                setResolvers.add((SetResolver) resolver);
            }
            if (notAbstract) {
                setterMapping.putIfAbsent(type, (SetResolver) resolver);
            }
        }
        if (resolver instanceof Serializer) {
            if (notFinal) {
                outResolverTypes.add(type);
                serializers.add((Serializer) resolver);
            }
            if (notAbstract) {
                serializerMapping.putIfAbsent(type, (Serializer) resolver);
            }
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public final Object get(@Nullable Object bean, Object property) {
        if (bean == null) {
            return handleNullPointer();
        }
        var resolver = this.getterMapping.unsafeGet(bean.getClass());
        if (resolver != null) {
            return resolver.get(bean, property);
        }
        return resolveGetResolverIfAbsent(bean.getClass()).get(bean, property);
    }

    @Override
    @SuppressWarnings("unchecked")
    public final void set(@Nullable Object bean, Object property, Object value) {
        if (bean == null) {
            handleNullPointer();
            return;
        }
        var resolver = this.setterMapping.unsafeGet(bean.getClass());
        if (resolver != null) {
            resolver.set(bean, property, value);
            return;
        }
        resolveSetResolverIfAbsent(bean.getClass()).set(bean, property, value);
    }

    protected final Object handleNullPointer() {
        if (this.ignoreNullPointer) {
            return null;
        }
        throw new ScriptRuntimeException("Null pointer.");
    }
}
