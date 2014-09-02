// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script.util;

import java.util.List;

/**
 *
 * @author Zqq
 */
public class StringUtil {

    public static final String[] EMPTY_ARRAY = new String[0];

    public static String concat(String s1, String s2, String s3) {
        return s1.concat(s2).concat(s3);
    }

    public static String concat(String s1, String s2, String s3, String s4) {
        int len = s1.length() + s2.length() + s3.length() + s4.length();
        return new StringBuilder(len)
                .append(s1)
                .append(s2)
                .append(s3)
                .append(s4)
                .toString();
    }

    public static String concat(String string, int number) {
        return string.concat(Integer.toString(number));
    }

    public static String concatObjectClass(String string, Object object) {
        return string.concat(object != null ? object.getClass().getName() : "[null]");
    }

    public static String concat(String string, Object object) {
        return string.concat(object != null ? object.toString() : "null");
    }

    public static String join(List list, char separator) {
        if (list == null) {
            return "";
        }
        int size = list.size();
        if (size == 0) {
            return "";
        }
        if (size == 1) {
            return String.valueOf(list.get(0));
        }
        StringBuilder sb = new StringBuilder();
        boolean notfirst = false;
        for (Object item : list) {
            if (notfirst) {
                sb.append(separator);
            } else {
                notfirst = true;
            }
            sb.append(item);
        }
        return sb.toString();
    }

    private static int findFirstDiff(char[] source, int index) {
        for (int i = index; i < source.length; i++) {
            char c = source[i];
            if (c != ',' && c != '\n' && c != '\r') {
                return i;
            }
        }
        return -1;
    }

    private static int findFirstEqual(char[] source, int index) {
        for (int i = index; i < source.length; i++) {
            char c = source[i];
            if (c == ',' || c == '\n' || c == '\r') {
                return i;
            }
        }
        return -1;
    }

    private static String[] splitc(String src) {
        //XXX: recheck
        char[] srcc = src.toCharArray();

        int maxparts = srcc.length + 1;
        int[] start = new int[maxparts];
        int[] end = new int[maxparts];

        int count = 0;

        start[0] = 0;
        int s = 0, e;
        char c = srcc[0];
        if (c == ',' || c == '\n' || c == '\r') {
            // string starts with delimiter
            end[0] = 0;
            count++;
            s = findFirstDiff(srcc, 1);
            if (s == -1) {
                // nothing after delimiters
                return EMPTY_ARRAY;
            }
            // new start
            start[1] = s;
        }
        for (;;) {
            // find new end
            e = findFirstEqual(srcc, s);
            if (e == -1) {
                end[count] = srcc.length;
                break;
            }
            end[count] = e;

            // find new start
            count++;
            s = findFirstDiff(srcc, e);
            if (s == -1) {
                start[count] = end[count] = srcc.length;
                break;
            }
            start[count] = s;
        }
        count++;
        String[] result = new String[count];
        for (int i = 0; i < count; i++) {
            result[i] = src.substring(start[i], end[i]);
        }
        return result;
    }

    public static String[] toArray(String src) {
        if (src == null || src.length() == 0) {
            return EMPTY_ARRAY;
        }
        final String[] array = splitc(src);
        int count = 0;
        for (String array1 : array) {
            String item = array1.trim();
            if (item.length() == 0) {
                continue;
            }
            array[count++] = item;
        }
        if (count == 0) {
            return EMPTY_ARRAY;
        }
        if (count != array.length) {
            String[] dest = new String[count];
            System.arraycopy(array, 0, dest, 0, count);
            return dest;
        }
        return array;
    }

    /**
     * Parses string template and replaces macros with resolved values.
     */
    public static String format(String template, Object... array) {
        if (template == null) {
            return null;
        }
        if (template.indexOf('{') < 0) {
            return template;
        }
        final StringBuilder result = new StringBuilder(template.length());
        final int len = template.length();
        final int arrayLen = array != null ? array.length : 0;
        int i = 0;
        int currentIndex = 0;
        int index;
        while (i < len) {
            int ndx = template.indexOf('{', i);
            if (ndx == -1) {
                result.append(i == 0 ? template : template.substring(i));
                break;
            }
            int j = ndx - 1;
            while ((j >= 0) && (template.charAt(j) == '\\')) {
                j--;
            }
            int escapeCharcount = ndx - 1 - j;
            result.append(template.substring(i,
                    escapeCharcount > 0
                    ? ndx - ((escapeCharcount + 1) >> 1)
                    : ndx));
            if ((escapeCharcount & 1) == 1) {
                result.append('{');
                i = ndx + 1;
                continue;
            }
            ndx += 1;
            int ndxEnd = template.indexOf('}', ndx);
            if (ndxEnd == -1) {
                throw new IllegalArgumentException(StringUtil.concat("Invalid message, unclosed macro at: ", ndx - 1));
            }
            if (ndx == ndxEnd) {
                index = currentIndex++;
            } else {
                index = template.charAt(ndx) - '0';
                for (int k = ndx + 1; k < ndxEnd; k++) {
                    index = index * 10 + (template.charAt(k) - '0');
                }
            }
            if (index < arrayLen && index >= 0 && array[index] != null) {
                result.append(array[index].toString());
            }
            i = ndxEnd + 1;
        }
        return result.toString();
    }
}
