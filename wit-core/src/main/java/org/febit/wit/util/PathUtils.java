/*
 * Copyright 2013-present febit.org (support@febit.org)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.febit.wit.util;

import lombok.experimental.UtilityClass;
import org.jspecify.annotations.Nullable;

/**
 * refer to the
 * <a href="https://github.com/oblac/jodd">Jodd</a> project.
 */
@UtilityClass
public class PathUtils {

    private static final char UNIX_SEPARATOR = '/';
    private static final char WINDOWS_SEPARATOR = '\\';

    private static boolean isSeparator(char ch) {
        return (ch == UNIX_SEPARATOR) || (ch == WINDOWS_SEPARATOR);
    }

    @Nullable
    public static String parent(@Nullable String path) {
        if (path == null) {
            return null;
        }
        int index = path.lastIndexOf(UNIX_SEPARATOR);
        if (index < 0) {
            return "";
        }
        return path.substring(0, index + 1);
    }

    @Nullable
    public static String sibling(@Nullable String refer, String path) {
        return refer != null
                ? concat(parent(refer), path)
                : path;
    }

    @Nullable
    public static String concat(@Nullable String refer, String path) {
        if (refer == null) {
            return null;
        }
        int len = refer.length();
        int prefix = getPrefixLength(path);
        if (prefix < 0) {
            // invalid full filename
            return null;
        }
        if (prefix > 0) {
            // full filename is already absolute, return normalized
            return normalize(path);
        }
        if (len == 0) {
            // base path is empty, return normalized full filename
            return normalize(path);
        }
        if (isSeparator(refer.charAt(len - 1))) {
            return normalize(refer.concat(path));
        }
        return normalize(refer + '/' + path);
    }

    /**
     * Normalizes a path, removing double and single dot path steps.
     *
     * <p>
     * Windows separators are converted to Unix separators.
     * All duplicate separators are merged into a single separator.
     * All occurrences of "./" are removed.
     * All occurrences of "dir/../" are removed.
     * <p>
     * Leading and trailing separators are preserved.
     * <p>
     *
     * <pre>
     * Overflow cases:
     * /../             -->   null
     * /../a            -->   null
     * ../              -->   null
     *
     * Double slash cases:
     * //a              -->   /a
     * /a//             -->   /a/
     * /a//b            -->   /a/b
     *
     * Dot slash cases:
     * /./              -->   /
     * /a/./            -->   /a/
     * /a/../           -->   /
     * /a/../b          -->   /b
     *
     * Other cases:
     * /a/./b           -->   /a/b
     * /a/./../b        -->   /b
     * /a/.././b        -->   /b
     *
     * Trailing slash cases:
     * /a/..            -->   /
     * /a/../           -->   /
     *
     * /a/              -->   /a/
     * /a               -->   /a
     *
     * /a/.             -->   /a
     * /a/./            -->   /a/
     *
     * /a/b/..          -->   /a
     * /a/b/../         -->   /a/
     * /a/b/.           -->   /a/b
     * /a/b/./          -->   /a/b/
     * </pre>
     *
     * @param path path
     * @return normalized filename
     * @see #normalize(String, boolean)
     */
    @Nullable
    @SuppressWarnings({
            "squid:S3776", // Cognitive Complexity of methods should not be too high
            "java:S6541", // Methods should not perform too many tasks (aka Brain method)
            "squid:ForLoopCounterChangedCheck",
            "squid:LabelsShouldNotBeUsedCheck",
    })
    public static String normalize(@Nullable String path) {
        return normalize(path, true);
    }

    /**
     * Normalizes a path, removing double and single dot path steps.
     *
     * @param path              path
     * @param keepTrailingSlash if true, keeps the trailing slash if it exists in the input; otherwise, removes it
     * @return normalized path
     * @See #normalize(String)
     */
    @Nullable
    @SuppressWarnings({
            "squid:S3776", // Cognitive Complexity of methods should not be too high
            "java:S6541", // Methods should not perform too many tasks (aka Brain method)
            "squid:ForLoopCounterChangedCheck",
            "squid:LabelsShouldNotBeUsedCheck",
    })
    public static String normalize(@Nullable String path, boolean keepTrailingSlash) {
        if (path == null) {
            return null;
        }
        if (path.isEmpty()) {
            return path;
        }

        int prefix = getPrefixLength(path);
        if (prefix < 0) {
            return null;
        }

        int size = path.length();
        char[] buffer = new char[size + 2];  // +1 for possible extra slash, +2 for arraycopy

        path.getChars(0, path.length(), buffer, 0);
        boolean tailingSlash = keepTrailingSlash && isSeparator(path.charAt(size - 1));

        // fix separators throughout
        for (int i = 0; i < size; i++) {
            if (buffer[i] == WINDOWS_SEPARATOR) {
                buffer[i] = UNIX_SEPARATOR;
            }
        }

        // add extra separator on the end to simplify code below
        if (buffer[size - 1] != UNIX_SEPARATOR) {
            buffer[size++] = UNIX_SEPARATOR;
        }

        // adjoining slashes
        for (int i = prefix + 1; i < size; i++) {
            if (buffer[i] == UNIX_SEPARATOR && buffer[i - 1] == UNIX_SEPARATOR) {
                System.arraycopy(buffer, i, buffer, i - 1, size - i);
                size--;
                i--;
            }
        }

        // dot slash
        for (int i = prefix + 1; i < size; i++) {
            if (buffer[i] == UNIX_SEPARATOR && buffer[i - 1] == '.'
                    && (i == prefix + 1 || buffer[i - 2] == UNIX_SEPARATOR)) {
                System.arraycopy(buffer, i + 1, buffer, i - 1, size - i);
                size -= 2;
                i--;
            }
        }

        // double dot slash
        outer:
        for (int i = prefix + 2; i < size; i++) {
            if (buffer[i] != UNIX_SEPARATOR
                    || buffer[i - 1] != '.'
                    || buffer[i - 2] != '.'
                    || (i != prefix + 2 && buffer[i - 3] != UNIX_SEPARATOR)) {
                continue;
            }
            if (i == prefix + 2) {
                return null;
            }
            int j;
            for (j = i - 4; j >= prefix; j--) {
                if (buffer[j] == UNIX_SEPARATOR) {
                    // remove b/../ from a/b/../c
                    System.arraycopy(buffer, i + 1, buffer, j + 1, size - i);
                    size -= (i - j);
                    i = j + 1;
                    continue outer;
                }
            }
            // remove a/../ from a/../c
            System.arraycopy(buffer, i + 1, buffer, prefix, size - i);
            size -= (i + 1 - prefix);
            i = prefix + 1;
        }

        if (size <= 0) {  // should never be less than 0
            return "";
        }
        if (size <= prefix) {  // should never be less than prefix
            return new String(buffer, 0, size);
        }
        if (!tailingSlash) {
            return new String(buffer, 0, size - 1);
        }
        buffer[size - 1] = UNIX_SEPARATOR;
        return new String(buffer, 0, size);
    }

    // ---------------------------------------------------------------- prefix

    /**
     * Returns the length of the filename prefix, such as <code>C:/</code> or <code>~/</code>.
     * <p>
     * This method will handle a file in either Unix or Windows format.
     * <p>
     * The prefix length includes the first slash in the full filename if applicable. Thus, it is possible that the
     * length returned is greater than the length of the input string.
     * <pre>
     * Windows:
     * a\b\c.txt           --> ""          --> relative
     * \a\b\c.txt          --> "\"         --> current drive absolute
     * C:a\b\c.txt         --> "C:"        --> drive relative
     * C:\a\b\c.txt        --> "C:\"       --> absolute
     * \\server\a\b\c.txt  --> "\\server\" --> UNC
     *
     * Unix:
     * a/b/c.txt           --> ""          --> relative
     * /a/b/c.txt          --> "/"         --> absolute
     * ~/a/b/c.txt         --> "~/"        --> current user
     * ~                   --> "~/"        --> current user (slash added)
     * ~user/a/b/c.txt     --> "~user/"    --> named user
     * ~user               --> "~user/"    --> named user (slash added)
     * </pre>
     * <p>
     * The output will be the same irrespective of the machine that the code is running on. ie. both Unix and Windows
     * prefixes are matched regardless.
     *
     * @param filename the filename to find the prefix in, null returns -1
     * @return the length of the prefix, -1 if invalid or null
     */
    @SuppressWarnings({
            "squid:S3776" // Cognitive Complexity of methods should not be too high
    })
    static int getPrefixLength(@Nullable String filename) {
        if (filename == null) {
            return -1;
        }
        final int len = filename.length();
        if (len == 0) {
            return 0;
        }
        final char ch0 = filename.charAt(0);
        if (ch0 == '.') {
            return 0;
        }
        if (ch0 == ':') {
            return -1;
        }
        if (len == 1) {
            if (isSeparator(ch0)) {
                return 1;
            }
            return ch0 == '~' ? 2 : 0;
        }
        char ch1 = filename.charAt(1);
        if (ch0 == '~') {
            int posUnix = filename.indexOf(UNIX_SEPARATOR, 1);
            if (posUnix == -1) {
                return len + 1;  // return a length greater than the input
            }
            return posUnix + 1;
        }
        if (ch1 == ':') {
            if ((ch0 < 'A' || ch0 > 'Z') && (ch0 < 'a' || ch0 > 'z')) {
                return -1;
            }
            if (len == 2 || !isSeparator(filename.charAt(2))) {
                return 2;
            }
            return 3;
        }
        if (!isSeparator(ch0)) {
            return 0;
        }
        if (!isSeparator(ch1)) {
            return 1;
        }
        int posUnix = filename.indexOf(UNIX_SEPARATOR, 2);
        int posWin = filename.indexOf(WINDOWS_SEPARATOR, 2);
        if ((posUnix == -1 && posWin == -1) || posUnix == 2 || posWin == 2) {
            return -1;
        }
        posUnix = posUnix == -1 ? posWin : posUnix;
        posWin = posWin == -1 ? posUnix : posWin;
        return Math.min(posUnix, posWin) + 1;
    }
}
