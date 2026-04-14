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
import org.febit.wit.engine.accessor.Accessor;
import org.febit.wit.engine.accessor.AccessorConsumer;
import org.febit.wit.engine.accessor.AccessorFactory;
import org.febit.wit.engine.accessor.Getter;
import org.febit.wit.engine.accessor.Renderer;
import org.febit.wit.engine.accessor.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class CompositeAccessorFactory implements AccessorFactory {

    private final TypeAccessors<Getter<?>> getters;
    private final TypeAccessors<Setter<?>> setters;
    private final TypeAccessors<Renderer<?>> renderers;

    public static Builder builder() {
        return new Builder();
    }

    private record TypeMapping<T>(Class<?> type, T value) {
    }

    private record TypeAccessors<T>(
            List<TypeMapping<T>> mappings,
            Function<Class<?>, T> fallback
    ) {
        /**
         * Find the accessor for the given type.
         * <p>
         * It will check the mappings in reverse order,
         * and return the first one that is assignable from the given type.
         * If no mapping is found, it will return the fallback accessor.
         */
        T find(final Class<?> type) {
            for (int i = mappings.size() - 1; i >= 0; i--) {
                var tuple = mappings.get(i);
                if (tuple.type().isAssignableFrom(type)) {
                    return tuple.value();
                }
            }
            return fallback.apply(type);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> Getter<T> getter(Class<T> type) {
        return (Getter<T>) getters.find(type);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> Setter<T> setter(Class<T> type) {
        return (Setter<T>) setters.find(type);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> Renderer<T> renderer(Class<T> type) {
        return (Renderer<T>) renderers.find(type);
    }

    public static class Builder implements AccessorConsumer {

        private final List<TypeMapping<Getter<?>>> getters = new ArrayList<>();
        private final List<TypeMapping<Setter<?>>> setters = new ArrayList<>();
        private final List<TypeMapping<Renderer<?>>> renderers = new ArrayList<>();

        private AccessorFactory fallback = ReflectBeanAccessorFactory.get();

        private boolean withBuiltIn = true;

        @Override
        public <T> void accept(Class<T> type, Accessor<? extends T> accessor) {
            accessor(type, accessor);
        }

        public Builder fallback(AccessorFactory factory) {
            this.fallback = factory;
            return this;
        }

        public Builder withBuiltIn(boolean with) {
            this.withBuiltIn = with;
            return this;
        }

        public <T> Builder accessor(Class<T> type, Accessor<? extends T> accessor) {
            // TODO check conflicts with existing accessors?
            if (accessor instanceof Getter<?> getter) {
                getters.add(new TypeMapping<>(type, getter));
            }
            if (accessor instanceof Setter<?> setter) {
                setters.add(new TypeMapping<>(type, setter));
            }
            if (accessor instanceof Renderer<?> renderer) {
                renderers.add(new TypeMapping<>(type, renderer));
            }
            return this;
        }

        public CompositeAccessorFactory build() {
            if (withBuiltIn) {
                BuiltInAccessors.registerAll(this::accessor);
            }
            return new CompositeAccessorFactory(
                    new TypeAccessors<>(List.copyOf(getters), fallback::getter),
                    new TypeAccessors<>(List.copyOf(setters), fallback::setter),
                    new TypeAccessors<>(List.copyOf(renderers), fallback::renderer)
            );
        }
    }
}
