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

import org.febit.wit.exception.ScriptEvaluateException;
import org.febit.wit.runtime.accessor.Getter;
import org.febit.wit.runtime.accessor.Setter;
import org.jspecify.annotations.Nullable;

import java.util.Collection;
import java.util.List;

public class CollectionAccessor<T extends Collection<?>> implements Getter<T>, Setter<T> {

    @Nullable
    @Override
    public Object get(T collection, @Nullable Object property) {
        if (property == null) {
            throw new ScriptEvaluateException("property should not be null for collection access.");
        }
        if (property instanceof Number number
                && collection instanceof List<?> list) {
            try {
                return list.get(number.intValue());
            } catch (IndexOutOfBoundsException e) {
                throw new ScriptEvaluateException("index out of bounds: " + number.intValue(), e);
            }
        }
        return switch (property.toString()) {
            case "size", "length" -> collection.size();
            case "isEmpty" -> collection.isEmpty();
            default -> throw new ScriptEvaluateException(
                    "unsupported property for collection access: " + property
            );
        };
    }

    @Override
    @SuppressWarnings("unchecked")
    public void set(T collection, @Nullable Object property, @Nullable Object value) {
        if (!(property instanceof Number number)) {
            throw new ScriptEvaluateException("property should be a number for collection access.");
        }
        if (!(collection instanceof List<?> list)) {
            throw new ScriptEvaluateException("collection should be a List for indexed access.");
        }
        var index = number.intValue();
        var size = list.size();
        if (index >= size) {
            for (int i = index - size; i != 0; i--) {
                list.add(null);
            }
            ((List<Object>) list).add(value);
            return;
        }
        ((List<Object>) list).set(index, value);
    }
}
