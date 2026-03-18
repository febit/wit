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
package org.febit.wit.runtime.accessor.impl;

import org.febit.wit.runtime.accessor.Getter;
import org.febit.wit.runtime.accessor.Setter;
import org.jspecify.annotations.Nullable;

import java.util.Map;

public class MapAccessor<T extends Map<?, ?>> implements Getter<T>, Setter<T> {

    @Override
    @Nullable
    public Object get(T map, @Nullable Object property) {
        return map.get(property);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void set(T map, @Nullable Object property, @Nullable Object value) {
        ((Map<Object, Object>) map).put(property, value);
    }
}
