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

import org.febit.wit.runtime.Undefined;
import org.febit.wit.runtime.accessor.Getter;
import org.febit.wit.runtime.accessor.Setter;
import org.febit.wit.runtime.heap.Heap;
import org.jspecify.annotations.Nullable;

public class HeapAccessor implements Getter<Heap>, Setter<Heap> {

    @Nullable
    @Override
    public Object get(Heap heap, @Nullable Object property) {
        if (property == null) {
            return Undefined.UNDEFINED;
        }
        return heap.get(property.toString());
    }

    @Override
    public void set(Heap heap, @Nullable Object property, @Nullable Object value) {
        if (property == null) {
            // Ignore assignment to null property
            return;
        }
        heap.set(property.toString(), value);
    }
}
