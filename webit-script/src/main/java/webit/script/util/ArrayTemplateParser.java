package webit.script.util;

import jodd.util.StringTemplateParser;

/**
 *
 * @author Zqq
 */
public class ArrayTemplateParser extends StringTemplateParser {
    
    private static ArrayTemplateParser _instance = null;

    public static ArrayTemplateParser getInstance() {
        if (_instance == null) {
            _instance = new ArrayTemplateParser();
        }
        return _instance;
    }
    
    public ArrayTemplateParser() {
        this.macroStart = "{";
        this.macroEnd = "}";
    }

    public String parse(String template, Object... context) {
        return parse(template, createArrayMacroResolver(context));
    }

    public static MacroResolver createArrayMacroResolver(final Object[] array) {
        if (array == null || array.length == 0) {
            return new MacroResolver() {
                public String resolve(String macroName) {
                    return null;
                }
            };
        }
        return new MacroResolver() {
            private int len = array.length;
            private int current = 0;

            public String resolve(String macroName) {
                int index;
                if (macroName.length() == 0) {
                   index = current;
                   current++;
                } else {
                     try {
                        index = Integer.parseInt(macroName);
                    } catch (Exception e) {
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
