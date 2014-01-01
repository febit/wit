// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script.resolvers.impl;

import webit.script.exceptions.ScriptRuntimeException;
import webit.script.resolvers.GetResolver;
import webit.script.resolvers.MatchMode;
import webit.script.util.StringUtil;

/**
 *
 * @author Zqq
 */
public class CharSequenceResolver implements GetResolver {

    public MatchMode getMatchMode() {
        return MatchMode.INSTANCEOF;
    }

    public Class<?> getMatchClass() {
        return CharSequence.class;
    }

    public Object get(final Object object, final Object property) {
        if (property instanceof Number) {
            try {
                return ((CharSequence) object).charAt(((Number) property).intValue());
            } catch (IndexOutOfBoundsException e) {
                throw new ScriptRuntimeException(StringUtil.concat("index out of bounds:", property));
            }
        } else {
            if ("size".equals(property)) {
                return ((CharSequence) object).length();
            } else if ("isEmpty".equals(property)) {
                return ((CharSequence) object).length() == 0;
            }
            throw new ScriptRuntimeException(StringUtil.concat("Invalid property or can't read: java.lang.CharSequence#", property));
        }
    }
}
