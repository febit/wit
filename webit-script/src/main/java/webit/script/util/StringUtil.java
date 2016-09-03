// Copyright (c) 2013-2015, Webit Team. All Rights Reserved.
package webit.script.util;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author zqq90
 */
public class StringUtil {

    public static String concat(String s1, String s2, String s3) {
        return new StringBuilder(s1.length() + s2.length() + s3.length())
                .append(s1)
                .append(s2)
                .append(s3)
                .toString();
    }

    public static String concatObjectClass(String string, Object object) {
        return string.concat(object != null ? object.getClass().getName() : "[null]");
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
        final StringBuilder sb = new StringBuilder();
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

    public static String[] toArray(String src) {
        if (src == null || src.length() == 0) {
            return ArrayUtil.EMPTY_STRINGS;
        }

        final char[] srcc = src.toCharArray();
        final int len = srcc.length;

        // list max size = (size + 1) / 2
        List<String> list = new ArrayList<>(
                len > 1024 ? 128
                : len > 64 ? 32
                : (len + 1) >> 1);

        int i = 0;
        while (i < len) {
            //skip empty & splits
            while (i < len) {
                char c = srcc[i];
                if (c != ','
                        && c != '\n'
                        && c != '\r'
                        && c != ' '
                        && c != '\t') {
                    break;
                }
                i++;
            }
            //check if end
            if (i == len) {
                break;
            }
            final int start = i;

            //find end
            while (i < len) {
                char c = srcc[i];
                if (c == ','
                        || c == '\n'
                        || c == '\r') {
                    break;
                }
                i++;
            }
            int end = i;
            //trim back end
            for (;;) {
                char c = srcc[end - 1];
                if (c == ' '
                        || c == '\t') {
                    end--;
                    continue;
                }
                break;
            }
            list.add(new String(srcc, start, end - start));
        }
        if (list.isEmpty()) {
            return ArrayUtil.EMPTY_STRINGS;
        }
        return list.toArray(new String[list.size()]);
    }

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
                throw new IllegalArgumentException(StringUtil.format("Invalid message, unclosed macro at: {}", ndx - 1));
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
