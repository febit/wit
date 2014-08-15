// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script.util;

import java.math.BigDecimal;
import java.math.BigInteger;
import webit.script.Context;
import webit.script.exceptions.ScriptRuntimeException;

/**
 *
 * @author Zqq
 */
public class ALU {

    //marks
    private static final int NULL = (1 << 30) - 1;
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

    //STRING BIG_DECIMAL BIG_INTEGER DOUBLE FLOAT LONG CHAR INTEGER SHORT BYTE
    private ALU() {
    }

    private static int getTypeMark(final Object o1) {
        if (o1 != null) {
            final Class cls = o1.getClass();
            if (cls == String.class) {
                return STRING;
            } else if (cls == Integer.class) {
                return INTEGER;
            } else if (cls == Long.class) {
                return LONG;
            } else if (cls == Short.class) {
                return SHORT;
            } else if (cls == Double.class) {
                return DOUBLE;
            } else if (cls == Float.class) {
                return FLOAT;
            } else if (cls == Character.class) {
                return CHAR;
            } else if (cls == Byte.class) {
                return BYTE;
            } else if (o1 instanceof Number) {
                if (cls == BigDecimal.class) {
                    return BIG_DECIMAL;
                } else if (cls == BigInteger.class) {
                    return BIG_INTEGER;
                } else if (o1 instanceof BigDecimal) {
                    return BIG_DECIMAL;
                } else if (o1 instanceof BigInteger) {
                    return BIG_INTEGER;
                }
                //XXX: 缺省处理 @tbd
                return DOUBLE;
            }
            return OBJECT;
        }
        return NULL;
    }

    //+1
    public static Object plusOne(final Object o1) {
        if (o1 != null) {
            if (o1 instanceof Number) {
                final Number num = (Number) o1;
                switch (getTypeMark(num)) {
                    //TODO: Unsupported Big*
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
                }
            } else if (o1 instanceof Character) {
                return ((Character) o1) + 1;
            } else {
                throw new ScriptRuntimeException("value not a number");
            }
        }
        throw valueIsNullException(o1);
    }

    // -1
    public static Object minusOne(final Object o1) {
        if (o1 != null) {
            if (o1 instanceof Number) {
                final Number num = (Number) o1;
                switch (getTypeMark(num)) {
                    //TODO: Unsupported Big*
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
                }
            } else if (o1 instanceof Character) {
                return ((Character) o1) - 1;
            } else {
                throw new ScriptRuntimeException("value not a number");
            }
        }
        throw valueIsNullException(o1);
    }

    //+
    public static Object plus(final Object o1, final Object o2) {
        if (o1 != null && o2 != null) {
            switch (getTypeMark(o1) | getTypeMark(o2)) {
                case STRING:
                case OBJECT:
                    return String.valueOf(o1).concat(String.valueOf(o2));
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
                case BIG_DECIMAL:
                    return new BigDecimal(o1.toString()).add(new BigDecimal(o2.toString()));
                case BIG_INTEGER:
                    //TODO: Unsupported
                    throw unsupportedTypeException(o1, o2);
                case CHAR:
                    //TODO: Unsupported
                    throw unsupportedTypeException(o1, o2);
                default:
                    throw unsupportedTypeException(o1, o2);
            }
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
                case BIG_DECIMAL:
                    return new BigDecimal(o1.toString()).subtract(new BigDecimal(o2.toString()));
                case BIG_INTEGER:
                    //TODO: Unsupported
                    throw unsupportedTypeException(o1, o2);
                case CHAR:
                    throw unsupportedTypeException(o1, o2);
                default:
                    throw unsupportedTypeException(o1, o2);
            }
        } else {
            throw valueIsNullException(o1, o2);
        }
    }

    // negative
    public static Object negative(final Object o1) {
        if (o1 != null) {
            switch (getTypeMark(o1)) {
                //TODO: Unsupported Big*
                case INTEGER:
                    return -((Number) o1).intValue();
                case LONG:
                    return -((Number) o1).longValue();
                case DOUBLE:
                    return -((Number) o1).doubleValue();
                case FLOAT:
                    return -((Number) o1).floatValue();
                case SHORT:
                    return -((Number) o1).shortValue();
                default:
                    throw new ScriptRuntimeException("value not a number"); //TODO: unsuitable message
            }
        } else {
            throw valueIsNullException(o1);
        }
    }

    //*
    public static Object mult(final Object o1, final Object o2) {
        if (o1 != null && o2 != null) {
            switch (getTypeMark(o1) | getTypeMark(o2)) {
                //TODO: Unsupported Big*
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
                case BIG_DECIMAL:
                    return new BigDecimal(o1.toString()).multiply(new BigDecimal(o2.toString()));
                case BIG_INTEGER:
                    //TODO: Unsupported
                    throw unsupportedTypeException(o1, o2);
                default:
                    throw unsupportedTypeException(o1, o2);
            }
        } else {
            throw valueIsNullException(o1, o2);
        }
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
                case BIG_DECIMAL:
                    return new BigDecimal(o1.toString()).divide(new BigDecimal(o2.toString()));
                case BIG_INTEGER:
                    //TODO: Unsupported
                    throw unsupportedTypeException(o1, o2);
                default:
                    throw unsupportedTypeException(o1, o2);
            }
        } else {
            throw valueIsNullException(o1, o2);
        }
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
                case BIG_DECIMAL:
                    return new BigDecimal(o1.toString()).remainder(new BigDecimal(o2.toString()));
                case BIG_INTEGER:
                    //TODO: Unsupported
                    throw unsupportedTypeException(o1, o2);
                default:
                    throw unsupportedTypeException(o1, o2);
            }
        } else {
            throw valueIsNullException(o1, o2);
        }
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
        if (o1 instanceof Number && o2 instanceof Number) {
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
                default:
                    return new BigDecimal(o1.toString()).compareTo(new BigDecimal(o2.toString())) == 0;
            }
        }
        //TODO Character
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
                case CHAR:
                    return (o1 instanceof Number ? ((Number) o1).intValue() : (int) (Character) o1)
                            > (o2 instanceof Number ? ((Number) o2).intValue() : (int) (Character) o2);
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
                case BIG_DECIMAL:
                case BIG_INTEGER:
                    return new BigDecimal(o1.toString()).compareTo(new BigDecimal(o2.toString())) > 0;
                default:
                    throw unsupportedTypeException(o1, o2);
            }
        } else {
            throw valueIsNullException(o1, o2);
        }
    }

    // >=
    public static boolean greaterEqual(final Object o1, final Object o2) {
        if (o1 != null && o2 != null) {
            switch (getTypeMark(o1) | getTypeMark(o2)) {
                case CHAR:
                    return (o1 instanceof Number ? ((Number) o1).intValue() : (int) (Character) o1)
                            >= (o2 instanceof Number ? ((Number) o2).intValue() : (int) (Character) o2);
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
                case BIG_DECIMAL:
                case BIG_INTEGER:
                    return new BigDecimal(o1.toString()).compareTo(new BigDecimal(o2.toString())) >= 0;
                default:
                    throw unsupportedTypeException(o1, o2);
            }
        } else {
            throw valueIsNullException(o1, o2);
        }
    }

    // <
    public static boolean less(final Object o1, final Object o2) {
        if (o1 != null && o2 != null) {
            switch (getTypeMark(o1) | getTypeMark(o2)) {
                case CHAR:
                    return (o1 instanceof Number ? ((Number) o1).intValue() : (int) (Character) o1)
                            < (o2 instanceof Number ? ((Number) o2).intValue() : (int) (Character) o2);
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
                case BIG_DECIMAL:
                case BIG_INTEGER:
                    return new BigDecimal(o1.toString()).compareTo(new BigDecimal(o2.toString())) < 0;
                default:
                    throw unsupportedTypeException(o1, o2);
            }
        } else {
            throw valueIsNullException(o1, o2);
        }
    }

    // <=
    public static boolean lessEqual(final Object o1, final Object o2) {
        if (o1 != null && o2 != null) {
            switch (getTypeMark(o1) | getTypeMark(o2)) {
                case CHAR:
                    return (o1 instanceof Number ? ((Number) o1).intValue() : (int) (Character) o1)
                            <= (o2 instanceof Number ? ((Number) o2).intValue() : (int) (Character) o2);
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
                case BIG_DECIMAL:
                case BIG_INTEGER:
                    return new BigDecimal(o1.toString()).compareTo(new BigDecimal(o2.toString())) <= 0;
                default:
                    throw unsupportedTypeException(o1, o2);
            }
        } else {
            throw valueIsNullException(o1, o2);
        }
    }

    // &
    public static Object bitAnd(final Object o1, final Object o2) {
        if (o1 != null && o2 != null) {
            switch (getTypeMark(o1) | getTypeMark(o2)) {
                case CHAR:
                    return (o1 instanceof Number ? ((Number) o1).intValue() : (int) (Character) o1)
                            & (o2 instanceof Number ? ((Number) o2).intValue() : (int) (Character) o2);
                case BYTE:
                    return ((Number) o1).byteValue() & ((Number) o2).byteValue();
                case SHORT:
                    return ((Number) o1).shortValue() & ((Number) o2).shortValue();
                case INTEGER:
                    return ((Number) o1).intValue() & ((Number) o2).intValue();
                case LONG:
                    return ((Number) o1).longValue() & ((Number) o2).longValue();
                default:
                    throw unsupportedTypeException(o1, o2);
            }
        } else {
            throw valueIsNullException(o1, o2);
        }
    }

    // |
    public static Object bitOr(final Object o1, final Object o2) {
        if (o1 != null && o2 != null) {
            switch (getTypeMark(o1) | getTypeMark(o2)) {
                case CHAR:
                    return (o1 instanceof Number ? ((Number) o1).intValue() : (int) (Character) o1)
                            | (o2 instanceof Number ? ((Number) o2).intValue() : (int) (Character) o2);
                case BYTE:
                    return ((Number) o1).byteValue() | ((Number) o2).byteValue();
                case SHORT:
                    return ((Number) o1).shortValue() | ((Number) o2).shortValue();
                case INTEGER:
                    return ((Number) o1).intValue() | ((Number) o2).intValue();
                case LONG:
                    return ((Number) o1).longValue() | ((Number) o2).longValue();
                default:
                    throw unsupportedTypeException(o1, o2);
            }
        } else {
            throw valueIsNullException(o1, o2);
        }
    }

    // ^ XOR
    public static Object bitXor(final Object o1, final Object o2) {
        if (o1 != null && o2 != null) {
            switch (getTypeMark(o1) | getTypeMark(o2)) {
                case CHAR:
                    return (o1 instanceof Number ? ((Number) o1).intValue() : (int) (Character) o1)
                            ^ (o2 instanceof Number ? ((Number) o2).intValue() : (int) (Character) o2);
                case BYTE:
                    return ((Number) o1).byteValue() ^ ((Number) o2).byteValue();
                case SHORT:
                    return ((Number) o1).shortValue() ^ ((Number) o2).shortValue();
                case INTEGER:
                    return ((Number) o1).intValue() ^ ((Number) o2).intValue();
                case LONG:
                    return ((Number) o1).longValue() ^ ((Number) o2).longValue();
                default:
                    throw unsupportedTypeException(o1, o2);
            }
        } else {
            throw valueIsNullException(o1, o2);
        }
    }

    // ~ 
    public static Object bitNot(final Object o1) {
        if (o1 != null) {
            switch (getTypeMark(o1)) {
                case BYTE:
                    return ~((Number) o1).byteValue();
                case SHORT:
                    return ~((Number) o1).shortValue();
                case INTEGER:
                    return ~((Number) o1).intValue();
                case LONG:
                    return ~((Number) o1).longValue();
                default:
                    throw new ScriptRuntimeException(StringUtil.concatObjectClass("unsupported type:", o1));
            }
        } else {
            throw valueIsNullException(o1);
        }
    }

    // <<
    public static Object lshift(final Object o1, final Object o2) {
        if (o1 != null && o2 != null) {
            if (o2 instanceof Number) {
                switch (getTypeMark(o1)) {
                    case CHAR:
                        return ((Character) o1) << ((Number) o2).intValue();
                    case BYTE:
                        return ((Byte) o1) << ((Number) o2).intValue();
                    case SHORT:
                        return ((Short) o1) << ((Number) o2).intValue();
                    case INTEGER:
                        return ((Integer) o1) << ((Number) o2).intValue();
                    case LONG:
                        return ((Long) o1) << ((Number) o2).intValue();
                    default:
                        throw new ScriptRuntimeException(StringUtil.concatObjectClass("left value type is unsupported:", o1));
                }
            }
            throw new ScriptRuntimeException("right value not a number");
        } else {
            throw valueIsNullException(o1, o2);
        }
    }

    // >>
    public static Object rshift(final Object o1, final Object o2) {
        if (o1 != null && o2 != null) {
            if (o2 instanceof Number) {
                switch (getTypeMark(o1)) {
                    case CHAR:
                        return ((Character) o1) >> ((Number) o2).intValue();
                    case BYTE:
                        return ((Byte) o1) >> ((Number) o2).intValue();
                    case SHORT:
                        return ((Short) o1) >> ((Number) o2).intValue();
                    case INTEGER:
                        return ((Integer) o1) >> ((Number) o2).intValue();
                    case LONG:
                        return ((Long) o1) >> ((Number) o2).intValue();
                    default:
                        throw new ScriptRuntimeException(StringUtil.concatObjectClass("left value type is unsupported:", o1));
                }
            }
            throw new ScriptRuntimeException("right value not a number");
        } else {
            throw valueIsNullException(o1, o2);
        }
    }

    // >>>
    public static Object urshift(final Object o1, final Object o2) {
        if (o1 != null && o2 != null) {
            if (o2 instanceof Number) {
                switch (getTypeMark(o1)) {
                    case CHAR:
                        return ((Character) o1) >>> ((Number) o2).intValue();
                    case BYTE:
                        return ((Byte) o1) >>> ((Number) o2).intValue();
                    case SHORT:
                        return ((Short) o1) >>> ((Number) o2).intValue();
                    case INTEGER:
                        return ((Integer) o1) >>> ((Number) o2).intValue();
                    case LONG:
                        return ((Long) o1) >>> ((Number) o2).intValue();
                    default:
                        throw new ScriptRuntimeException(StringUtil.concatObjectClass("left value type is unsupported:", o1));
                }
            }
            throw new ScriptRuntimeException("right value not a number");
        } else {
            throw valueIsNullException(o1, o2);
        }
    }

    private static ScriptRuntimeException unsupportedTypeException(final Object o1, final Object o2) {
        return new ScriptRuntimeException(StringUtil.concat("Unsupported type: left[", o1.getClass().getName(), "], right[", o2.getClass().getName(), "]"));
    }

    private static ScriptRuntimeException valueIsNullException(final Object o1) {
        return new ScriptRuntimeException("value is null");
    }

    private static ScriptRuntimeException valueIsNullException(final Object o1, final Object o2) {
        if (o1 == null) {
            if (o2 == null) {
                return new ScriptRuntimeException("left & right values are null");
            } else {
                return new ScriptRuntimeException("left value is null");
            }
        } else if (o2 == null) {
            return new ScriptRuntimeException("right value is null");
        } else {
            return new ScriptRuntimeException("left & right values are not null");
        }
    }

    public static boolean isTrue(final Object o) {
        if (o == null || o == Context.VOID) {
            return false;
        } else if (o instanceof Boolean) {
            return (Boolean) o;
        } else {
            //if Collection empty 
            return CollectionUtil.notEmpty(o, true);
        }
    }
}
