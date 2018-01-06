// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.resolvers.impl;

import org.febit.wit.exceptions.ScriptRuntimeException;
import org.febit.wit.resolvers.GetResolver;
import org.febit.wit.resolvers.Resolver;
import org.febit.wit.resolvers.ResolverManager;
import org.febit.wit.resolvers.SetResolver;
import org.febit.wit.util.ALU;
import org.febit.wit.util.ArrayUtil;
import org.febit.wit.util.StringUtil;

/**
 *
 * @author zqq90
 */
public class PrimitiveArrayResolver implements Resolver {

    @FunctionalInterface
    protected interface InternalGetResolver<T> extends GetResolver<T> {

        Object getValue(T array, int property);

        @Override
        default Object get(T array, Object property) {
            if (property instanceof Number) {
                return getValue(array, ((Number) property).intValue());
            }
            switch (property.toString()) {
                case "size":
                case "length":
                    return ArrayUtil.getSize(array);
                case "isEmpty":
                    return ArrayUtil.getSize(array) == 0;
                default:
            }
            throw new ScriptRuntimeException(StringUtil.format("Invalid property: array#{}", property));
        }

        @Override
        default Class<T> getMatchClass() {
            return null;
        }
    }

    @FunctionalInterface
    protected interface InternalSetResolver<T, C> extends SetResolver<T> {

        void setValue(T array, int index, C value);

        @Override
        @SuppressWarnings("unchecked")
        default void set(T array, Object property, Object value) {
            if (property instanceof Number) {
                try {
                    setValue(array, ((Number) property).intValue(), (C) value);
                    return;
                } catch (ArrayIndexOutOfBoundsException e) {
                    throw new ScriptRuntimeException(StringUtil.format("Array index out of bounds, index={}", property), e);
                } catch (ClassCastException e) {
                    throw new ScriptRuntimeException(e.getMessage(), e);
                }
            }
            throw new ScriptRuntimeException(StringUtil.format("Invalid property or can't write: array#{}", property));
        }

        @Override
        default Class<T> getMatchClass() {
            return null;
        }
    }

    @Override
    public Class getMatchClass() {
        return null;
    }

    @Override
    public void register(ResolverManager resolverManager) {

        //InternalGetResolver
        resolverManager.registerResolver(boolean[].class,
                (InternalGetResolver<boolean[]>) (array, index) -> array[index]);
        resolverManager.registerResolver(char[].class,
                (InternalGetResolver<char[]>) (array, index) -> array[index]);
        resolverManager.registerResolver(int[].class,
                (InternalGetResolver<int[]>) (array, index) -> array[index]);
        resolverManager.registerResolver(float[].class,
                (InternalGetResolver<float[]>) (array, index) -> array[index]);
        resolverManager.registerResolver(double[].class,
                (InternalGetResolver<double[]>) (array, index) -> array[index]);
        resolverManager.registerResolver(long[].class,
                (InternalGetResolver<long[]>) (array, index) -> array[index]);
        resolverManager.registerResolver(short[].class,
                (InternalGetResolver<short[]>) (array, index) -> array[index]);
        resolverManager.registerResolver(byte[].class,
                (InternalGetResolver<byte[]>) (array, index) -> array[index]);

        // InternalSetResolver
        resolverManager.registerResolver(boolean[].class,
                (InternalSetResolver<boolean[], Object>) (array, index, value) -> array[index] = ALU.isTrue(value));
        resolverManager.registerResolver(char[].class,
                (InternalSetResolver<char[], Character>) (array, index, value) -> array[index] = value);
        resolverManager.registerResolver(int[].class,
                (InternalSetResolver<int[], Number>) (array, index, value) -> array[index] = value.intValue());
        resolverManager.registerResolver(float[].class,
                (InternalSetResolver<float[], Number>) (array, index, value) -> array[index] = value.floatValue());
        resolverManager.registerResolver(double[].class,
                (InternalSetResolver<double[], Number>) (array, index, value) -> array[index] = value.doubleValue());
        resolverManager.registerResolver(long[].class,
                (InternalSetResolver<long[], Number>) (array, index, value) -> array[index] = value.longValue());
        resolverManager.registerResolver(short[].class,
                (InternalSetResolver<short[], Number>) (array, index, value) -> array[index] = value.shortValue());
        resolverManager.registerResolver(byte[].class,
                (InternalSetResolver<byte[], Number>) (array, index, value) -> array[index] = value.byteValue());
    }
}
