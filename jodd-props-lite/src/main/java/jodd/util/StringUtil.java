// Copyright (c) 2003-2013, Jodd Team (jodd.org). All Rights Reserved.

package jodd.util;

/**
 *
 * @author Zqq
 */
public class StringUtil {
    
    	/**
	 * Splits a string in several parts (tokens) that are separated by delimiter
	 * characters. Delimiter may contains any number of character and it is
	 * always surrounded by two strings.
	 *
	 * @param src    source to examine
	 * @param d      string with delimiter characters
	 *
	 * @return array of tokens
	 */
	public static String[] splitc(String src, String d) {
		if ((d.length() == 0) || (src.length() == 0) ) {
			return new String[] {src};
		}
		char[] delimiters = d.toCharArray();
		char[] srcc = src.toCharArray();

		int maxparts = srcc.length + 1;
		int[] start = new int[maxparts];
		int[] end = new int[maxparts];

		int count = 0;

		start[0] = 0;
		int s = 0, e;
		if (webit.script.util.CharUtil.equalsOne(srcc[0], delimiters) == true) {	// string starts with delimiter
			end[0] = 0;
			count++;
			s = webit.script.util.CharUtil.findFirstDiff(srcc, 1, delimiters);
			if (s == -1) {							// nothing after delimiters
				return new String[] {"", ""};
			}
			start[1] = s;							// new start
		}
		while (true) {
			// find new end
			e = webit.script.util.CharUtil.findFirstEqual(srcc, s, delimiters);
			if (e == -1) {
				end[count] = srcc.length;
				break;
			}
			end[count] = e;

			// find new start
			count++;
			s = webit.script.util.CharUtil.findFirstDiff(srcc, e, delimiters);
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
        
	/**
	 * Finds first occurrence of a substring in the given source but within limited range [start, end).
	 * It is fastest possible code, but still original <code>String.indexOf(String, int)</code>
	 * is much faster (since it uses char[] value directly) and should be used when no range is needed.
	 *
	 * @param src		source string for examination
	 * @param sub		substring to find
	 * @param startIndex	starting index
	 * @param endIndex		ending index
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
				j++; k++;
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
