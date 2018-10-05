// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.resolvers.impl;

import org.febit.wit.exceptions.ScriptRuntimeException;
import org.febit.wit.lang.Iter;
import org.febit.wit.resolvers.GetResolver;
import org.febit.wit.util.StringUtil;

/**
 * @author zqq90
 */
public class IterGetResolver implements GetResolver<Iter> {

    @Override
    public Class<Iter> getMatchClass() {
        return Iter.class;
    }

    @Override
    public Object get(final Iter iter, final Object property) {
        if (property == null) {
            return null;
        }
        switch (property.toString()) {
            case "hasNext":
                return iter.hasNext();
            case "index":
                return iter.index();
            case "isFirst":
                return iter.index() == 0;
            case "next":
                return iter.next();
            case "isEven":
                //Note: index start from 0
                return (iter.index() & 1) != 0;
            case "isOdd":
                //Note: index start from 0
                return (iter.index() & 1) == 0;
            default:
        }
        throw new ScriptRuntimeException(StringUtil.format(
                "Invalid property or can't read: org.febit.wit.lang.Iter#{}", property));
    }
}
