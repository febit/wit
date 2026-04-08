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

import lombok.experimental.UtilityClass;
import org.febit.wit.runtime.Undefined;
import org.febit.wit.runtime.accessor.impl.ByteArrayRenderer;
import org.febit.wit.runtime.accessor.impl.CharArrayRenderer;
import org.febit.wit.runtime.accessor.impl.CharSequenceAccessor;
import org.febit.wit.runtime.accessor.impl.CollectionAccessor;
import org.febit.wit.runtime.accessor.impl.GenericArrayAccessor;
import org.febit.wit.runtime.accessor.impl.HeapAccessor;
import org.febit.wit.runtime.accessor.impl.IterGetter;
import org.febit.wit.runtime.accessor.impl.MapAccessor;
import org.febit.wit.runtime.accessor.impl.ObjectArrayAccessor;
import org.febit.wit.runtime.accessor.impl.UndefinedAccessor;
import org.febit.wit.runtime.heap.Heap;
import org.febit.wit.runtime.iter.Iter;

import java.util.Collection;
import java.util.Map;

@UtilityClass
public class PresetAccessors {

    public static void registerAll(AccessorConsumer consumer) {
        consumer.accept(Undefined.class, new UndefinedAccessor());
        consumer.accept(CharSequence.class, new CharSequenceAccessor());

        forArrays(consumer);

        consumer.accept(Heap.class, new HeapAccessor());
        consumer.accept(Iter.class, new IterGetter());

        consumer.accept(Collection.class, new CollectionAccessor<>());
        consumer.accept(Map.class, new MapAccessor<>());
    }

    public static void forArrays(AccessorConsumer consumer) {
        consumer.accept(byte[].class, new ByteArrayRenderer());
        consumer.accept(char[].class, new CharArrayRenderer());

        consumer.accept(Object[].class, new ObjectArrayAccessor());

        consumer.accept(boolean[].class, GenericArrayAccessor.forBoolean());
        consumer.accept(byte[].class, GenericArrayAccessor.forByte());
        consumer.accept(short[].class, GenericArrayAccessor.forShort());
        consumer.accept(char[].class, GenericArrayAccessor.forChar());
        consumer.accept(int[].class, GenericArrayAccessor.forInt());
        consumer.accept(float[].class, GenericArrayAccessor.forFloat());
        consumer.accept(double[].class, GenericArrayAccessor.forDouble());
        consumer.accept(long[].class, GenericArrayAccessor.forLong());
    }
}
