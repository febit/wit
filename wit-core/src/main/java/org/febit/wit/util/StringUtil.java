// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.util;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zqq90
 */
public class StringUtil {

    private StringUtil() {
    }

    public static String join(List<?> list, char separator) {
        if (list == null
                || list.isEmpty()) {
            return "";
        }
        final StringBuilder buf = new StringBuilder();
        for (Object item : list) {
            buf.append(item)
                    .append(separator);
        }
        return buf.substring(0, buf.length() - 1);
    }

    private static boolean isArrayValueEnd(char c) {
        return c == ','
                || c == '\n'
                || c == '\r';
    }

    private static boolean isArrayValueEndOrEmpty(char c) {
        switch (c) {
            case ',':
            case '\n':
            case '\r':
            case ' ':
            case '\t':
                return true;
            default:
                return false;
        }
    }

    public static String[] toArray(String src) {
        if (src == null || src.isEmpty()) {
            return ArrayUtil.emptyStrings();
        }

        final char[] srcChars = src.toCharArray();
        final int len = srcChars.length;

        List<String> list = new ArrayList<>(len > 1024 ? 64 : 16);

        int i = 0;
        while (i < len) {
            //skip empty & splits
            while (i < len && isArrayValueEndOrEmpty(srcChars[i])) {
                i++;
            }
            //check if end
            if (i == len) {
                break;
            }
            final int start = i;

            //find end
            while (i < len
                    && !isArrayValueEnd(srcChars[i])) {
                i++;
            }
            int end = i;
            //trim back end
            while (isArrayValueEndOrEmpty(srcChars[end - 1])) {
                end--;
            }
            list.add(new String(srcChars, start, end - start));
        }
        return list.isEmpty()
                ? ArrayUtil.emptyStrings()
                : list.toArray(new String[0]);
    }

    @SuppressWarnings({
            "squid:S135", // Loops should not contain more than a single "break" or "continue" statement
            "squid:S3776" // Cognitive Complexity of methods should not be too high
    })
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
            while (j >= 0
                    && template.charAt(j) == '\\') {
                j--;
            }
            int escapeCharCount = ndx - 1 - j;
            result.append(template, i, escapeCharCount > 0
                    ? ndx - ((escapeCharCount + 1) >> 1)
                    : ndx);
            if ((escapeCharCount & 1) == 1) {
                result.append('{');
                i = ndx + 1;
                continue;
            }
            ndx += 1;
            int ndxEnd = template.indexOf('}', ndx);
            if (ndxEnd == -1) {
                throw new IllegalArgumentException("Invalid message, unclosed macro at: " + (ndx - 1));
            }
            index = ndx == ndxEnd
                    ? currentIndex++
                    : Integer.parseInt(template.substring(ndx, ndxEnd));
            if (index < arrayLen && index >= 0 && array[index] != null) {
                result.append(array[index].toString());
            }
            i = ndxEnd + 1;
        }
        return result.toString();
    }
}
