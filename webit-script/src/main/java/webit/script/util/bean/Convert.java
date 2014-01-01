// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script.util.bean;

import webit.script.util.ClassUtil;
import webit.script.util.StringUtil;

/**
 *
 * @author Zqq
 */
public class Convert {

    public static Object convert(String string, Class cls) {
        if (string == null) {
            return null;
        }//
        else if (cls.isArray()) {
            if (cls == Class[].class) {
                return toClassArray(string);
            } else if (cls == String[].class) {
                return toStringArray(string);
            } else if (cls == int[].class) {
                return toIntArray(string);
            } else if (cls == Integer[].class) {
                return toIntegerArray(string);
            } else if (cls == boolean[].class) {
                return toBoolArray(string);
            } else if (cls == Integer[].class) {
                return toBooleanArray(string);
            }
        }//
        else if (cls == String.class) {
            return string;
        } else if (cls == Class.class) {
            return toClass(string);
        } else if (cls == int.class || cls == Integer.class) {
            return toInt(string);
        } else if (cls == boolean.class || cls == Boolean.class) {
            return toBoolean(string);
        }

        return string;
    }

    public static Class toClass(String string) {
        try {
            return ClassUtil.getClass(string);
        } catch (ClassNotFoundException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static boolean toBoolean(String string) {
        return string != null && ("true".equals(string = string.toLowerCase()) || "on".equals(string));
    }

    public static int toInt(String string) {
        return Integer.valueOf(string);
    }

    public static String[] toStringArray(String string) {
        String[] strings;
        StringUtil.trimAll(strings = StringUtil.splitc(string, ','));
        return strings;
    }

    public static Class[] toClassArray(String string) {
        int i;
        String[] classNames;
        Class[] classes = new Class[i = (classNames = toStringArray(string)).length];
        while (i != 0) {
            --i;
            classes[i] = toClass(classNames[i]);
        }
        return classes;
    }

    public static boolean[] toBoolArray(String string) {
        int i;
        String[] strings;
        boolean[] bools;
        bools = new boolean[i = (strings = toStringArray(string)).length];
        while (i != 0) {
            --i;
            bools[i] = toBoolean(strings[i]);
        }
        return bools;
    }

    public static Boolean[] toBooleanArray(String string) {
        int i;
        String[] strings;
        Boolean[] booleans = new Boolean[i = (strings = toStringArray(string)).length];
        while (i != 0) {
            --i;
            booleans[i] = toBoolean(strings[i]);
        }
        return booleans;
    }

    public static int[] toIntArray(String string) {
        int i;
        String[] strings;
        int[] ints = new int[i = (strings = toStringArray(string)).length];
        while (i != 0) {
            --i;
            ints[i] = toInt(strings[i]);
        }
        return ints;
    }

    public static Integer[] toIntegerArray(String string) {
        int i;
        String[] strings;
        Integer[] ints = new Integer[i = (strings = toStringArray(string)).length];
        while (i != 0) {
            --i;
            ints[i] = toInt(strings[i]);
        }
        return ints;
    }
}
