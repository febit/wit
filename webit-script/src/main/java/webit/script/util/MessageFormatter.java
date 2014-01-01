// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script.util;

/**
 *
 * @author zqq90
 */
public class MessageFormatter {

    // ---------------------------------------------------------------- properties
    private final static char macroStart = '{';
    private final static char macroEnd = '}';
    private final static char escapeChar = '\\';
    private final static boolean parseValues = false;
    private final static boolean resolveEscapes = true;

    // ---------------------------------------------------------------- parse
    /**
     * Parses string template and replaces macros with resolved values.
     */
    public static String format(String template, Object... array) {
        if (template == null) {
            return null;
        }
        StringBuilder result = new StringBuilder(template.length());

        int i = 0;
        int len = template.length();

        int currentIndex = 0;
        int arrayLen = array != null ? array.length : 0;

        //
        int index;
        int j;
        int escapeCharcount;
        String value;
        //
        while (i < len) {
            int ndx = template.indexOf(macroStart, i);
            if (ndx == -1) {
                result.append(i == 0 ? template : template.substring(i));
                break;
            }

            // check escaped
            j = ndx - 1;
            while ((j >= 0) && (template.charAt(j) == escapeChar)) {
                j--;
            }

            escapeCharcount = ndx - 1 - j;

            if (resolveEscapes && escapeCharcount > 0) {
                result.append(template.substring(i, ndx - ((escapeCharcount + 1) >> 1)));
            } else {
                result.append(template.substring(i, ndx));
            }

            if ((escapeCharcount & 1) == 1) {// if escapeCharcount is odd
                result.append(macroStart);
                i = ndx + 1;
                continue;
            }

            // find macros end
            ndx += 1;
            int ndx_end = template.indexOf(macroEnd, ndx);
            if (ndx_end == -1) {
                throw new IllegalArgumentException(StringUtil.concat("Invalid string template, unclosed macro at: ", (ndx - 1)));
            }

            if (ndx == ndx_end) {
                // {}
                index = currentIndex++;
            } else {
                //{number}
                try {
                    index = template.charAt(ndx) - '0';
                    for (int k = ndx + 1; k < ndx_end; k++) {
                        index = index * 10 + (template.charAt(k) - '0');
                    }
                } catch (Exception e) {
                    index = -1;
                }
            }

            // find value and append
            if (index < arrayLen && index >= 0 && array[index] != null) {
                value = array[index].toString();
                if (parseValues && value.indexOf(macroStart) >= 0) {
                    value = format(value, array);
                }
                result.append(value);
            }
            i = ndx_end + 1;
        }
        return result.toString();
    }
}
