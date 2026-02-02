// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.runtime.accessor;

import lombok.experimental.UtilityClass;
import org.febit.wit.runtime.ALU;
import org.febit.wit.runtime.Undefined;
import org.febit.wit.runtime.accessor.impl.ByteArrayRender;
import org.febit.wit.runtime.accessor.impl.CharArrayRender;
import org.febit.wit.runtime.accessor.impl.CharSequenceAccessor;
import org.febit.wit.runtime.accessor.impl.CollectionAccessor;
import org.febit.wit.runtime.accessor.impl.HeapAccessor;
import org.febit.wit.runtime.accessor.impl.InternalVoidAccessor;
import org.febit.wit.runtime.accessor.impl.IterGetter;
import org.febit.wit.runtime.accessor.impl.MapAccessor;
import org.febit.wit.runtime.accessor.impl.ObjectArrayAccessor;
import org.febit.wit.runtime.heap.Heap;
import org.febit.wit.runtime.iter.Iter;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;

@UtilityClass
public class PredefinedAccessors {

    public static void registerAll(AccessorConsumer consumer) {
        consumer.accept(Undefined.class, new InternalVoidAccessor());
        consumer.accept(CharSequence.class, new CharSequenceAccessor());

        forArrays(consumer);

        consumer.accept(Heap.class, new HeapAccessor());
        consumer.accept(Iter.class, new IterGetter());

        consumer.accept(Collection.class, new CollectionAccessor<>());
        consumer.accept(Map.class, new MapAccessor<>());
    }

    public static void forArrays(AccessorConsumer consumer) {
        consumer.accept(byte[].class, new ByteArrayRender());
        consumer.accept(char[].class, new CharArrayRender());

        consumer.accept(Object[].class, new ObjectArrayAccessor());

        consumer.accept(boolean[].class,
                (PrimitiveArrayGetter<boolean[]>) (array, index) -> array[index]);
        consumer.accept(char[].class,
                (PrimitiveArrayGetter<char[]>) (array, index) -> array[index]);
        consumer.accept(int[].class,
                (PrimitiveArrayGetter<int[]>) (array, index) -> array[index]);
        consumer.accept(float[].class,
                (PrimitiveArrayGetter<float[]>) (array, index) -> array[index]);
        consumer.accept(double[].class,
                (PrimitiveArrayGetter<double[]>) (array, index) -> array[index]);
        consumer.accept(long[].class,
                (PrimitiveArrayGetter<long[]>) (array, index) -> array[index]);
        consumer.accept(short[].class,
                (PrimitiveArrayGetter<short[]>) (array, index) -> array[index]);
        consumer.accept(byte[].class,
                (PrimitiveArrayGetter<byte[]>) (array, index) -> array[index]);

        consumer.accept(boolean[].class,
                (PrimitiveArraySetter<boolean[], Object>) (array, index, value) ->
                        array[index] = ALU.isTruly(value));
        consumer.accept(char[].class,
                (PrimitiveArraySetter<char[], Character>) (array, index, value) ->
                        array[index] = Objects.requireNonNull(value));
        consumer.accept(int[].class,
                (PrimitiveArraySetter<int[], Number>) (array, index, value) ->
                        array[index] = Objects.requireNonNull(value).intValue());
        consumer.accept(float[].class,
                (PrimitiveArraySetter<float[], Number>) (array, index, value) ->
                        array[index] = Objects.requireNonNull(value).floatValue());
        consumer.accept(double[].class,
                (PrimitiveArraySetter<double[], Number>) (array, index, value) ->
                        array[index] = Objects.requireNonNull(value).doubleValue());
        consumer.accept(long[].class,
                (PrimitiveArraySetter<long[], Number>) (array, index, value) ->
                        array[index] = Objects.requireNonNull(value).longValue());
        consumer.accept(short[].class,
                (PrimitiveArraySetter<short[], Number>) (array, index, value) ->
                        array[index] = Objects.requireNonNull(value).shortValue());
        consumer.accept(byte[].class,
                (PrimitiveArraySetter<byte[], Number>) (array, index, value) ->
                        array[index] = Objects.requireNonNull(value).byteValue());
    }
}
