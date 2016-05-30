// Copyright (c) 2013-2015, Webit Team. All Rights Reserved.
package webit.script.util;

import java.math.BigDecimal;
import java.math.BigInteger;
import webit.script.Context;
import webit.script.exceptions.ScriptRuntimeException;

/**
 *
 * @author zqq90
 */
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

    private ALU() {
    }

    private static int getTypeMark(final Object o1) {
        final Class cls = o1.getClass();
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
            //Note: otherwise, treat as BigDecimal
            return BIG_DECIMAL;
        }
        return OBJECT;
    }

    // +1
    public static Object plusOne(final Object o1) {
        if (o1 != null) {
            if (o1 instanceof Number) {
                final Number num = (Number) o1;
                switch (getTypeMark(num)) {
                    case INTEGER:
                    case SHORT:
                    case BYTE:
                        return num.intValue() + 1;
                    case LONG:
                        return num.longValue() + 1L;
                    case DOUBLE:
                        return num.doubleValue() + 1D;
                    case FLOAT:
                        return num.floatValue() + 1F;
                    case BIG_INTEGER:
                        return ((BigInteger) num).add(BigInteger.ONE);
                    case BIG_DECIMAL:
                        return ((BigDecimal) num).add(BigDecimal.ONE);
                }
            } else if (o1 instanceof Character) {
                return ((Character) o1) + 1;
            }
            throw unsupportedTypeException(o1);
        }
        throw valueIsNullException();
    }

    // -1
    public static Object minusOne(final Object o1) {
        if (o1 != null) {
            if (o1 instanceof Number) {
                final Number num = (Number) o1;
                switch (getTypeMark(num)) {
                    case INTEGER:
                    case SHORT:
                    case BYTE:
                        return num.intValue() - 1;
                    case LONG:
                        return num.longValue() - 1L;
                    case DOUBLE:
                        return num.doubleValue() - 1D;
                    case FLOAT:
                        return num.floatValue() - 1F;
                    case BIG_INTEGER:
                        return ((BigInteger) num).subtract(BigInteger.ONE);
                    case BIG_DECIMAL:
                        return ((BigDecimal) num).subtract(BigDecimal.ONE);
                }
            } else if (o1 instanceof Character) {
                return ((Character) o1) - 1;
            }
            throw unsupportedTypeException(o1);
        }
        throw valueIsNullException();
    }

    //+
    public static Object plus(final Object o1, final Object o2) {
        if (o1 != null && o2 != null) {
            switch (getTypeMark(o1) | getTypeMark(o2)) {
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
                //Note: else upgrade to BigDecimal
                case BIG_DECIMAL:
                    return toBigDecimal(o1).add(toBigDecimal(o2));
                case CHAR:
                    return plus(charToInt(o1), charToInt(o2));
                default:
            }
            throw unsupportedTypeException(o1, o2);
        } else {
            return o1 != null ? o1 : o2;
        }
    }

    //-
    public static Object minus(final Object o1, final Object o2) {
        if (o1 != null && o2 != null) {
            switch (getTypeMark(o1) | getTypeMark(o2)) {
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
                //Note: else upgrade to BigDecimal
                case BIG_DECIMAL:
                    return toBigDecimal(o1).subtract(toBigDecimal(o2));
                case CHAR:
                    return minus(charToInt(o1), charToInt(o2));
                default:
            }
            throw unsupportedTypeException(o1, o2);
        }
        throw valueIsNullException(o1, o2);
    }

    // negative
    public static Object negative(final Object o1) {
        if (o1 != null) {
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
        throw valueIsNullException();
    }

    //*
    public static Object mult(final Object o1, final Object o2) {
        if (o1 != null && o2 != null) {
            switch (getTypeMark(o1) | getTypeMark(o2)) {
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
                //Note: else upgrade to BigDecimal
                case BIG_DECIMAL:
                    return toBigDecimal(o1).multiply(toBigDecimal(o2));
                case CHAR:
                    return mult(charToInt(o1), charToInt(o2));
                default:
            }
            throw unsupportedTypeException(o1, o2);
        }
        throw valueIsNullException(o1, o2);
    }

    // /
    public static Object div(final Object o1, final Object o2) {
        if (o1 != null && o2 != null) {
            switch (getTypeMark(o1) | getTypeMark(o2)) {
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
                //Note: else upgrade to BigDecimal
                case BIG_DECIMAL:
                    return toBigDecimal(o1).divide(toBigDecimal(o2));
                case CHAR:
                    return div(charToInt(o1), charToInt(o2));
                default:
            }
            throw unsupportedTypeException(o1, o2);
        }
        throw valueIsNullException(o1, o2);
    }

    // %
    public static Object mod(final Object o1, final Object o2) {
        if (o1 != null && o2 != null) {
            switch (getTypeMark(o1) | getTypeMark(o2)) {
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
                //Note: else upgrade to BigDecimal
                case BIG_DECIMAL:
                    return toBigDecimal(o1).remainder(toBigDecimal(o2));
                case CHAR:
                    return mod(charToInt(o1), charToInt(o2));
                default:
            }
            throw unsupportedTypeException(o1, o2);
        }
        throw valueIsNullException(o1, o2);
    }

    // &&
    public static Object and(final Object o1, final Object o2) {
        return isTrue(o1) ? o2 : o1;
    }

    // ||
    public static Object or(final Object o1, final Object o2) {
        return isTrue(o1) ? o1 : o2;
    }

    // !
    public static boolean not(final Object o1) {
        return !isTrue(o1);
    }

    // ==
    public static boolean equal(final Object o1, final Object o2) {
        if (o1 == o2) {
            return true;
        }
        if (o1 == null || o2 == null) {
            return false;
        }
        if (o1.equals(o2)) {
            return true;
        }
        switch (getTypeMark(o1) | getTypeMark(o2)) {
            case BYTE:
            case SHORT:
            case INTEGER:
                return ((Number) o1).intValue() == ((Number) o2).intValue();
            case LONG:
                return ((Number) o1).longValue() == ((Number) o2).longValue();
            case DOUBLE:
                return ((Number) o1).doubleValue() == ((Number) o2).doubleValue();
            case FLOAT:
                return ((Number) o1).floatValue() == ((Number) o2).floatValue();
            case BIG_INTEGER:
                if (notDoubleOrFloat(o1, o2)) {
                    return toBigInteger(o1).compareTo(toBigInteger(o2)) == 0;
                }
            //Note: else upgrade to BigDecimal
            case BIG_DECIMAL:
                return toBigDecimal(o1).compareTo(toBigDecimal(o2)) == 0;
            case CHAR:
                return equal(charToInt(o1), charToInt(o2));
            default:
        }
        return false;
    }

    // !=
    public static boolean notEqual(final Object o1, final Object o2) {
        return !equal(o1, o2);
    }

    // >
    public static boolean greater(final Object o1, final Object o2) {
        if (o1 != null && o2 != null) {
            switch (getTypeMark(o1) | getTypeMark(o2)) {
                case BYTE:
                case SHORT:
                case INTEGER:
                    return ((Number) o1).intValue() > ((Number) o2).intValue();
                case LONG:
                    return ((Number) o1).longValue() > ((Number) o2).longValue();
                case DOUBLE:
                    return ((Number) o1).doubleValue() > ((Number) o2).doubleValue();
                case FLOAT:
                    return ((Number) o1).floatValue() > ((Number) o2).floatValue();
                case BIG_INTEGER:
                    if (notDoubleOrFloat(o1, o2)) {
                        return toBigInteger(o1).compareTo(toBigInteger(o2)) > 0;
                    }
                //Note: else upgrade to BigDecimal
                case BIG_DECIMAL:
                    return toBigDecimal(o1).compareTo(toBigDecimal(o2)) > 0;
                case CHAR:
                    return greater(charToInt(o1), charToInt(o2));
                default:
            }
            throw unsupportedTypeException(o1, o2);
        }
        throw valueIsNullException(o1, o2);
    }

    // >=
    public static boolean greaterEqual(final Object o1, final Object o2) {
        if (o1 != null && o2 != null) {
            switch (getTypeMark(o1) | getTypeMark(o2)) {
                case CHAR:
                    return greaterEqual(charToInt(o1), charToInt(o2));
                case BYTE:
                case SHORT:
                case INTEGER:
                    return ((Number) o1).intValue() >= ((Number) o2).intValue();
                case LONG:
                    return ((Number) o1).longValue() >= ((Number) o2).longValue();
                case DOUBLE:
                    return ((Number) o1).doubleValue() >= ((Number) o2).doubleValue();
                case FLOAT:
                    return ((Number) o1).floatValue() >= ((Number) o2).floatValue();
                case BIG_INTEGER:
                    if (notDoubleOrFloat(o1, o2)) {
                        return toBigInteger(o1).compareTo(toBigInteger(o2)) >= 0;
                    }
                //Note: else upgrade to BigDecimal
                case BIG_DECIMAL:
                    return toBigDecimal(o1).compareTo(toBigDecimal(o2)) >= 0;
                default:
            }
            throw unsupportedTypeException(o1, o2);
        }
        throw valueIsNullException(o1, o2);
    }

    // <
    public static boolean less(final Object o1, final Object o2) {
        if (o1 != null && o2 != null) {
            switch (getTypeMark(o1) | getTypeMark(o2)) {
                case CHAR:
                    return less(charToInt(o1), charToInt(o2));
                case BYTE:
                case SHORT:
                case INTEGER:
                    return ((Number) o1).intValue() < ((Number) o2).intValue();
                case LONG:
                    return ((Number) o1).longValue() < ((Number) o2).longValue();
                case DOUBLE:
                    return ((Number) o1).doubleValue() < ((Number) o2).doubleValue();
                case FLOAT:
                    return ((Number) o1).floatValue() < ((Number) o2).floatValue();
                case BIG_INTEGER:
                    if (notDoubleOrFloat(o1, o2)) {
                        return toBigInteger(o1).compareTo(toBigInteger(o2)) < 0;
                    }
                //Note: else upgrade to BigDecimal
                case BIG_DECIMAL:
                    return toBigDecimal(o1).compareTo(toBigDecimal(o2)) < 0;
                default:
            }
            throw unsupportedTypeException(o1, o2);
        }
        throw valueIsNullException(o1, o2);
    }

    // <=
    public static boolean lessEqual(final Object o1, final Object o2) {
        if (o1 != null && o2 != null) {
            switch (getTypeMark(o1) | getTypeMark(o2)) {
                case CHAR:
                    return lessEqual(charToInt(o1), charToInt(o2));
                case BYTE:
                case SHORT:
                case INTEGER:
                    return ((Number) o1).intValue() <= ((Number) o2).intValue();
                case LONG:
                    return ((Number) o1).longValue() <= ((Number) o2).longValue();
                case DOUBLE:
                    return ((Number) o1).doubleValue() <= ((Number) o2).doubleValue();
                case FLOAT:
                    return ((Number) o1).floatValue() <= ((Number) o2).floatValue();
                case BIG_INTEGER:
                    if (notDoubleOrFloat(o1, o2)) {
                        return toBigInteger(o1).compareTo(toBigInteger(o2)) <= 0;
                    }
                //Note: else upgrade to BigDecimal
                case BIG_DECIMAL:
                    return toBigDecimal(o1).compareTo(toBigDecimal(o2)) <= 0;
                default:
            }
            throw unsupportedTypeException(o1, o2);
        }
        throw valueIsNullException(o1, o2);
    }

    // &
    public static Object bitAnd(final Object o1, final Object o2) {
        if (o1 != null && o2 != null) {
            switch (getTypeMark(o1) | getTypeMark(o2)) {
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
                //Note: else unsupported
                default:
            }
            throw unsupportedTypeException(o1, o2);
        }
        throw valueIsNullException(o1, o2);
    }

    // |
    public static Object bitOr(final Object o1, final Object o2) {
        if (o1 != null && o2 != null) {
            switch (getTypeMark(o1) | getTypeMark(o2)) {
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
                //Note: else unsupported
                default:
            }
            throw unsupportedTypeException(o1, o2);
        }
        throw valueIsNullException(o1, o2);
    }

    // ^ XOR
    public static Object bitXor(final Object o1, final Object o2) {
        if (o1 != null && o2 != null) {
            switch (getTypeMark(o1) | getTypeMark(o2)) {
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
                //Note: else unsupported
                default:
            }
            throw unsupportedTypeException(o1, o2);
        }
        throw valueIsNullException(o1, o2);
    }

    // ~ 
    public static Object bitNot(final Object o1) {
        if (o1 != null) {
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
        throw valueIsNullException();
    }

    // <<
    public static Object lshift(final Object o1, final Object o2) {
        if (o1 != null && o2 != null) {
            if (o2 instanceof Number) {
                int right = ((Number) o2).intValue();
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
                }
            }
            throw unsupportedTypeException(o1, o2);
        }
        throw valueIsNullException(o1, o2);
    }

    // >>
    public static Object rshift(final Object o1, final Object o2) {
        if (o1 != null && o2 != null) {
            if (o2 instanceof Number) {
                int right = ((Number) o2).intValue();
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
                }
            }
            throw unsupportedTypeException(o1, o2);
        }
        throw valueIsNullException(o1, o2);
    }

    // >>>
    public static Object urshift(final Object o1, final Object o2) {
        if (o1 != null && o2 != null) {
            if (o2 instanceof Number) {
                int right = ((Number) o2).intValue();
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
                }
            }
            throw unsupportedTypeException(o1, o2);
        }
        throw valueIsNullException(o1, o2);
    }

    private static Object charToInt(final Object o1) {
        if (o1 instanceof Character) {
            return (int) ((Character) o1);
        }
        return o1;
    }

    private static BigInteger toBigInteger(final Object o1) {
        if (o1 instanceof BigInteger) {
            return (BigInteger) o1;
        }
        Class type = o1.getClass();
        if (type == Integer.class
                || type == Long.class
                || type == Short.class
                || type == Byte.class) {
            return BigInteger.valueOf(((Number) o1).longValue());
        }
        if (o1 instanceof BigDecimal) {
            return ((BigDecimal) o1).toBigInteger();
        }
        if (type == Float.class
                || type == Double.class) {
            return BigDecimal.valueOf(((Number) o1).doubleValue()).toBigInteger();
        }
        return new BigDecimal(o1.toString()).toBigInteger();
    }

    private static BigDecimal toBigDecimal(final Object o1) {
        if (o1 instanceof BigDecimal) {
            return (BigDecimal) o1;
        }
        Class type = o1.getClass();
        if (type == Integer.class
                || type == Long.class
                || type == Short.class
                || type == Byte.class) {
            return BigDecimal.valueOf(((Number) o1).longValue());
        }
        if (type == Float.class
                || type == Double.class) {
            return BigDecimal.valueOf(((Number) o1).doubleValue());
        }
        if (o1 instanceof BigInteger) {
            return new BigDecimal((BigInteger) o1);
        }
        return toBigDecimal(o1);
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
        Class type = o1.getClass();
        return type != Float.class && type != Double.class;
    }

    private static boolean notDoubleOrFloat(Object o1, Object o2) {
        return notDoubleOrFloat(o1) && notDoubleOrFloat(o2);
    }

    private static ScriptRuntimeException unsupportedTypeException(final Object o1, final Object o2) {
        return new ScriptRuntimeException(StringUtil.format("Unsupported type: left[{}], right[{}]", o1.getClass(), o2.getClass()));
    }

    private static ScriptRuntimeException unsupportedTypeException(final Object o1) {
        return new ScriptRuntimeException(StringUtil.format("Unsupported type: [{}]", o1.getClass()));
    }

    private static ScriptRuntimeException valueIsNullException() {
        return new ScriptRuntimeException("value is null");
    }

    private static ScriptRuntimeException valueIsNullException(final Object o1, final Object o2) {
        return new ScriptRuntimeException(
                o1 == null
                ? (o2 == null ? "left & right values are null" : "left value is null")
                : (o2 == null ? "right value is null" : "left & right values are not null"));
    }
}
