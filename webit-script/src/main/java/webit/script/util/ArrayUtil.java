// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.util;

import java.util.Arrays;
import webit.script.exceptions.ScriptRuntimeException;

/**
 *
 * @author Zqq
 */
public class ArrayUtil {

    //XXX:Will asm do this better?
    public static int getSize(final Object object) {
        final Class cls;
        if (object instanceof Object[]) {
            return ((Object[]) object).length;
        } else if ((cls = object.getClass()) == int[].class) {
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
        return -1;
    }

    @SuppressWarnings("unchecked")
    public static Object getByIndex(final Object o1, final int index) {
        final Class clazz;
        try {
            if (o1 instanceof Object[]) {
                return ((Object[]) o1)[index];
            }//
            else if ((clazz = o1.getClass()) == int[].class) {
                return ((int[]) o1)[index];
            }//
            else if (clazz == boolean[].class) {
                return ((boolean[]) o1)[index];
            }//
            else if (clazz == char[].class) {
                return ((char[]) o1)[index];
            }//
            else if (clazz == float[].class) {
                return ((float[]) o1)[index];
            }//
            else if (clazz == double[].class) {
                return ((double[]) o1)[index];
            }//
            else if (clazz == long[].class) {
                return ((long[]) o1)[index];
            }//
            else if (clazz == short[].class) {
                return ((short[]) o1)[index];
            }//
            else if (clazz == byte[].class) {
                return ((byte[]) o1)[index];
            }//
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new ScriptRuntimeException(StringUtil.concat("Array index out of bounds, index=", index));
        }
        throw new ScriptRuntimeException("Not an array: ".concat(o1.getClass().getName()));
    }

    @SuppressWarnings("unchecked")
    public static void setByIndex(final Object o1, final int index, final Object value) {
        final Class clazz;

        try {
            if (o1 instanceof Object[]) {
                ((Object[]) o1)[index] = value;
                return;
            }//
            else if ((clazz = o1.getClass()) == int[].class) {
                ((int[]) o1)[index] = ((Number) value).intValue();
                return;
            }//
            else if (clazz == boolean[].class) {
                ((boolean[]) o1)[index] = ALU.isTrue(value);
                return;
            }//
            else if (clazz == char[].class) {
                ((char[]) o1)[index] = (Character) value;
                return;
            }//
            else if (clazz == float[].class) {
                ((float[]) o1)[index] = ((Number) value).floatValue();
                return;
            }//
            else if (clazz == double[].class) {
                ((double[]) o1)[index] = ((Number) value).doubleValue();
                return;
            }//
            else if (clazz == long[].class) {
                ((long[]) o1)[index] = ((Number) value).longValue();
                return;
            }//
            else if (clazz == short[].class) {
                ((short[]) o1)[index] = ((Number) value).shortValue();
                return;
            }//
            else if (clazz == byte[].class) {
                ((byte[]) o1)[index] = ((Number) value).byteValue();
                return;
            }//
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new ScriptRuntimeException(StringUtil.concat("Array index out of bounds, index=", index));
        } catch (ClassCastException e) {
            throw new ScriptRuntimeException(e.getMessage());
        }
        throw new ScriptRuntimeException(StringUtil.concatObjectClass("Not an array: ", o1));
    }

    public static String arrayToString(final Object o1) {
        final Class clazz;
        if (o1 instanceof Object[]) {
            return Arrays.toString((Object[]) o1);
        }//
        else if ((clazz = o1.getClass()) == int[].class) {
            return Arrays.toString((int[]) o1);
        }//
        else if (clazz == boolean[].class) {
            return Arrays.toString((boolean[]) o1);
        }//
        else if (clazz == char[].class) {
            return Arrays.toString((char[]) o1);
        }//
        else if (clazz == float[].class) {
            return Arrays.toString((float[]) o1);
        }//
        else if (clazz == double[].class) {
            return Arrays.toString((double[]) o1);
        }//
        else if (clazz == long[].class) {
            return Arrays.toString((long[]) o1);
        }//
        else if (clazz == short[].class) {
            return Arrays.toString((short[]) o1);
        }//
        else if (clazz == byte[].class) {
            return Arrays.toString((byte[]) o1);
        }//
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
}
