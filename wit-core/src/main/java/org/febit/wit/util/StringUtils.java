// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.util;

import lombok.experimental.UtilityClass;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@UtilityClass
public class StringUtils {

    public static boolean isNonEmpty(@Nullable String str) {
        return str != null && !str.isEmpty();
    }

    public static String join(@Nullable List<?> list, char separator) {
        if (list == null
                || list.isEmpty()) {
            return "";
        }
        var buf = new StringBuilder();
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
        return switch (c) {
            case ',', '\n', '\r', ' ', '\t' -> true;
            default -> false;
        };
    }

    public static String[] toArray(@Nullable String src) {
        if (src == null || src.isEmpty()) {
            return ArrayUtils.emptyStrings();
        }

        var srcChars = src.toCharArray();
        var len = srcChars.length;

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
                ? ArrayUtils.emptyStrings()
                : list.toArray(new String[0]);
    }
}
