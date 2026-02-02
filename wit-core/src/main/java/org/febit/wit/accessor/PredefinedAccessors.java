// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.accessor;

import lombok.experimental.UtilityClass;
import org.febit.wit.accessor.impl.BagAccessor;
import org.febit.wit.accessor.impl.ByteArrayRender;
import org.febit.wit.accessor.impl.CharArrayRender;
import org.febit.wit.accessor.impl.CharSequenceAccessor;
import org.febit.wit.accessor.impl.CollectionAccessor;
import org.febit.wit.accessor.impl.InternalVoidAccessor;
import org.febit.wit.accessor.impl.IterGetter;
import org.febit.wit.accessor.impl.MapAccessor;
import org.febit.wit.accessor.impl.ObjectArrayAccessor;
import org.febit.wit.lang.ALU;
import org.febit.wit.lang.Bag;
import org.febit.wit.lang.InternalVoid;
import org.febit.wit.lang.Iter;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;

@UtilityClass
public class PredefinedAccessors {

    public static void registerAll(AccessorConsumer consumer) {
        consumer.accept(InternalVoid.class, new InternalVoidAccessor());
        consumer.accept(CharSequence.class, new CharSequenceAccessor());

        forArrays(consumer);

        consumer.accept(Bag.class, new BagAccessor());
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
