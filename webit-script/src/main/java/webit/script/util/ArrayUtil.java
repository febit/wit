// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script.util;

import java.util.Arrays;
import webit.script.exceptions.ScriptRuntimeException;

/**
 *
 * @author Zqq
 */
public class ArrayUtil {

    public static int getSize(final Object object) {
        if (object instanceof Object[]) {
            return ((Object[]) object).length;
        }
        final Class cls = object.getClass();
        if (cls.isArray()) {
            if (cls == int[].class) {
                return ((int[]) object).length;
            } else if (cls == long[].class) {
                return ((long[]) object).length;
            } else if (cls == float[].class) {
                return ((float[]) object).length;
            } else if (cls == double[].class) {
                return ((double[]) object).length;
            } else if (cls == short[].class) {
                return ((short[]) object).length;
            } else if (cls == byte[].class) {
                return ((byte[]) object).length;
            } else if (cls == char[].class) {
                return ((char[]) object).length;
            } else if (cls == boolean[].class) {
                return ((boolean[]) object).length;
            }
        }
        return -1;
    }

    @SuppressWarnings("unchecked")
    public static Object getByIndex(final Object o1, final int index) {
        try {
            if (o1 instanceof Object[]) {
                return ((Object[]) o1)[index];
            }
            final Class cls = o1.getClass();
            if (cls.isArray()) {
                if (cls == int[].class) {
                    return ((int[]) o1)[index];
                } else if (cls == boolean[].class) {
                    return ((boolean[]) o1)[index];
                } else if (cls == char[].class) {
                    return ((char[]) o1)[index];
                } else if (cls == float[].class) {
                    return ((float[]) o1)[index];
                } else if (cls == double[].class) {
                    return ((double[]) o1)[index];
                } else if (cls == long[].class) {
                    return ((long[]) o1)[index];
                } else if (cls == short[].class) {
                    return ((short[]) o1)[index];
                } else if (cls == byte[].class) {
                    return ((byte[]) o1)[index];
                }
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new ScriptRuntimeException(StringUtil.concat("Array index out of bounds, index=", index));
        }
        throw new ScriptRuntimeException("Not an array: ".concat(o1.getClass().getName()));
    }

    @SuppressWarnings("unchecked")
    public static void setByIndex(final Object o1, final int index, final Object value) {

        try {
            if (o1 instanceof Object[]) {
                ((Object[]) o1)[index] = value;
                return;
            }
            final Class cls = o1.getClass();
            if (cls.isArray()) {
                if (cls == int[].class) {
                    ((int[]) o1)[index] = ((Number) value).intValue();
                    return;
                } else if (cls == boolean[].class) {
                    ((boolean[]) o1)[index] = ALU.isTrue(value);
                    return;
                } else if (cls == char[].class) {
                    ((char[]) o1)[index] = (Character) value;
                    return;
                } else if (cls == float[].class) {
                    ((float[]) o1)[index] = ((Number) value).floatValue();
                    return;
                } else if (cls == double[].class) {
                    ((double[]) o1)[index] = ((Number) value).doubleValue();
                    return;
                } else if (cls == long[].class) {
                    ((long[]) o1)[index] = ((Number) value).longValue();
                    return;
                } else if (cls == short[].class) {
                    ((short[]) o1)[index] = ((Number) value).shortValue();
                    return;
                } else if (cls == byte[].class) {
                    ((byte[]) o1)[index] = ((Number) value).byteValue();
                    return;
                }
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new ScriptRuntimeException(StringUtil.concat("Array index out of bounds, index=", index));
        } catch (ClassCastException e) {
            throw new ScriptRuntimeException(e.getMessage());
        }
        throw new ScriptRuntimeException(StringUtil.concatObjectClass("Not an array: ", o1));
    }

    public static String arrayToString(final Object o1) {
        if (o1 instanceof Object[]) {
            return Arrays.toString((Object[]) o1);
        }
        final Class cls = o1.getClass();
        if (cls.isArray()) {
            if (cls == int[].class) {
                return Arrays.toString((int[]) o1);
            } else if (cls == boolean[].class) {
                return Arrays.toString((boolean[]) o1);
            } else if (cls == char[].class) {
                return Arrays.toString((char[]) o1);
            } else if (cls == float[].class) {
                return Arrays.toString((float[]) o1);
            } else if (cls == double[].class) {
                return Arrays.toString((double[]) o1);
            } else if (cls == long[].class) {
                return Arrays.toString((long[]) o1);
            } else if (cls == short[].class) {
                return Arrays.toString((short[]) o1);
            } else if (cls == byte[].class) {
                return Arrays.toString((byte[]) o1);
            }
        }
        throw new ScriptRuntimeException(StringUtil.concatObjectClass("Not an array: ", o1));
    }

    public static void invert(Object[] array) {
        int i, j;
        Object cell;
        for (i = 0, j = array.length - 1; i < j; i++, j--) {
            cell = array[i];
            array[i] = array[j];
            array[j] = cell;
        }
    }

    public static Object[] ensureMinSize(final Object[] array, final int len) {
        if (array == null) {
            return new Object[len];
        } else if (array.length >= len) {
            return array;
        } else {
            final Object[] newArray;
            System.arraycopy(array, 0, newArray = new Object[len], 0, array.length);
            return newArray;
        }
    }

    public static Object get(final Object[] array, final int index, final Object defaultValue) {
        if (array != null && index < array.length) {
            return array[index];
        } else {
            return defaultValue;
        }
    }

    public static String[] subarray(String[] src, int offset, int len) {
        String dest[] = new String[len];
        System.arraycopy(src, offset, dest, 0, len);
        return dest;
    }
}
