// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script.util.bean;

import webit.script.util.ClassEntry;
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
        } else if (cls.isArray()) {
            if (cls == Class[].class) {
                return toClassArray(string);
            } else if (cls == ClassEntry[].class) {
                return toClassEntryArray(string);
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
        } else if (cls == String.class) {
            return string;
        } else if (cls == Class.class) {
            return toClass(string);
        } else if (cls == ClassEntry.class) {
            return toClassEntry(string);
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
        return string != null && string.equalsIgnoreCase("true");
    }

    public static int toInt(String string) {
        return Integer.valueOf(string);
    }

    public static ClassEntry toClassEntry(String string) {
        try {
            return ClassEntry.wrap(string);
        } catch (ClassNotFoundException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static String[] toStringArray(String string) {
        String[] strings = StringUtil.splitc(string, ',');
        StringUtil.trimAll(strings);
        return strings;
    }

    public static Class[] toClassArray(String string) {
        final String[] strings = StringUtil.splitc(string);
        final int len = strings.length;
        final Class[] entrys = new Class[len];
        int j = 0;
        String item;
        for (int i = 0; i < len; i++) {
            item = strings[i].trim();
            if (!item.isEmpty()) {
                entrys[j++] = toClass(item);
            }
        }
        if (j == len) {
            return entrys;
        } else {
            final Class[] result = new Class[j];
            System.arraycopy(entrys, 0, result, 0, j);
            return result;
        }
    }

    public static boolean[] toBoolArray(String string) {
        final String[] strings = StringUtil.splitc(string);
        final int len = strings.length;
        final boolean[] entrys = new boolean[len];
        int j = 0;
        String item;
        for (int i = 0; i < len; i++) {
            item = strings[i].trim();
            if (!item.isEmpty()) {
                entrys[j++] = toBoolean(item);
            }
        }
        if (j == len) {
            return entrys;
        } else {
            final boolean[] result = new boolean[j];
            System.arraycopy(entrys, 0, result, 0, j);
            return result;
        }
    }

    public static Boolean[] toBooleanArray(String string) {
        final String[] strings = StringUtil.splitc(string);
        final int len = strings.length;
        final Boolean[] entrys = new Boolean[len];
        int j = 0;
        String item;
        for (int i = 0; i < len; i++) {
            item = strings[i].trim();
            if (!item.isEmpty()) {
                entrys[j++] = toBoolean(item);
            }
        }
        if (j == len) {
            return entrys;
        } else {
            final Boolean[] result = new Boolean[j];
            System.arraycopy(entrys, 0, result, 0, j);
            return result;
        }
    }

    public static int[] toIntArray(String string) {
        final String[] strings = StringUtil.splitc(string);
        final int len = strings.length;
        final int[] entrys = new int[len];
        int j = 0;
        String item;
        for (int i = 0; i < len; i++) {
            item = strings[i].trim();
            if (!item.isEmpty()) {
                entrys[j++] = toInt(item);
            }
        }
        if (j == len) {
            return entrys;
        } else {
            final int[] result = new int[j];
            System.arraycopy(entrys, 0, result, 0, j);
            return result;
        }
    }

    public static Integer[] toIntegerArray(String string) {
        final String[] strings = StringUtil.splitc(string);
        final int len = strings.length;
        final Integer[] entrys = new Integer[len];
        int j = 0;
        String item;
        for (int i = 0; i < len; i++) {
            item = strings[i].trim();
            if (!item.isEmpty()) {
                entrys[j++] = toInt(item);
            }
        }
        if (j == len) {
            return entrys;
        } else {
            final Integer[] result = new Integer[j];
            System.arraycopy(entrys, 0, result, 0, j);
            return result;
        }
    }

    public static ClassEntry[] toClassEntryArray(final String string) {
        final String[] strings = StringUtil.splitc(string);
        final int len = strings.length;
        final ClassEntry[] entrys = new ClassEntry[len];
        int j = 0;
        String item;
        for (int i = 0; i < len; i++) {
            item = strings[i].trim();
            if (!item.isEmpty()) {
                entrys[j++] = toClassEntry(item);
            }
        }
        if (j == len) {
            return entrys;
        } else {
            final ClassEntry[] result = new ClassEntry[j];
            System.arraycopy(entrys, 0, result, 0, j);
            return result;
        }
    }
}
