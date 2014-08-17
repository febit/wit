// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.
package webit.script.util.props;

/**
 * Parser for string macro templates. On parsing, macro values in provided
 * string are resolved and replaced with real values. Once set, one string
 * template parser can be reused for parsing, even using different macro
 * resolvers.
 */
class StringTemplateParser {

    private final static String macroStart = "${";
    private final static String macroEnd = "}";

    /**
     * Parses string template and replaces macros with resolved values.
     */
    static String parse(String template, final MacroResolver macroResolver) {

        if (!template.contains(macroStart)) {
            return template;
        }

        int i = 0;
        int len = template.length();
        StringBuilder result = new StringBuilder(len);

        int startLen = macroStart.length();
        int endLen = macroEnd.length();

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

            while ((j >= 0) && (template.charAt(j) == '\\')) {
                escape = !escape;
                if (escape) {
                    count++;
                }
                j--;
            }
            result.append(template.substring(i, ndx - count));

            if (escape) {
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
                int n = indexOf(template, macroStart, ndx1, ndx2);
                if (n == -1) {
                    break;
                }
                ndx1 = n + startLen;
            }

            String name = template.substring(ndx1, ndx2);

            // find value and append
            String value;
            value = macroResolver.resolve(name);
            if (value == null) {
                value = "";
            }

            if (ndx == ndx1) {
                result.append(value);
                i = ndx2 + endLen;
            } else {
                // inner macro
                template = template.substring(0, ndx1 - startLen) + value + template.substring(ndx2 + endLen);
                len = template.length();
                i = ndx - startLen;
            }
        }
        return result.toString();
    }

    private static int indexOf(String src, String sub, int startIndex, int endIndex) {
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
     * Macro value resolver.
     */
    interface MacroResolver {

        /**
         * Resolves macro value for macro name founded in string template.
         * <code>null</code> values will be replaced with empty strings.
         */
        String resolve(String macroName);
    }
}
