// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.util;

/**
 *
 * @author zqq90
 */
public class MessageFormatter {

    public static String format(final String format, final Object... args) {
        return format != null ? ArrayTemplateParser.parse(format, args) : null;
    }
}
