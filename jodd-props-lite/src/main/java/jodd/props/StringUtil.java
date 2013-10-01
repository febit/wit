// Copyright (c) 2003-2013, Jodd Team (jodd.org). All Rights Reserved.
package jodd.props;

/**
 *
 * @author Zqq
 */
class StringUtil {

    /**
     * Finds first occurrence of a substring in the given source but within
     * limited range [start, end). It is fastest possible code, but still
     * original
     * <code>String.indexOf(String, int)</code> is much faster (since it uses
     * char[] value directly) and should be used when no range is needed.
     *
     * @param src	source string for examination
     * @param sub	substring to find
     * @param startIndex	starting index
     * @param endIndex	ending index
     * @return index of founded substring or -1 if substring not found
     */
    public static int indexOf(String src, String sub, int startIndex, int endIndex) {
        if (startIndex < 0) {
            startIndex = 0;
        }
        int srclen = src.length();
        if (endIndex > srclen) {
            endIndex = srclen;
        }
        int sublen = sub.length();
        if (sublen == 0) {
            return startIndex > srclen ? srclen : startIndex;
        }

        int total = endIndex - sublen + 1;
        char c = sub.charAt(0);
        mainloop:
        for (int i = startIndex; i < total; i++) {
            if (src.charAt(i) != c) {
                continue;
            }
            int j = 1;
            int k = i + 1;
            while (j < sublen) {
                if (sub.charAt(j) != src.charAt(k)) {
                    continue mainloop;
                }
                j++;
                k++;
            }
            return i;
        }
        return -1;
    }

    /**
     * Trim whitespaces from the left.
     */
    public static String trimLeft(String src) {
        int len = src.length();
        int st = 0;
        while ((st < len) && (isWhitespace(src.charAt(st)))) {
            st++;
        }
        return st > 0 ? src.substring(st) : src;
    }

    /**
     * Trim whitespaces from the right.
     */
    public static String trimRight(String src) {
        int len = src.length();
        int count = len;
        while ((len > 0) && (isWhitespace(src.charAt(len - 1)))) {
            len--;
        }
        return (len < count) ? src.substring(0, len) : src;
    }

    public static boolean containsOnlyWhitespaces(String string) {
        int size = string.length();
        for (int i = 0; i < size; i++) {
            char c = string.charAt(i);
            if (isWhitespace(c) == false) {
                return false;
            }
        }
        return true;
    }

    public static boolean isBlank(String string) {
        return ((string == null) || containsOnlyWhitespaces(string));
    }

    public static boolean isWhitespace(char c) {
        return c <= ' ';
    }
}
