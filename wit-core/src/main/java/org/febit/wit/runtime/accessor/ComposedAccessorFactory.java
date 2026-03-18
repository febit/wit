/*
 * Copyright 2013-present febit.org (support@febit.org)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
public class ComposedAccessorFactory implements AccessorFactory {

    private final ClassMap<Getter<?>> mappedGetters = new ClassMap<>();
    private final ClassMap<Setter<?>> mappedSetters = new ClassMap<>();
    private final ClassMap<Render<?>> mappedRenders = new ClassMap<>();

    private final List<TypedTuple<Getter<?>>> getters;
    private final List<TypedTuple<Setter<?>>> setters;
    private final List<TypedTuple<Render<?>>> renders;

    private final AccessorFactory fallback;

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
            getter = fallback.getter(type);
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
            setter = fallback.setter(type);
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
            render = fallback.render(type);
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

        private AccessorFactory fallback = ReflectBeanAccessorFactory.INSTANCE;

        private boolean withPresets = true;

        public Builder fallback(AccessorFactory factory) {
            this.fallback = factory;
            return this;
        }

        public Builder fallbackWithReflect() {
            this.fallback = ReflectBeanAccessorFactory.INSTANCE;
            return this;
        }

        public Builder fallbackWithAsm() {
            this.fallback = new AsmBeanAccessorFactory();
            return this;
        }

        public Builder withPresets(boolean with) {
            this.withPresets = with;
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

        public ComposedAccessorFactory build() {
            if (withPresets) {
                PresetAccessors.registerAll(this::accessor);
            }
            return new ComposedAccessorFactory(
                    List.copyOf(getters),
                    List.copyOf(setters),
                    List.copyOf(renders),
                    fallback
            );
        }
    }
}
