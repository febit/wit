package webit.script.util;

/**
 *
 * @author Zqq
 */
public class ArrayTemplateParser {

    private static ArrayTemplateParser _instance = null;

    public static ArrayTemplateParser getInstance() {
        if (_instance == null) {
            _instance = new ArrayTemplateParser();
        }
        return _instance;
    }
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
    public String parse(String template, MacroResolver macroResolver) {
        StringBuilder result = new StringBuilder(template.length());

        int i = 0;
        int len = template.length();

        int startLen = 1;
        int endLen = 1;

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
            //resolveEscapes
            result.append(template.substring(i, ndx - count));
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

            String name = template.substring(ndx1, ndx2);

            // find value and append
            Object value;

            value = macroResolver.resolve(name);
            if (value == null) {
                value = "";
            }


            if (ndx == ndx1) {
                String stringValue = value.toString();
                if (parseValues == true) {
                    //parseValues
                    if (stringValue.indexOf(macroStart) >= 0) {
                        stringValue = parse(stringValue, macroResolver);
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

    // ---------------------------------------------------------------- resolver
    /**
     * Macro value resolver.
     */
    public interface MacroResolver {

        /**
         * Resolves macro value for macro name founded in string template.
         * <code>null</code> values will be replaced with empty strings.
         */
        String resolve(String macroName);
    }

    public String parse(final String template, final Object... context) {
        return parse(template, createArrayMacroResolver(context));
    }
    private static final MacroResolver EMPTY = new MacroResolver() {
        public String resolve(String macroName) {
            return null;
        }
    };

    public static MacroResolver createArrayMacroResolver(final Object[] array) {
        if (array == null || array.length == 0) {
            return EMPTY;
        }
        return new MacroResolver() {
            private final int len = array.length;
            private int current = 0;

            public String resolve(String macroName) {
                int index;
                if (macroName.length() == 0) {
                    index = current;
                    current++;
                } else {
                    try {
                        index = Integer.parseInt(macroName);
                    } catch (Throwable e) {
                        return null;
                    }
                }
                if (index >= 0 && index < len) {
                    Object value = array[index];
                    if (value == null) {
                        return null;
                    }
                    return value.toString();
                }
                return null;
            }
        };
    }
}
