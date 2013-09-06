// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.util;

/**
 *
 * @author zqq90
 */
public class MessageFormatter {

    private final static ArrayTemplateParser PARSER = new ArrayTemplateParser();

    public static String format(String format, Object... args) {
        if (format != null) {
            return PARSER.parse(format, args);
        } else {
            return null;
        }
    }
}
