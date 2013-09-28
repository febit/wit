package webit.script.util;

/**
 *
 * @author Zqq
 */
public class ArrayTemplateParser {

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
    public static String parse(String template, Object... array) {
        StringBuilder result = new StringBuilder(template.length());

        int i = 0;
        int len = template.length();

        int startLen = 1;
        int endLen = 1;

        int currentIndex = 0;
        int index;
        int arrayLen = array != null ? array.length : 0;

        while (i < len) {
            int ndx = template.indexOf(macroStart, i);
            if (ndx == -1) {
                result.append(i == 0 ? template : template.substring(i));
                break;
            }

            // check escaped
            int j = ndx - 1;
            boolean escape = false;
            int count = 0;

            while ((j >= 0) && (template.charAt(j) == escapeChar)) {
                escape = !escape;
                if (escape) {
                    count++;
                }
                j--;
            }
            if (resolveEscapes) {
                result.append(template.substring(i, ndx - count));
            } else {
                result.append(template.substring(i, ndx));
            }
            if (escape == true) {
                result.append(macroStart);
                i = ndx + startLen;
                continue;
            }

            // find macros end
            ndx += startLen;
            int ndx2 = template.indexOf(macroEnd, ndx);
            if (ndx2 == -1) {
                throw new IllegalArgumentException("Invalid string template, unclosed macro at: " + (ndx - startLen));
            }

            // detect inner macros, there is no escaping
            int ndx1 = ndx;
            while (ndx1 < ndx2) {
                int n = StringUtil.indexOf(template, macroStart, ndx1, ndx2);
                if (n == -1) {
                    break;
                }
                ndx1 = n + startLen;
            }

            //String name = template.substring(ndx1, ndx2);
            //resolve index
            if (ndx1 == ndx2) {
                index = currentIndex++;
            } else {
                try {
                    index = 0;
                    index = template.charAt(ndx1) - '0';
                    for (int k = ndx1 + 1; k < ndx2; k++) {
                        index *= 10;
                        index += template.charAt(k) - '0';
                    }
                } catch (Exception e) {
                    index = -1;
                }
            }

            // find value and append
            Object value;
            if (index < arrayLen && index >= 0) {
                value = array[index];
                if (value == null) {
                    value = StringPool.EMPTY;
                }
            } else {
                value = StringPool.EMPTY;
            }

            if (ndx == ndx1) {
                String stringValue = value.toString();
                if (parseValues) {
                    if (stringValue.indexOf(macroStart) >= 0) {
                        stringValue = parse(stringValue, array);
                    }
                }
                result.append(stringValue);
                i = ndx2 + endLen;
            } else {
                // inner macro
                template = template.substring(0, ndx1 - startLen) + value.toString() + template.substring(ndx2 + endLen);
                len = template.length();
                i = ndx - startLen;
            }
        }
        return result.toString();
    }
}
