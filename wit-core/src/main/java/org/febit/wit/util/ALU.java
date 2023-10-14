// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.util;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import lombok.experimental.UtilityClass;
import org.febit.lang.annotation.NullableApi;
import org.febit.wit.Context;
import org.febit.wit.exceptions.ScriptRuntimeException;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.DoubleAdder;
import java.util.concurrent.atomic.LongAccumulator;
import java.util.concurrent.atomic.LongAdder;

/**
 * @author zqq90
 */
@SuppressWarnings({
        "WeakerAccess"
})
@UtilityClass
@NullableApi
public class ALU {

    private static final int OBJECT = (1 << 29) - 1;
    private static final int STRING = (1 << 10) - 1;
    private static final int CHAR = (1 << 9) - 1;
    private static final int BIG_DECIMAL = (1 << 8) - 1;
    private static final int BIG_INTEGER = (1 << 7) - 1;
    private static final int DOUBLE = (1 << 6) - 1;
    private static final int FLOAT = (1 << 5) - 1;
    private static final int LONG = (1 << 4) - 1;
    private static final int INTEGER = (1 << 3) - 1;
    private static final int SHORT = (1 << 2) - 1;
    private static final int BYTE = (1 << 1) - 1;

    private static final Class<?>[] KNOWN_BASE_IMMUTABLES = {
            String.class,
            Integer.class,
            Long.class,
            Boolean.class,
            Short.class,
            Double.class,
            Float.class,
            Character.class,
            Byte.class,
            BigInteger.class,
            BigDecimal.class
    };

    public static boolean isKnownBaseImmutable(Object obj) {
        if (obj == null) {
            return true;
        }
        return isKnownBaseImmutableType(obj.getClass());
    }

    public static boolean isKnownBaseImmutableType(Class<?> cls) {
        for (Class<?> known : KNOWN_BASE_IMMUTABLES) {
            if (known.equals(cls)) {
                return true;
            }
        }
        return false;
    }

    @SuppressWarnings({
            "squid:S3776" // Cognitive Complexity of methods should not be too high
    })
    private static int getTypeMark(@Nonnull final Object o1) {
        final Class<?> cls = o1.getClass();
        if (cls == String.class) {
            return STRING;
        }
        if (cls == Integer.class) {
            return INTEGER;
        }
        if (cls == Long.class) {
            return LONG;
        }
        if (cls == Short.class) {
            return SHORT;
        }
        if (cls == Double.class) {
            return DOUBLE;
        }
        if (cls == Float.class) {
            return FLOAT;
        }
        if (cls == Character.class) {
            return CHAR;
        }
        if (cls == Byte.class) {
            return BYTE;
        }
        if (o1 instanceof Number) {
            if (o1 instanceof BigInteger) {
                return BIG_INTEGER;
            }
            if (o1 instanceof BigDecimal) {
                return BIG_DECIMAL;
            }
            if (o1 instanceof AtomicInteger) {
                return INTEGER;
            }
            if (o1 instanceof AtomicLong
                    || o1 instanceof LongAdder
                    || o1 instanceof LongAccumulator) {
                return LONG;
            }
            if (o1 instanceof DoubleAdder) {
                return DOUBLE;
            }
            // Note: otherwise, treat as BigDecimal
            return BIG_DECIMAL;
        }
        return OBJECT;
    }

    private static int getTypeMark(@Nonnull final Object o1, @Nonnull final Object o2) {
        return getTypeMark(o1) | getTypeMark(o2);
    }

    // +1
    public static Object plusOne(final Object o1) {
        requireNonNull(o1);
        switch (getTypeMark(o1)) {
            case INTEGER:
            case SHORT:
            case BYTE:
                return ((Number) o1).intValue() + 1;
            case CHAR:
                return ((Character) o1) + 1;
            case LONG:
                return ((Number) o1).longValue() + 1L;
            case DOUBLE:
                return ((Number) o1).doubleValue() + 1D;
            case FLOAT:
                return ((Number) o1).floatValue() + 1F;
            case BIG_INTEGER:
                return toBigInteger(o1).add(BigInteger.ONE);
            case BIG_DECIMAL:
                return toBigDecimal(o1).add(BigDecimal.ONE);
            default:
        }
        throw unsupportedTypeException(o1);
    }

    // -1
    public static Object minusOne(final Object o1) {
        requireNonNull(o1);
        switch (getTypeMark(o1)) {
            case INTEGER:
            case SHORT:
            case BYTE:
                return ((Number) o1).intValue() - 1;
            case CHAR:
                return ((Character) o1) - 1;
            case LONG:
                return ((Number) o1).longValue() - 1L;
            case DOUBLE:
                return ((Number) o1).doubleValue() - 1D;
            case FLOAT:
                return ((Number) o1).floatValue() - 1F;
            case BIG_INTEGER:
                return toBigInteger(o1).subtract(BigInteger.ONE);
            case BIG_DECIMAL:
                return toBigDecimal(o1).subtract(BigDecimal.ONE);
            default:
        }
        throw unsupportedTypeException(o1);
    }

    //+
    @Nullable
    public static Object plus(final Object o1, final Object o2) {
        if (o1 == null || o2 == null) {
            return o1 != null ? o1 : o2;
        }
        switch (getTypeMark(o1, o2)) {
            case STRING:
            case OBJECT:
                return o1.toString().concat(o2.toString());
            case INTEGER:
            case SHORT:
            case BYTE:
                return ((Number) o1).intValue() + ((Number) o2).intValue();
            case LONG:
                return ((Number) o1).longValue() + ((Number) o2).longValue();
            case DOUBLE:
                return ((Number) o1).doubleValue() + ((Number) o2).doubleValue();
            case FLOAT:
                return ((Number) o1).floatValue() + ((Number) o2).floatValue();
            case BIG_INTEGER:
                if (notDoubleOrFloat(o1, o2)) {
                    return toBigInteger(o1).add(toBigInteger(o2));
                }
                // Note: else upgrade to BigDecimal
                return toBigDecimal(o1).add(toBigDecimal(o2));
            case BIG_DECIMAL:
                return toBigDecimal(o1).add(toBigDecimal(o2));
            case CHAR:
                return plus(charToInt(o1), charToInt(o2));
            default:
        }
        throw unsupportedTypeException(o1, o2);
    }

    //-
    public static Object minus(final Object o1, final Object o2) {
        requireNonNull(o1, o2);
        switch (getTypeMark(o1, o2)) {
            case INTEGER:
            case SHORT:
            case BYTE:
                return ((Number) o1).intValue() - ((Number) o2).intValue();
            case LONG:
                return ((Number) o1).longValue() - ((Number) o2).longValue();
            case DOUBLE:
                return ((Number) o1).doubleValue() - ((Number) o2).doubleValue();
            case FLOAT:
                return ((Number) o1).floatValue() - ((Number) o2).floatValue();
            case BIG_INTEGER:
                if (notDoubleOrFloat(o1, o2)) {
                    return toBigInteger(o1).subtract(toBigInteger(o2));
                }
                // Note: else upgrade to BigDecimal
                return toBigDecimal(o1).subtract(toBigDecimal(o2));
            case BIG_DECIMAL:
                return toBigDecimal(o1).subtract(toBigDecimal(o2));
            case CHAR:
                return minus(charToInt(o1), charToInt(o2));
            default:
        }
        throw unsupportedTypeException(o1, o2);
    }

    // negative
    public static Object negative(final Object o1) {
        requireNonNull(o1);
        switch (getTypeMark(o1)) {
            case INTEGER:
                return -((Integer) o1);
            case LONG:
                return -((Long) o1);
            case DOUBLE:
                return -((Double) o1);
            case FLOAT:
                return -((Float) o1);
            case SHORT:
                return -((Short) o1);
            case BIG_INTEGER:
                return ((BigInteger) o1).negate();
            case BIG_DECIMAL:
                return ((BigDecimal) o1).negate();
            case CHAR:
                return -((Character) o1);
            default:
        }
        throw unsupportedTypeException(o1);
    }

    //*
    public static Object mult(final Object o1, final Object o2) {
        requireNonNull(o1, o2);
        switch (getTypeMark(o1, o2)) {
            case INTEGER:
            case SHORT:
            case BYTE:
                return ((Number) o1).intValue() * ((Number) o2).intValue();
            case LONG:
                return ((Number) o1).longValue() * ((Number) o2).longValue();
            case DOUBLE:
                return ((Number) o1).doubleValue() * ((Number) o2).doubleValue();
            case FLOAT:
                return ((Number) o1).floatValue() * ((Number) o2).floatValue();
            case BIG_INTEGER:
                if (notDoubleOrFloat(o1, o2)) {
                    return toBigInteger(o1).multiply(toBigInteger(o2));
                }
                // Note: else upgrade to BigDecimal
                return toBigDecimal(o1).multiply(toBigDecimal(o2));
            case BIG_DECIMAL:
                return toBigDecimal(o1).multiply(toBigDecimal(o2));
            case CHAR:
                return mult(charToInt(o1), charToInt(o2));
            default:
        }
        throw unsupportedTypeException(o1, o2);
    }

    // /
    public static Object div(final Object o1, final Object o2) {
        requireNonNull(o1, o2);
        switch (getTypeMark(o1, o2)) {
            case INTEGER:
            case SHORT:
            case BYTE:
                return ((Number) o1).intValue() / ((Number) o2).intValue();
            case LONG:
                return ((Number) o1).longValue() / ((Number) o2).longValue();
            case DOUBLE:
                return ((Number) o1).doubleValue() / ((Number) o2).doubleValue();
            case FLOAT:
                return ((Number) o1).floatValue() / ((Number) o2).floatValue();
            case BIG_INTEGER:
                if (notDoubleOrFloat(o1, o2)) {
                    return toBigInteger(o1).divide(toBigInteger(o2));
                }
                // Note: else upgrade to BigDecimal
                return toBigDecimal(o1).divide(toBigDecimal(o2));
            case BIG_DECIMAL:
                return toBigDecimal(o1).divide(toBigDecimal(o2));
            case CHAR:
                return div(charToInt(o1), charToInt(o2));
            default:
        }
        throw unsupportedTypeException(o1, o2);
    }

    // %
    public static Object mod(final Object o1, final Object o2) {
        requireNonNull(o1, o2);
        switch (getTypeMark(o1, o2)) {
            case INTEGER:
            case SHORT:
            case BYTE:
                return ((Number) o1).intValue() % ((Number) o2).intValue();
            case LONG:
                return ((Number) o1).longValue() % ((Number) o2).longValue();
            case DOUBLE:
                return ((Number) o1).doubleValue() % ((Number) o2).doubleValue();
            case FLOAT:
                return ((Number) o1).floatValue() % ((Number) o2).floatValue();
            case BIG_INTEGER:
                if (notDoubleOrFloat(o1, o2)) {
                    return toBigInteger(o1).remainder(toBigInteger(o2));
                }
                // Note: else upgrade to BigDecimal
                return toBigDecimal(o1).remainder(toBigDecimal(o2));
            case BIG_DECIMAL:
                return toBigDecimal(o1).remainder(toBigDecimal(o2));
            case CHAR:
                return mod(charToInt(o1), charToInt(o2));
            default:
        }
        throw unsupportedTypeException(o1, o2);
    }

    // &&
    @Nullable
    public static Object and(final Object o1, final Object o2) {
        return isTrue(o1) ? o2 : o1;
    }

    // ||
    @Nullable
    public static Object or(final Object o1, final Object o2) {
        return isTrue(o1) ? o1 : o2;
    }

    // !
    public static boolean not(final Object o1) {
        return !isTrue(o1);
    }

    // ==
    public static boolean isEqual(final Object o1, final Object o2) {
        if (o1 == o2) {
            return true;
        }
        if (o1 == null || o2 == null) {
            return false;
        }
        if (o1.equals(o2)) {
            return true;
        }
        switch (getTypeMark(o1, o2)) {
            case BYTE:
            case SHORT:
            case INTEGER:
                return ((Number) o1).intValue() == ((Number) o2).intValue();
            case LONG:
                return ((Number) o1).longValue() == ((Number) o2).longValue();
            case BIG_INTEGER:
                if (notDoubleOrFloat(o1, o2)) {
                    return toBigInteger(o1).compareTo(toBigInteger(o2)) == 0;
                }
                // Note: else upgrade to BigDecimal
                return toBigDecimal(o1).compareTo(toBigDecimal(o2)) == 0;
            case DOUBLE:
            case FLOAT:
                // Note: Floating point numbers should not be tested for equality.
            case BIG_DECIMAL:
                return toBigDecimal(o1).compareTo(toBigDecimal(o2)) == 0;
            case CHAR:
                return isEqual(charToInt(o1), charToInt(o2));
            default:
        }
        return false;
    }

    // !=
    public static boolean notEqual(final Object o1, final Object o2) {
        return !isEqual(o1, o2);
    }

    // >
    public static boolean greater(final Object o1, final Object o2) {
        requireNonNull(o1, o2);
        switch (getTypeMark(o1, o2)) {
            case BYTE:
            case SHORT:
            case INTEGER:
                return ((Number) o1).intValue() > ((Number) o2).intValue();
            case LONG:
                return ((Number) o1).longValue() > ((Number) o2).longValue();
            case BIG_INTEGER:
                if (notDoubleOrFloat(o1, o2)) {
                    return toBigInteger(o1).compareTo(toBigInteger(o2)) > 0;
                }
                // Note: else upgrade to BigDecimal
                return toBigDecimal(o1).compareTo(toBigDecimal(o2)) > 0;
            case DOUBLE:
            case FLOAT:
                // Note: Floating point numbers should not be tested for equality.
            case BIG_DECIMAL:
                return toBigDecimal(o1).compareTo(toBigDecimal(o2)) > 0;
            case CHAR:
                return greater(charToInt(o1), charToInt(o2));
            default:
        }
        throw unsupportedTypeException(o1, o2);
    }

    // >=
    public static boolean greaterEqual(final Object o1, final Object o2) {
        return !less(o1, o2);
    }

    // <
    public static boolean less(final Object o1, final Object o2) {
        requireNonNull(o1, o2);
        switch (getTypeMark(o1, o2)) {
            case CHAR:
                return less(charToInt(o1), charToInt(o2));
            case BYTE:
            case SHORT:
            case INTEGER:
                return ((Number) o1).intValue() < ((Number) o2).intValue();
            case LONG:
                return ((Number) o1).longValue() < ((Number) o2).longValue();
            case BIG_INTEGER:
                if (notDoubleOrFloat(o1, o2)) {
                    return toBigInteger(o1).compareTo(toBigInteger(o2)) < 0;
                }
                // Note: else upgrade to BigDecimal
                return toBigDecimal(o1).compareTo(toBigDecimal(o2)) < 0;
            case DOUBLE:
            case FLOAT:
                // Note: Floating point numbers should not be tested for equality.
            case BIG_DECIMAL:
                return toBigDecimal(o1).compareTo(toBigDecimal(o2)) < 0;
            default:
        }
        throw unsupportedTypeException(o1, o2);
    }

    // <=
    public static boolean lessEqual(final Object o1, final Object o2) {
        return !greater(o1, o2);
    }

    // &
    public static Object bitAnd(final Object o1, final Object o2) {
        requireNonNull(o1, o2);
        switch (getTypeMark(o1, o2)) {
            case CHAR:
                return bitAnd(charToInt(o1), charToInt(o2));
            case BYTE:
                return ((Number) o1).byteValue() & ((Number) o2).byteValue();
            case SHORT:
                return ((Number) o1).shortValue() & ((Number) o2).shortValue();
            case INTEGER:
                return ((Number) o1).intValue() & ((Number) o2).intValue();
            case LONG:
                return ((Number) o1).longValue() & ((Number) o2).longValue();
            case BIG_INTEGER:
                if (notDoubleOrFloat(o1, o2)) {
                    return toBigInteger(o1).and(toBigInteger(o2));
                }
                // Note: else unsupported
                throw unsupportedTypeException(o1, o2);
            default:
        }
        throw unsupportedTypeException(o1, o2);
    }

    // |
    public static Object bitOr(final Object o1, final Object o2) {
        requireNonNull(o1, o2);
        switch (getTypeMark(o1, o2)) {
            case CHAR:
                return bitOr(charToInt(o1), charToInt(o2));
            case BYTE:
                return ((Number) o1).byteValue() | ((Number) o2).byteValue();
            case SHORT:
                return ((Number) o1).shortValue() | ((Number) o2).shortValue();
            case INTEGER:
                return ((Number) o1).intValue() | ((Number) o2).intValue();
            case LONG:
                return ((Number) o1).longValue() | ((Number) o2).longValue();
            case BIG_INTEGER:
                if (notDoubleOrFloat(o1, o2)) {
                    return toBigInteger(o1).or(toBigInteger(o2));
                }
                // Note: else unsupported
                throw unsupportedTypeException(o1, o2);
            default:
        }
        throw unsupportedTypeException(o1, o2);
    }

    // ^ XOR
    @Nonnull
    public static Object bitXor(final Object o1, final Object o2) {
        requireNonNull(o1, o2);
        switch (getTypeMark(o1, o2)) {
            case CHAR:
                return bitXor(charToInt(o1), charToInt(o2));
            case BYTE:
                return ((Number) o1).byteValue() ^ ((Number) o2).byteValue();
            case SHORT:
                return ((Number) o1).shortValue() ^ ((Number) o2).shortValue();
            case INTEGER:
                return ((Number) o1).intValue() ^ ((Number) o2).intValue();
            case LONG:
                return ((Number) o1).longValue() ^ ((Number) o2).longValue();
            case BIG_INTEGER:
                if (notDoubleOrFloat(o1, o2)) {
                    return toBigInteger(o1).xor(toBigInteger(o2));
                }
                // Note: else unsupported
                throw unsupportedTypeException(o1, o2);
            default:
        }
        throw unsupportedTypeException(o1, o2);
    }

    // ~
    @Nonnull
    public static Object bitNot(final Object o1) {
        requireNonNull(o1);
        switch (getTypeMark(o1)) {
            case CHAR:
                return ~((Character) o1);
            case BYTE:
                return ~((Byte) o1);
            case SHORT:
                return ~((Short) o1);
            case INTEGER:
                return ~((Integer) o1);
            case LONG:
                return ~((Long) o1);
            case BIG_INTEGER:
                return ((BigInteger) o1).not();
            default:
        }
        throw unsupportedTypeException(o1);
    }

    // <<
    @Nonnull
    public static Object lshift(final Object o1, final Object o2) {
        requireNonNull(o1, o2);
        int right = requireNumber(o2).intValue();
        switch (getTypeMark(o1)) {
            case CHAR:
                return ((Character) o1) << right;
            case BYTE:
                return ((Byte) o1) << right;
            case SHORT:
                return ((Short) o1) << right;
            case INTEGER:
                return ((Integer) o1) << right;
            case LONG:
                return ((Long) o1) << right;
            case BIG_INTEGER:
                return ((BigInteger) o1).shiftLeft(right);
            default:
                throw unsupportedTypeException(o1, o2);
        }
    }

    // >>
    @Nonnull
    public static Object rshift(final Object o1, final Object o2) {
        requireNonNull(o1, o2);
        int right = requireNumber(o2).intValue();
        switch (getTypeMark(o1)) {
            case CHAR:
                return ((Character) o1) >> right;
            case BYTE:
                return ((Byte) o1) >> right;
            case SHORT:
                return ((Short) o1) >> right;
            case INTEGER:
                return ((Integer) o1) >> right;
            case LONG:
                return ((Long) o1) >> right;
            case BIG_INTEGER:
                return ((BigInteger) o1).shiftRight(right);
            default:
                throw unsupportedTypeException(o1, o2);
        }
    }

    // >>>
    @Nonnull
    public static Object urshift(final Object o1, final Object o2) {
        requireNonNull(o1, o2);
        int right = requireNumber(o2).intValue();
        switch (getTypeMark(o1)) {
            case CHAR:
                return ((Character) o1) >>> right;
            case BYTE:
                return ((Byte) o1) >>> right;
            case SHORT:
                return ((Short) o1) >>> right;
            case INTEGER:
                return ((Integer) o1) >>> right;
            case LONG:
                return ((Long) o1) >>> right;
            default:
                throw unsupportedTypeException(o1, o2);
        }
    }

    private static Object charToInt(final Object o1) {
        if (o1 instanceof Character) {
            return Integer.valueOf((Character) o1);
        }
        return o1;
    }

    private static boolean isSafeToLong(Class<?> type) {
        return type == Integer.class
                || type == Long.class
                || type == Short.class
                || type == Byte.class;
    }

    @Nonnull
    private static BigInteger toBigInteger(final Object o1) {
        if (o1 == null) {
            return BigInteger.ZERO;
        }
        if (o1 instanceof BigInteger) {
            return (BigInteger) o1;
        }
        if (isSafeToLong(o1.getClass())) {
            return BigInteger.valueOf(((Number) o1).longValue());
        }
        if (o1 instanceof BigDecimal) {
            return ((BigDecimal) o1).toBigInteger();
        }
        return new BigDecimal(o1.toString()).toBigInteger();
    }

    @Nonnull
    private static BigDecimal toBigDecimal(final Object o1) {
        if (o1 == null) {
            return BigDecimal.ZERO;
        }
        if (o1 instanceof BigDecimal) {
            return (BigDecimal) o1;
        }
        if (isSafeToLong(o1.getClass())) {
            return BigDecimal.valueOf(((Number) o1).longValue());
        }
        if (o1 instanceof BigInteger) {
            return new BigDecimal((BigInteger) o1);
        }
        // floating decimals
        return new BigDecimal(o1.toString());
    }

    public static boolean isTrue(final Object o) {
        if (o == null) {
            return false;
        }
        if (o.getClass() == Boolean.class) {
            return (Boolean) o;
        }
        if (o == Context.VOID) {
            return false;
        }
        //if Collection empty
        return CollectionUtil.notEmpty(o, true);
    }

    private static boolean notDoubleOrFloat(Object o1) {
        Class<?> type = o1 == null ? null : o1.getClass();
        return type != Float.class
                && type != Double.class;
    }

    private static boolean notDoubleOrFloat(Object o1, Object o2) {
        return notDoubleOrFloat(o1) && notDoubleOrFloat(o2);
    }

    @Nonnull
    private static ScriptRuntimeException unsupportedTypeException(final Object o1, final Object o2) {
        return new ScriptRuntimeException(StringUtil.format(
                "Unsupported type: left[{}], right[{}]",
                typeString(o1), typeString(o2)
        ));
    }

    @Nonnull
    private static String typeString(final Object o1) {
        return o1 == null ? "null" : o1.getClass().getCanonicalName();
    }

    @Nonnull
    private static ScriptRuntimeException unsupportedTypeException(final Object o1) {
        return new ScriptRuntimeException(StringUtil.format(
                "Unsupported type: [{}]",
                typeString(o1)
        ));
    }

    @Nonnull
    public static Number requireNumber(final Object val) {
        if (val instanceof Number) {
            return (Number) val;
        }
        if (val instanceof Character) {
            return Integer.valueOf((Character) val);
        }
        throw new ScriptRuntimeException("Number is required, not found: " + ClassUtil.getClassName(val));
    }

    private static void requireNonNull(final Object obj) {
        if (obj == null) {
            throw new ScriptRuntimeException("value is null");
        }
    }

    private static void requireNonNull(final Object o1, final Object o2) {
        if (o1 == null || o2 == null) {
            if (o1 != null) {
                throw new ScriptRuntimeException("right value is null");
            } else {
                throw new ScriptRuntimeException(o2 != null
                        ? "left value is null"
                        : "left & right values are null");
            }
        }
    }
}
