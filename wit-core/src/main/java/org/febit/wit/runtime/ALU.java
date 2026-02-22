// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.runtime;

import lombok.experimental.UtilityClass;
import org.febit.wit.exception.ScriptEvaluateException;
import org.febit.wit.util.CollectionUtils;
import org.jspecify.annotations.Nullable;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.DoubleAdder;
import java.util.concurrent.atomic.LongAccumulator;
import java.util.concurrent.atomic.LongAdder;

@UtilityClass
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

    public static boolean isKnownBaseImmutable(@Nullable Object obj) {
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
    private static int getTypeMark(Object o1) {
        var cls = o1.getClass();
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

    private static int getTypeMark(Object o1, final Object o2) {
        return getTypeMark(o1) | getTypeMark(o2);
    }

    // +1
    public static Object plusOne(@Nullable Object o1) {
        requireNonNull(o1);
        return switch (getTypeMark(o1)) {
            case INTEGER, SHORT, BYTE -> ((Number) o1).intValue() + 1;
            case CHAR -> ((Character) o1) + 1;
            case LONG -> ((Number) o1).longValue() + 1L;
            case DOUBLE -> ((Number) o1).doubleValue() + 1D;
            case FLOAT -> ((Number) o1).floatValue() + 1F;
            case BIG_INTEGER -> toBigInteger(o1).add(BigInteger.ONE);
            case BIG_DECIMAL -> toBigDecimal(o1).add(BigDecimal.ONE);
            default -> throw unsupportedTypeException(o1);
        };
    }

    // -1
    public static Object minusOne(@Nullable Object o1) {
        requireNonNull(o1);
        return switch (getTypeMark(o1)) {
            case INTEGER, SHORT, BYTE -> ((Number) o1).intValue() - 1;
            case CHAR -> ((Character) o1) - 1;
            case LONG -> ((Number) o1).longValue() - 1L;
            case DOUBLE -> ((Number) o1).doubleValue() - 1D;
            case FLOAT -> ((Number) o1).floatValue() - 1F;
            case BIG_INTEGER -> toBigInteger(o1).subtract(BigInteger.ONE);
            case BIG_DECIMAL -> toBigDecimal(o1).subtract(BigDecimal.ONE);
            default -> throw unsupportedTypeException(o1);
        };
    }

    //+
    @Nullable
    public static Object plus(@Nullable Object o1, @Nullable Object o2) {
        if (o1 == null || o2 == null) {
            return o1 != null ? o1 : o2;
        }
        return switch (getTypeMark(o1, o2)) {
            case STRING, OBJECT -> o1.toString().concat(o2.toString());
            case INTEGER, SHORT, BYTE -> ((Number) o1).intValue() + ((Number) o2).intValue();
            case LONG -> ((Number) o1).longValue() + ((Number) o2).longValue();
            case FLOAT -> toFloat(o1) + toFloat(o2);
            case DOUBLE -> toDouble(o1) + toDouble(o2);
            case BIG_INTEGER -> {
                if (isNotDoubleOrFloat(o1, o2)) {
                    yield toBigInteger(o1).add(toBigInteger(o2));
                }
                // Note: else upgrade to BigDecimal
                yield toBigDecimal(o1).add(toBigDecimal(o2));
            }
            case BIG_DECIMAL -> toBigDecimal(o1).add(toBigDecimal(o2));
            case CHAR -> plus(charToInt(o1), charToInt(o2));
            default -> throw unsupportedTypeException(o1, o2);
        };
    }

    //-
    public static Object minus(@Nullable Object o1, @Nullable Object o2) {
        requireNonNull(o1, o2);
        return switch (getTypeMark(o1, o2)) {
            case INTEGER, SHORT, BYTE -> ((Number) o1).intValue() - ((Number) o2).intValue();
            case LONG -> ((Number) o1).longValue() - ((Number) o2).longValue();
            case FLOAT -> toFloat(o1) - toFloat(o2);
            case DOUBLE -> toDouble(o1) - toDouble(o2);
            case BIG_INTEGER -> {
                if (isNotDoubleOrFloat(o1, o2)) {
                    yield toBigInteger(o1).subtract(toBigInteger(o2));
                }
                // Note: else upgrade to BigDecimal
                yield toBigDecimal(o1).subtract(toBigDecimal(o2));
            }
            case BIG_DECIMAL -> toBigDecimal(o1).subtract(toBigDecimal(o2));
            case CHAR -> minus(charToInt(o1), charToInt(o2));
            default -> throw unsupportedTypeException(o1, o2);
        };
    }

    // negative
    public static Object negative(@Nullable Object o1) {
        requireNonNull(o1);
        return switch (getTypeMark(o1)) {
            case INTEGER -> -((Integer) o1);
            case LONG -> -((Long) o1);
            case DOUBLE -> -((Double) o1);
            case FLOAT -> -((Float) o1);
            case SHORT -> -((Short) o1);
            case BIG_INTEGER -> ((BigInteger) o1).negate();
            case BIG_DECIMAL -> ((BigDecimal) o1).negate();
            case CHAR -> -((Character) o1);
            default -> throw unsupportedTypeException(o1);
        };
    }

    //*
    public static Object multi(@Nullable Object o1, @Nullable Object o2) {
        requireNonNull(o1, o2);
        return switch (getTypeMark(o1, o2)) {
            case INTEGER, SHORT, BYTE -> ((Number) o1).intValue() * ((Number) o2).intValue();
            case LONG -> ((Number) o1).longValue() * ((Number) o2).longValue();
            case FLOAT -> toFloat(o1) * toFloat(o2);
            case DOUBLE -> toDouble(o1) * toDouble(o2);
            case BIG_INTEGER -> {
                if (isNotDoubleOrFloat(o1, o2)) {
                    yield toBigInteger(o1).multiply(toBigInteger(o2));
                }
                // Note: else upgrade to BigDecimal
                yield toBigDecimal(o1).multiply(toBigDecimal(o2));
            }
            case BIG_DECIMAL -> toBigDecimal(o1).multiply(toBigDecimal(o2));
            case CHAR -> multi(charToInt(o1), charToInt(o2));
            default -> throw unsupportedTypeException(o1, o2);
        };
    }

    // /
    public static Object div(@Nullable Object o1, @Nullable Object o2) {
        requireNonNull(o1, o2);
        return switch (getTypeMark(o1, o2)) {
            case INTEGER, SHORT, BYTE -> ((Number) o1).intValue() / ((Number) o2).intValue();
            case LONG -> ((Number) o1).longValue() / ((Number) o2).longValue();
            case FLOAT -> toFloat(o1) / toFloat(o2);
            case DOUBLE -> toDouble(o1) / toDouble(o2);
            case BIG_INTEGER -> {
                if (isNotDoubleOrFloat(o1, o2)) {
                    yield toBigInteger(o1).divide(toBigInteger(o2));
                }
                // Note: else upgrade to BigDecimal
                yield toBigDecimal(o1).divide(toBigDecimal(o2), RoundingMode.HALF_UP);
            }
            case BIG_DECIMAL -> toBigDecimal(o1).divide(toBigDecimal(o2), RoundingMode.HALF_UP);
            case CHAR -> div(charToInt(o1), charToInt(o2));
            default -> throw unsupportedTypeException(o1, o2);
        };
    }

    // %
    public static Object mod(@Nullable Object o1, @Nullable Object o2) {
        requireNonNull(o1, o2);
        return switch (getTypeMark(o1, o2)) {
            case INTEGER, SHORT, BYTE -> ((Number) o1).intValue() % ((Number) o2).intValue();
            case LONG -> ((Number) o1).longValue() % ((Number) o2).longValue();
            case FLOAT -> toFloat(o1) % toFloat(o2);
            case DOUBLE -> toDouble(o1) % toDouble(o2);
            case BIG_INTEGER -> {
                if (isNotDoubleOrFloat(o1, o2)) {
                    yield toBigInteger(o1).remainder(toBigInteger(o2));
                }
                // Note: else upgrade to BigDecimal
                yield toBigDecimal(o1).remainder(toBigDecimal(o2));
                // Note: else upgrade to BigDecimal
            }
            case BIG_DECIMAL -> toBigDecimal(o1).remainder(toBigDecimal(o2));
            case CHAR -> mod(charToInt(o1), charToInt(o2));
            default -> throw unsupportedTypeException(o1, o2);
        };
    }

    // &&
    @Nullable
    public static Object and(@Nullable Object o1, @Nullable Object o2) {
        return isTruly(o1) ? o2 : o1;
    }

    // ||
    @Nullable
    public static Object or(@Nullable Object o1, @Nullable Object o2) {
        return isTruly(o1) ? o1 : o2;
    }

    // !
    public static boolean not(@Nullable Object o1) {
        return !isTruly(o1);
    }

    public static boolean isTruly(@Nullable Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj.getClass() == Boolean.class) {
            return (Boolean) obj;
        }
        if (obj == Undefined.UNDEFINED) {
            return false;
        }

        var size = CollectionUtils.size(obj);
        if (size == 0) {
            return false;
        }
        if (size > 0) {
            return true;
        }
        if (obj instanceof Iterable<?> iter) {
            return iter.iterator().hasNext();
        }
        if (obj instanceof Iterator<?> iter) {
            return iter.hasNext();
        }
        if (obj instanceof Enumeration<?> e) {
            return e.hasMoreElements();
        }
        return true;
    }
    // ==
    public static boolean isEqual(@Nullable Object o1, @Nullable Object o2) {
        if (o1 == o2) {
            return true;
        }
        if (o1 == null || o2 == null) {
            return false;
        }
        if (o1.equals(o2)) {
            return true;
        }
        return switch (getTypeMark(o1, o2)) {
            case BYTE, SHORT, INTEGER -> ((Number) o1).intValue() == ((Number) o2).intValue();
            case LONG -> ((Number) o1).longValue() == ((Number) o2).longValue();
            case BIG_INTEGER -> {
                if (isNotDoubleOrFloat(o1, o2)) {
                    yield toBigInteger(o1).compareTo(toBigInteger(o2)) == 0;
                }
                // Note: else upgrade to BigDecimal
                yield toBigDecimal(o1).compareTo(toBigDecimal(o2)) == 0;
            }
            case FLOAT -> Float.floatToIntBits(toFloat(o1))
                    == Float.floatToIntBits(toFloat(o2));
            case DOUBLE -> Double.doubleToLongBits(toDouble(o1))
                    == Double.doubleToLongBits(toDouble(o2));
            // Note: Floating point numbers should not be tested for equality.
            case BIG_DECIMAL -> toBigDecimal(o1).compareTo(toBigDecimal(o2)) == 0;
            case CHAR -> isEqual(charToInt(o1), charToInt(o2));
            default -> false;
        };
    }

    // !=
    public static boolean isNotEqual(@Nullable Object o1, @Nullable Object o2) {
        return !isEqual(o1, o2);
    }

    // >
    public static boolean greater(@Nullable Object o1, @Nullable Object o2) {
        requireNonNull(o1, o2);
        return switch (getTypeMark(o1, o2)) {
            case BYTE, SHORT, INTEGER -> ((Number) o1).intValue() > ((Number) o2).intValue();
            case LONG -> ((Number) o1).longValue() > ((Number) o2).longValue();
            case BIG_INTEGER -> {
                if (isNotDoubleOrFloat(o1, o2)) {
                    yield toBigInteger(o1).compareTo(toBigInteger(o2)) > 0;
                }
                // Note: else upgrade to BigDecimal
                yield toBigDecimal(o1).compareTo(toBigDecimal(o2)) > 0;
                // Note: else upgrade to BigDecimal
            }
            // Note: Floating point numbers should not be tested for equality.
            case DOUBLE, FLOAT, BIG_DECIMAL -> toBigDecimal(o1).compareTo(toBigDecimal(o2)) > 0;
            case CHAR -> greater(charToInt(o1), charToInt(o2));
            default -> throw unsupportedTypeException(o1, o2);
        };
    }

    // >=
    public static boolean greaterEqual(@Nullable Object o1, @Nullable Object o2) {
        return !less(o1, o2);
    }

    // <
    public static boolean less(@Nullable Object o1, @Nullable Object o2) {
        requireNonNull(o1, o2);
        return switch (getTypeMark(o1, o2)) {
            case CHAR -> less(charToInt(o1), charToInt(o2));
            case BYTE, SHORT, INTEGER -> ((Number) o1).intValue() < ((Number) o2).intValue();
            case LONG -> ((Number) o1).longValue() < ((Number) o2).longValue();
            case BIG_INTEGER -> {
                if (isNotDoubleOrFloat(o1, o2)) {
                    yield toBigInteger(o1).compareTo(toBigInteger(o2)) < 0;
                }
                // Note: else upgrade to BigDecimal
                yield toBigDecimal(o1).compareTo(toBigDecimal(o2)) < 0;
            }
            // Note: Floating point numbers should not be tested for equality.
            case DOUBLE, FLOAT, BIG_DECIMAL -> toBigDecimal(o1).compareTo(toBigDecimal(o2)) < 0;
            default -> throw unsupportedTypeException(o1, o2);
        };
    }

    // <=
    public static boolean lessEqual(@Nullable Object o1, @Nullable Object o2) {
        return !greater(o1, o2);
    }

    // &
    public static Object bitAnd(@Nullable Object o1, @Nullable Object o2) {
        requireNonNull(o1, o2);
        return switch (getTypeMark(o1, o2)) {
            case CHAR -> bitAnd(charToInt(o1), charToInt(o2));
            case BYTE -> ((Number) o1).byteValue() & ((Number) o2).byteValue();
            case SHORT -> ((Number) o1).shortValue() & ((Number) o2).shortValue();
            case INTEGER -> ((Number) o1).intValue() & ((Number) o2).intValue();
            case LONG -> ((Number) o1).longValue() & ((Number) o2).longValue();
            case BIG_INTEGER -> {
                if (isNotDoubleOrFloat(o1, o2)) {
                    yield toBigInteger(o1).and(toBigInteger(o2));
                }
                // Note: else unsupported
                throw unsupportedTypeException(o1, o2);
            }
            default -> throw unsupportedTypeException(o1, o2);
        };
    }

    // |
    public static Object bitOr(@Nullable Object o1, @Nullable Object o2) {
        requireNonNull(o1, o2);
        return switch (getTypeMark(o1, o2)) {
            case CHAR -> bitOr(charToInt(o1), charToInt(o2));
            case BYTE -> ((Number) o1).byteValue() | ((Number) o2).byteValue();
            case SHORT -> ((Number) o1).shortValue() | ((Number) o2).shortValue();
            case INTEGER -> ((Number) o1).intValue() | ((Number) o2).intValue();
            case LONG -> ((Number) o1).longValue() | ((Number) o2).longValue();
            case BIG_INTEGER -> {
                if (isNotDoubleOrFloat(o1, o2)) {
                    yield toBigInteger(o1).or(toBigInteger(o2));
                }
                // Note: else unsupported
                throw unsupportedTypeException(o1, o2);
            }
            default -> throw unsupportedTypeException(o1, o2);
        };
    }

    // ^ XOR
    public static Object bitXor(@Nullable Object o1, @Nullable Object o2) {
        requireNonNull(o1, o2);
        return switch (getTypeMark(o1, o2)) {
            case CHAR -> bitXor(charToInt(o1), charToInt(o2));
            case BYTE -> ((Number) o1).byteValue() ^ ((Number) o2).byteValue();
            case SHORT -> ((Number) o1).shortValue() ^ ((Number) o2).shortValue();
            case INTEGER -> ((Number) o1).intValue() ^ ((Number) o2).intValue();
            case LONG -> ((Number) o1).longValue() ^ ((Number) o2).longValue();
            case BIG_INTEGER -> {
                if (isNotDoubleOrFloat(o1, o2)) {
                    yield toBigInteger(o1).xor(toBigInteger(o2));
                }
                // Note: else unsupported
                throw unsupportedTypeException(o1, o2);
            }
            default -> throw unsupportedTypeException(o1, o2);
        };
    }

    // ~
    public static Object bitNot(@Nullable Object o1) {
        requireNonNull(o1);
        return switch (getTypeMark(o1)) {
            case CHAR -> ~((Character) o1);
            case BYTE -> ~((Byte) o1);
            case SHORT -> ~((Short) o1);
            case INTEGER -> ~((Integer) o1);
            case LONG -> ~((Long) o1);
            case BIG_INTEGER -> ((BigInteger) o1).not();
            default -> throw unsupportedTypeException(o1);
        };
    }

    // <<
    public static Object lshift(@Nullable Object o1, @Nullable Object o2) {
        requireNonNull(o1, o2);
        int right = requireNumber(o2).intValue();
        return switch (getTypeMark(o1)) {
            case CHAR -> ((Character) o1) << right;
            case BYTE -> ((Byte) o1) << right;
            case SHORT -> ((Short) o1) << right;
            case INTEGER -> ((Integer) o1) << right;
            case LONG -> ((Long) o1) << right;
            case BIG_INTEGER -> ((BigInteger) o1).shiftLeft(right);
            default -> throw unsupportedTypeException(o1, o2);
        };
    }

    // >>
    public static Object rshift(@Nullable Object o1, @Nullable Object o2) {
        requireNonNull(o1, o2);
        int right = requireNumber(o2).intValue();
        return switch (getTypeMark(o1)) {
            case CHAR -> ((Character) o1) >> right;
            case BYTE -> ((Byte) o1) >> right;
            case SHORT -> ((Short) o1) >> right;
            case INTEGER -> ((Integer) o1) >> right;
            case LONG -> ((Long) o1) >> right;
            case BIG_INTEGER -> ((BigInteger) o1).shiftRight(right);
            default -> throw unsupportedTypeException(o1, o2);
        };
    }

    // >>>
    public static Object urshift(@Nullable Object o1, @Nullable Object o2) {
        requireNonNull(o1, o2);
        int right = requireNumber(o2).intValue();
        return switch (getTypeMark(o1)) {
            case CHAR -> ((Character) o1) >>> right;
            case BYTE -> ((Byte) o1) >>> right;
            case SHORT -> ((Short) o1) >>> right;
            case INTEGER -> ((Integer) o1) >>> right;
            case LONG -> ((Long) o1) >>> right;
            default -> throw unsupportedTypeException(o1, o2);
        };
    }


    private static Object charToInt(final Object o1) {
        if (o1 instanceof Character c) {
            return Integer.valueOf(c);
        }
        return o1;
    }

    private static boolean isSafeToLong(Class<?> type) {
        return type == Integer.class
                || type == Long.class
                || type == Short.class
                || type == Byte.class;
    }

    private static float toFloat(@Nullable Object o1) {
        if (o1 == null) {
            return 0F;
        }
        if (o1 instanceof Float f) {
            return f;
        }
        if (o1 instanceof Double d) {
            return new BigDecimal(d.toString()).floatValue();
        }
        if (o1 instanceof Number number) {
            return number.floatValue();
        }
        return toBigDecimal(o1).floatValue();
    }

    private static double toDouble(@Nullable Object o1) {
        if (o1 == null) {
            return 0D;
        }
        if (o1 instanceof Double d) {
            return d;
        }
        if (o1 instanceof Float f) {
            return new BigDecimal(f.toString()).doubleValue();
        }
        if (o1 instanceof Number number) {
            return number.doubleValue();
        }
        return toBigDecimal(o1).doubleValue();
    }

    private static BigInteger toBigInteger(@Nullable Object o1) {
        if (o1 == null) {
            return BigInteger.ZERO;
        }
        if (o1 instanceof BigInteger bi) {
            return bi;
        }
        if (isSafeToLong(o1.getClass())) {
            return BigInteger.valueOf(((Number) o1).longValue());
        }
        if (o1 instanceof BigDecimal decimal) {
            return decimal.toBigInteger();
        }
        return new BigDecimal(o1.toString()).toBigInteger();
    }

    private static BigDecimal toBigDecimal(@Nullable Object o1) {
        if (o1 == null) {
            return BigDecimal.ZERO;
        }
        if (o1 instanceof BigDecimal decimal) {
            return decimal;
        }
        if (isSafeToLong(o1.getClass())) {
            return BigDecimal.valueOf(((Number) o1).longValue());
        }
        if (o1 instanceof BigInteger bi) {
            return new BigDecimal(bi);
        }
        // floating decimals
        return new BigDecimal(o1.toString());
    }

    private static boolean isNotDoubleOrFloat(@Nullable Object o1) {
        Class<?> type = o1 == null ? null : o1.getClass();
        return type != Float.class
                && type != Double.class;
    }

    private static boolean isNotDoubleOrFloat(@Nullable Object o1, @Nullable Object o2) {
        return isNotDoubleOrFloat(o1) && isNotDoubleOrFloat(o2);
    }

    private static String toTypeString(@Nullable Object o1) {
        return o1 == null ? "null" : o1.getClass().getCanonicalName();
    }

    private static ScriptEvaluateException unsupportedTypeException(@Nullable Object o1, @Nullable Object o2) {
        return new ScriptEvaluateException("Unsupported type: left ["
                + toTypeString(o1) + "], right[" + toTypeString(o2) + "]"
        );
    }

    private static ScriptEvaluateException unsupportedTypeException(@Nullable Object o1) {
        return new ScriptEvaluateException("Unsupported type: " + toTypeString(o1));
    }

    public static Number requireNumber(@Nullable Object val) {
        if (val instanceof Number number) {
            return number;
        }
        if (val instanceof Character c) {
            return Integer.valueOf(c);
        }
        throw new ScriptEvaluateException("value is not a number: " + toTypeString(val));
    }

    private static void requireNonNull(@Nullable Object obj) {
        if (obj == null) {
            throw new ScriptEvaluateException("value is null");
        }
    }

    private static void requireNonNull(@Nullable Object o1, @Nullable Object o2) {
        if (o1 == null || o2 == null) {
            if (o1 != null) {
                throw new ScriptEvaluateException("right value is null");
            } else {
                throw new ScriptEvaluateException(o2 != null
                        ? "left value is null"
                        : "left & right values are null");
            }
        }
    }
}
