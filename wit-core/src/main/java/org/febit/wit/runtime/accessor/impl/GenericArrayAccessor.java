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

import lombok.RequiredArgsConstructor;
import org.febit.wit.exception.ScriptEvaluateException;
import org.febit.wit.ir.support.ALU;
import org.febit.wit.runtime.accessor.Getter;
import org.febit.wit.runtime.accessor.Setter;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

@RequiredArgsConstructor
public class GenericArrayAccessor<T, I> implements Getter<T>, Setter<T> {

    private final ArrayGetter<T, I> getter;
    private final ArraySetter<T, I> setter;

    @Nullable
    @Override
    public Object get(T array, @Nullable Object property) {
        if (property == null) {
            throw new ScriptEvaluateException("property/index should not be null for array access.");
        }
        if (property instanceof Number idx) {
            return getter.get(array, idx.intValue());
        }
        return switch (property.toString()) {
            case "size", "length" -> ALU.size(array);
            case "isEmpty" -> ALU.size(array) == 0;
            default -> throw new ScriptEvaluateException("Unsupported property for array access: " + property);
        };
    }

    @Override
    @SuppressWarnings("unchecked")
    public void set(T array, @Nullable Object property, @Nullable Object value) {
        if (!(property instanceof Number idx)) {
            throw new ScriptEvaluateException("property/index should be a number for array access.");
        }
        try {
            setter.set(array, idx.intValue(), (I) value);
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new ScriptEvaluateException("index out of bounds: " + idx, e);
        } catch (ClassCastException e) {
            throw new ScriptEvaluateException(e.getMessage(), e);
        }
    }

    public interface ArrayGetter<T, I> {
        I get(T array, int idx);
    }

    public interface ArraySetter<T, I> {
        void set(T array, int idx, @Nullable I value);
    }

    public static GenericArrayAccessor<boolean[], Object> forBoolean() {
        return new GenericArrayAccessor<>(GenericArrayAccessor::get, GenericArrayAccessor::set);
    }

    public static GenericArrayAccessor<byte[], Number> forByte() {
        return new GenericArrayAccessor<>(GenericArrayAccessor::get, GenericArrayAccessor::set);
    }

    public static GenericArrayAccessor<short[], Number> forShort() {
        return new GenericArrayAccessor<>(GenericArrayAccessor::get, GenericArrayAccessor::set);
    }

    public static GenericArrayAccessor<char[], Object> forChar() {
        return new GenericArrayAccessor<>(GenericArrayAccessor::get, GenericArrayAccessor::set);
    }

    public static GenericArrayAccessor<int[], Number> forInt() {
        return new GenericArrayAccessor<>(GenericArrayAccessor::get, GenericArrayAccessor::set);
    }

    public static GenericArrayAccessor<float[], Number> forFloat() {
        return new GenericArrayAccessor<>(GenericArrayAccessor::get, GenericArrayAccessor::set);
    }

    public static GenericArrayAccessor<double[], Number> forDouble() {
        return new GenericArrayAccessor<>(GenericArrayAccessor::get, GenericArrayAccessor::set);
    }

    public static GenericArrayAccessor<long[], Number> forLong() {
        return new GenericArrayAccessor<>(GenericArrayAccessor::get, GenericArrayAccessor::set);
    }

    private static Boolean get(boolean[] array, int index) {
        return array[index];
    }

    private static Byte get(byte[] array, int index) {
        return array[index];
    }

    private static Short get(short[] array, int index) {
        return array[index];
    }

    private static Character get(char[] array, int index) {
        return array[index];
    }

    private static Integer get(int[] array, int index) {
        return array[index];
    }

    private static Float get(float[] array, int index) {
        return array[index];
    }

    private static Double get(double[] array, int index) {
        return array[index];
    }

    private static Long get(long[] array, int index) {
        return array[index];
    }

    private static void set(boolean[] array, int index, @Nullable Object value) {
        array[index] = ALU.isTruly(value);
    }

    private static void set(byte[] array, int index, @Nullable Number value) {
        array[index] = Objects.requireNonNull(value).byteValue();
    }

    private static void set(short[] array, int index, @Nullable Number value) {
        array[index] = Objects.requireNonNull(value).shortValue();
    }

    private static void set(char[] array, int index, @Nullable Object value) {
        Objects.requireNonNull(value);
        if (value instanceof Character c) {
            array[index] = c;
            return;
        }
        if (value instanceof Number n) {
            array[index] = (char) n.intValue();
            return;
        }
        if (value instanceof CharSequence s) {
            if (s.length() != 1) {
                throw new ScriptEvaluateException("CharSequence value for char should have length of 1"
                        + ", but got: " + s.length());
            }
            array[index] = s.charAt(0);
            return;
        }
        throw new ScriptEvaluateException("Unsupported value for char array: " + value);
    }

    private static void set(int[] array, int index, @Nullable Number value) {
        array[index] = Objects.requireNonNull(value).intValue();
    }

    private static void set(float[] array, int index, @Nullable Number value) {
        array[index] = Objects.requireNonNull(value).floatValue();
    }

    private static void set(double[] array, int index, @Nullable Number value) {
        array[index] = Objects.requireNonNull(value).doubleValue();
    }

    private static void set(long[] array, int index, @Nullable Number value) {
        array[index] = Objects.requireNonNull(value).longValue();
    }

}
