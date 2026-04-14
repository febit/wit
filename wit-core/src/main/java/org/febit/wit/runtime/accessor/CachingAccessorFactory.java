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

import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import org.febit.wit.engine.accessor.AccessorFactory;
import org.febit.wit.engine.accessor.Getter;
import org.febit.wit.engine.accessor.Renderer;
import org.febit.wit.engine.accessor.Setter;
import org.febit.wit.util.ClassMap;

@Accessors(fluent = true)
@RequiredArgsConstructor(staticName = "of")
public class CachingAccessorFactory implements AccessorFactory {

    @lombok.Getter
    private final AccessorFactory delegate;

    private final ClassMap<Getter<?>> getters = new ClassMap<>();
    private final ClassMap<Setter<?>> setters = new ClassMap<>();
    private final ClassMap<Renderer<?>> renderers = new ClassMap<>();

    @Override
    @SuppressWarnings("unchecked")
    public <T> Getter<T> getter(Class<T> type) {
        var getter = (Getter<T>) getters.get(type);
        if (getter != null) {
            return getter;
        }
        return (Getter<T>) getters.computeIfAbsent(type, delegate::getter);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> Setter<T> setter(Class<T> type) {
        var setter = (Setter<T>) setters.get(type);
        if (setter != null) {
            return setter;
        }
        return (Setter<T>) setters.computeIfAbsent(type, delegate::setter);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> Renderer<T> renderer(Class<T> type) {
        var renderer = (Renderer<T>) renderers.get(type);
        if (renderer != null) {
            return renderer;
        }
        return (Renderer<T>) renderers.computeIfAbsent(type, delegate::renderer);
    }
}
