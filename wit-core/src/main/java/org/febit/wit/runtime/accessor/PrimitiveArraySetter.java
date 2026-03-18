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

import org.febit.wit.exception.ScriptEvaluateException;
import org.jspecify.annotations.Nullable;

@FunctionalInterface
public interface PrimitiveArraySetter<T, C> extends Setter<T> {

    void setValue(T array, int idx, @Nullable C value);

    @Override
    @SuppressWarnings("unchecked")
    default void set(T array, @Nullable Object property, @Nullable Object value) {
        if (property == null) {
            throw new ScriptEvaluateException("Array index is null.");
        }
        if (property instanceof Number idx) {
            try {
                setValue(array, idx.intValue(), (C) value);
                return;
            } catch (ArrayIndexOutOfBoundsException e) {
                throw new ScriptEvaluateException("Array index out of bounds, index=" + idx, e);
            } catch (ClassCastException e) {
                throw new ScriptEvaluateException(e.getMessage(), e);
            }
        }
        throw new ScriptEvaluateException("Invalid property or can't write: array#" + property);
    }
}
