// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.runtime.accessor;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.febit.wit.extern.asm.AsmBeanAccessorFactory;
import org.febit.wit.util.ClassMap;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class DefaultAccessorFactory implements AccessorFactory {

    private static final ReflectBeanAccessorFactory REFLECT_FACTORY = new ReflectBeanAccessorFactory();

    private final ClassMap<Getter<?>> mappedGetters = new ClassMap<>();
    private final ClassMap<Setter<?>> mappedSetters = new ClassMap<>();
    private final ClassMap<Render<?>> mappedRenders = new ClassMap<>();

    private final List<TypedTuple<Getter<?>>> getters;
    private final List<TypedTuple<Setter<?>>> setters;
    private final List<TypedTuple<Render<?>>> renders;

    private final AccessorFactory fallbackFactory;

    @Nullable
    private static <T> T lookup(List<TypedTuple<T>> tuples, final Class<?> type) {
        for (int i = tuples.size() - 1; i >= 0; i--) {
            var tuple = tuples.get(i);
            if (tuple.type().isAssignableFrom(type)) {
                return tuple.value();
            }
        }
        return null;
    }

    public static Builder builder() {
        return new Builder();
    }

    private record TypedTuple<T>(Class<?> type, T value) {
        public static <T> TypedTuple<T> of(Class<?> type, T value) {
            return new TypedTuple<>(type, value);
        }
    }

    @SuppressWarnings("unchecked")
    private <T> Getter<T> resolveGetter(final Class<?> type) {
        var getter = mappedGetters.get(type);
        if (getter != null) {
            return (Getter<T>) getter;
        }
        getter = lookup(getters, type);
        if (getter == null) {
            getter = fallbackFactory.getter(type);
        }
        return (Getter<T>) mappedGetters.putIfAbsent(type, getter);
    }

    @SuppressWarnings("unchecked")
    private <T> Setter<T> resolveSetter(final Class<?> type) {
        var setter = mappedSetters.get(type);
        if (setter != null) {
            return (Setter<T>) setter;
        }
        setter = lookup(setters, type);
        if (setter == null) {
            setter = fallbackFactory.setter(type);
        }
        return (Setter<T>) mappedSetters.putIfAbsent(type, setter);
    }

    @SuppressWarnings("unchecked")
    private <T> Render<T> resolveRender(final Class<?> type) {
        var render = mappedRenders.get(type);
        if (render != null) {
            return (Render<T>) render;
        }
        render = lookup(renders, type);
        if (render == null) {
            render = fallbackFactory.render(type);
        }
        return (Render<T>) mappedRenders.putIfAbsent(type, render);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> Getter<T> getter(Class<T> type) {
        var getter = (Getter<T>) this.mappedGetters.unsafeGet(type);
        if (getter != null) {
            return getter;
        }
        return resolveGetter(type);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> Render<T> render(Class<T> type) {
        var render = (Render<T>) this.mappedRenders.unsafeGet(type);
        if (render != null) {
            return render;
        }
        return resolveRender(type);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> Setter<T> setter(Class<T> type) {
        var setter = (Setter<T>) this.mappedSetters.unsafeGet(type);
        if (setter != null) {
            return setter;
        }
        return resolveSetter(type);
    }

    public static class Builder {

        private final List<TypedTuple<Getter<?>>> getters = new ArrayList<>();
        private final List<TypedTuple<Setter<?>>> setters = new ArrayList<>();
        private final List<TypedTuple<Render<?>>> renders = new ArrayList<>();

        private boolean withPredefined = true;
        private AccessorFactory fallback = REFLECT_FACTORY;

        public Builder fallback(AccessorFactory factory) {
            this.fallback = factory;
            return this;
        }

        public Builder fallbackWithReflect() {
            this.fallback = REFLECT_FACTORY;
            return this;
        }

        public Builder fallbackWithAsm() {
            this.fallback = new AsmBeanAccessorFactory();
            return this;
        }

        public Builder withPredefined(boolean with) {
            this.withPredefined = with;
            return this;
        }

        public <T> Builder accessor(Class<T> type, Accessor<? extends T> accessor) {
            // TODO check conflicts with existing accessors?
            if (accessor instanceof Getter<?> getter) {
                getters.add(TypedTuple.of(type, getter));
            }
            if (accessor instanceof Setter<?> setter) {
                setters.add(TypedTuple.of(type, setter));
            }
            if (accessor instanceof Render<?> render) {
                renders.add(TypedTuple.of(type, render));
            }
            return this;
        }

        public DefaultAccessorFactory build() {
            if (withPredefined) {
                PredefinedAccessors.registerAll(this::accessor);
            }
            return new DefaultAccessorFactory(
                    List.copyOf(getters),
                    List.copyOf(setters),
                    List.copyOf(renders),
                    fallback
            );
        }
    }
}
