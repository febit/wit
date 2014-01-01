// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script.util;

/**
 *
 * @author zqq90
 */
public class CharUtil {

    public static char highSurrogate(int codePoint) {
        return (char) ((codePoint >>> 10)
                + (Character.MIN_HIGH_SURROGATE - (Character.MIN_SUPPLEMENTARY_CODE_POINT >>> 10)));
    }

    public static char lowSurrogate(int codePoint) {
        return (char) ((codePoint & 0x3ff) + Character.MIN_LOW_SURROGATE);
    }

    public static boolean isSurrogate(char ch) {
        return ch >= Character.MIN_SURROGATE && ch < (Character.MAX_SURROGATE + 1);
    }

    public static boolean isBmpCodePoint(int codePoint) {
        return codePoint >>> 16 == 0;
    }

    public static boolean equalsOne(final char c, final char[] match) {
        for (int i = 0, len = match.length; i < len; i++) {
            if (c == match[i]) {
                return true;
            }
        }
        return false;
    }

    public static int findFirstDiff(char[] source, int index, char match) {
        for (int i = index; i < source.length; i++) {
            if (source[i] != match) {
                return i;
            }
        }
        return -1;
    }

    public static int findFirstDiff(char[] source, int index, char[] match) {
        for (int i = index; i < source.length; i++) {
            if (equalsOne(source[i], match) == false) {
                return i;
            }
        }
        return -1;
    }

    public static int findFirstEqual(char[] source, int index, char match) {
        for (int i = index; i < source.length; i++) {
            if (source[i] == match) {
                return i;
            }
        }
        return -1;
    }

    public static int findFirstEqual(char[] source, int index, char[] match) {
        for (int i = index; i < source.length; i++) {
            if (equalsOne(source[i], match)) {
                return i;
            }
        }
        return -1;
    }

    public static char toLowerAscii(char c) {
        if ((c >= 'A') && (c <= 'Z')) {
            c += (char) 0x20;
        }
        return c;
    }

    public static boolean isWhitespace(char c) {
        return c <= ' ';
    }

    public static int lastNotWhitespaceOrNewLine(final char[] buf, final int from, final int end) {
        int pos;
        finish:
        for (pos = end - 1; pos >= from; pos--) {
            switch (buf[pos]) {
                case ' ':
                case '\t':
                case '\b':
                case '\f':
                    continue finish;
                default:
                    // not a blank line
                    return pos;
            }
        }
        return pos;
    }

    public static int indexOf(char[] source, char[] target, int fromIndex) {
        final int sourceCount = source.length;
        final int targetCount = target.length;
        if (fromIndex >= sourceCount) {
            return (targetCount == 0 ? sourceCount : -1);
        }
        if (fromIndex < 0) {
            fromIndex = 0;
        }
        if (targetCount == 0) {
            return fromIndex;
        }

        char first = target[0];
        int max = sourceCount - targetCount;

        for (int i = fromIndex; i <= max; i++) {
            /* Look for first character. */
            if (source[i] != first) {
                while (++i <= max && source[i] != first);
            }

            /* Found first character, now look at the rest of v2 */
            if (i <= max) {
                int j = i + 1;
                int end = j + targetCount - 1;
                for (int k = 1; j < end && source[j]
                        == target[k]; j++, k++);

                if (j == end) {
                    /* Found whole array. */
                    return i;
                }
            }
        }
        return -1;
    }

}
