// Copyright (c) 2013-2015, Webit Team. All Rights Reserved.
package webit.script.resolvers.impl;

import webit.script.exceptions.ScriptRuntimeException;
import webit.script.resolvers.GetResolver;
import webit.script.util.StringUtil;

/**
 *
 * @author zqq90
 */
public class CharSequenceResolver implements GetResolver {

    @Override
    public Object get(final Object object, final Object property) {
        if (property instanceof Number) {
            try {
                return ((CharSequence) object).charAt(((Number) property).intValue());
            } catch (IndexOutOfBoundsException e) {
                throw new ScriptRuntimeException(StringUtil.concat("index out of bounds:", property));
            }
        }
        if ("length".equals(property) || "size".equals(property)) {
            return ((CharSequence) object).length();
        }
        if ("isEmpty".equals(property)) {
            return ((CharSequence) object).length() == 0;
        }
        throw new ScriptRuntimeException(StringUtil.concat("Invalid property or can't read: java.lang.CharSequence#", property));
    }

    @Override
    public Class getMatchClass() {
        return CharSequence.class;
    }
}
