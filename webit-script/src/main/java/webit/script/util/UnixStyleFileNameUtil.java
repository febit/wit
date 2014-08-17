// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script.util;

/**
 *
 * @author Zqq
 */
public class UnixStyleFileNameUtil {

    private static final char UNIX_SEPARATOR = '/';
    private static final char WINDOWS_SEPARATOR = '\\';
    //

    private static boolean isSeparator(char ch) {
        return (ch == UNIX_SEPARATOR) || (ch == WINDOWS_SEPARATOR);
    }

    // ---------------------------------------------------------------- normalization
    public static String normalize(String filename) {
        return doNormalize(filename, true);
    }

    public static String normalizeNoEndSeparator(String filename) {
        return doNormalize(filename, false);
    }

    public static String getPath(String filename) {
        if (filename != null) {
            int index;
            return (index = filename.lastIndexOf(UNIX_SEPARATOR)) >= 0
                    ? filename.substring(0, index + 1)
                    : "";
        } else {
            return null;
        }
    }

    public static String concat(String basePath, String fullFilenameToAdd) {
        int prefix;
        int len;
        if (basePath != null) {
            if ((prefix = getPrefixLength(fullFilenameToAdd)) == 0 && (len = basePath.length()) != 0) {
                if (isSeparator(basePath.charAt(len - 1))) {
                    return doNormalize(basePath.concat(fullFilenameToAdd), true);
                } else {
                    return doNormalize(StringUtil.concat(basePath, "/", fullFilenameToAdd), true);
                }
            } else if (prefix > 0) {
                return doNormalize(fullFilenameToAdd, true);
            } else {
                return null;
            }
        }
        return null;
    }

    /**
     * Internal method to perform the normalization.
     *
     * @param filename file name
     * @param separator separator character to use
     * @param keepSeparator <code>true</code> to keep the final separator
     * @return normalized filename
     */
    private static String doNormalize(String filename, boolean keepSeparator) {
        if (filename == null) {
            return null;
        }
        int size = filename.length();
        if (size == 0) {
            return filename;
        }
        int prefix = getPrefixLength(filename);
        if (prefix < 0) {
            return null;
        }

        char[] array = new char[size + 2];  // +1 for possible extra slash, +2 for arraycopy
        filename.getChars(0, filename.length(), array, 0);

        // fix separators throughout
        for (int i = 0; i < array.length; i++) {
            if (array[i] == WINDOWS_SEPARATOR) {
                array[i] = UNIX_SEPARATOR;
            }
        }

        // add extra separator on the end to simplify code below
        boolean lastIsDirectory = true;
        if (array[size - 1] != UNIX_SEPARATOR) {
            array[size++] = UNIX_SEPARATOR;
            lastIsDirectory = false;
        }

        // adjoining slashes
        for (int i = prefix + 1; i < size; i++) {
            if (array[i] == UNIX_SEPARATOR && array[i - 1] == UNIX_SEPARATOR) {
                System.arraycopy(array, i, array, i - 1, size - i);
                size--;
                i--;
            }
        }

        // dot slash
        for (int i = prefix + 1; i < size; i++) {
            if (array[i] == UNIX_SEPARATOR && array[i - 1] == '.'
                    && (i == prefix + 1 || array[i - 2] == UNIX_SEPARATOR)) {
                if (i == size - 1) {
                    lastIsDirectory = true;
                }
                System.arraycopy(array, i + 1, array, i - 1, size - i);
                size -= 2;
                i--;
            }
        }

        // double dot slash
        outer:
        for (int i = prefix + 2; i < size; i++) {
            if (array[i] == UNIX_SEPARATOR && array[i - 1] == '.' && array[i - 2] == '.'
                    && (i == prefix + 2 || array[i - 3] == UNIX_SEPARATOR)) {
                if (i == prefix + 2) {
                    return null;
                }
                if (i == size - 1) {
                    lastIsDirectory = true;
                }
                int j;
                for (j = i - 4; j >= prefix; j--) {
                    if (array[j] == UNIX_SEPARATOR) {
                        // remove b/../ from a/b/../c
                        System.arraycopy(array, i + 1, array, j + 1, size - i);
                        size -= (i - j);
                        i = j + 1;
                        continue outer;
                    }
                }
                // remove a/../ from a/../c
                System.arraycopy(array, i + 1, array, prefix, size - i);
                size -= (i + 1 - prefix);
                i = prefix + 1;
            }
        }

        if (size <= 0) {  // should never be less than 0
            return "";
        }
        if (size <= prefix) {  // should never be less than prefix
            return new String(array, 0, size);
        }
        if (lastIsDirectory && keepSeparator) {
            return new String(array, 0, size);  // keep trailing separator
        }
        return new String(array, 0, size - 1);  // lose trailing separator
    }

    // ---------------------------------------------------------------- prefix
    /**
     * Returns the length of the filename prefix, such as <code>C:/</code> or
     * <code>~/</code>.
     * <p>
     * This method will handle a file in either Unix or Windows format.
     * <p>
     * The prefix length includes the first slash in the full filename if
     * applicable. Thus, it is possible that the length returned is greater than
     * the length of the input string.
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
     * The output will be the same irrespective of the machine that the code is
     * running on. ie. both Unix and Windows prefixes are matched regardless.
     *
     * @param filename the filename to find the prefix in, null returns -1
     * @return the length of the prefix, -1 if invalid or null
     */
    public static int getPrefixLength(String filename) {
        if (filename == null) {
            return -1;
        }
        int len;
        if ((len = filename.length()) == 0) {
            return 0;
        }
        char ch0;
        if ((ch0 = filename.charAt(0)) == ':') {
            return -1;
        }
        if (len == 1) {
//            if (ch0 == '~') {
//                return 2;  // return a length greater than the input
//            }
            return (isSeparator(ch0) ? 1 : (ch0 == '~' ? 2 : 0));
        } else {
            char ch1;
            if (ch0 == '~') {
                int posUnix = filename.indexOf(UNIX_SEPARATOR, 1);
                int posWin = filename.indexOf(WINDOWS_SEPARATOR, 1);
                if (posUnix == -1 && posWin == -1) {
                    return len + 1;  // return a length greater than the input
                }
                posUnix = (posUnix == -1 ? posWin : posUnix);
                posWin = (posWin == -1 ? posUnix : posWin);
                return Math.min(posUnix, posWin) + 1;
            } else if ((ch1 = filename.charAt(1)) == ':') {
                if ((ch0 >= 'A' && ch0 <= 'Z') || (ch0 >= 'a' && ch0 <= 'z')) {
                    if (len == 2 || isSeparator(filename.charAt(2)) == false) {
                        return 2;
                    }
                    return 3;
                }
                return -1;
            } else if (isSeparator(ch0) && isSeparator(ch1)) {
                int posUnix = filename.indexOf(UNIX_SEPARATOR, 2);
                int posWin = filename.indexOf(WINDOWS_SEPARATOR, 2);
                if ((posUnix == -1 && posWin == -1) || posUnix == 2 || posWin == 2) {
                    return -1;
                }
                posUnix = (posUnix == -1 ? posWin : posUnix);
                posWin = (posWin == -1 ? posUnix : posWin);
                return Math.min(posUnix, posWin) + 1;
            } else {
                return (isSeparator(ch0) ? 1 : 0);
            }
        }
    }
}
