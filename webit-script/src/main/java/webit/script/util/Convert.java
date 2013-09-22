// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.util;

/**
 *
 * @author Zqq
 */
public class Convert {

    public static Object convert(String string, Class cls) {
        if (string == null) {
            return null;
        }

        if (cls.isArray()) {
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
        }else{
            
            if (cls == String.class) {
                return string;
            } else if (cls == Class.class) {
                return toClass(string);
            }else if (cls == Integer.class) {
                return toInt(string);
            }else if (cls == Boolean.class) {
                return toBoolean(string);
            }
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
        return string != null && "true".equals(string.toLowerCase());
    }

    public static int toInt(String string) {
        return Integer.valueOf(string);
    }

    public static String[] toStringArray(String string) {
        String[] strings = StringUtil.splitc(string, ',');
        StringUtil.trimAll(strings);
        return strings;
    }

    public static Class[] toClassArray(String string) {

        String[] classNames = toStringArray(string);
        Class[] classes = new Class[classNames.length];
        for (int i = 0; i < classes.length; i++) {
            classes[i] = toClass(classNames[i]);
        }
        return classes;
    }

    public static boolean[] toBoolArray(String string) {

        String[] strings = toStringArray(string);
        boolean[] bools = new boolean[strings.length];
        for (int i = 0; i < bools.length; i++) {
            bools[i] = toBoolean(strings[i]);
        }
        return bools;
    }

    public static Boolean[] toBooleanArray(String string) {

        String[] strings = toStringArray(string);
        Boolean[] booleans = new Boolean[strings.length];
        for (int i = 0; i < booleans.length; i++) {
            booleans[i] = toBoolean(strings[i]);
        }
        return booleans;
    }

    public static int[] toIntArray(String string) {

        String[] strings = toStringArray(string);
        int[] ints = new int[strings.length];
        for (int i = 0; i < ints.length; i++) {
            ints[i] = toInt(strings[i]);
        }
        return ints;
    }

    public static Integer[] toIntegerArray(String string) {

        String[] strings = toStringArray(string);
        Integer[] ints = new Integer[strings.length];
        for (int i = 0; i < ints.length; i++) {
            ints[i] = toInt(strings[i]);
        }
        return ints;
    }
}
