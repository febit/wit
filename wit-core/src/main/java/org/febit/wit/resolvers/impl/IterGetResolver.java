// Copyright (c) 2013-2016, febit.org. All Rights Reserved.
package org.febit.wit.resolvers.impl;

import org.febit.wit.exceptions.ScriptRuntimeException;
import org.febit.wit.lang.Iter;
import org.febit.wit.resolvers.GetResolver;
import org.febit.wit.util.StringUtil;

/**
 *
 * @author zqq90
 */
public class IterGetResolver implements GetResolver {

    @Override
    public Class getMatchClass() {
        return Iter.class;
    }

    @Override
    public Object get(final Object object, final Object property) {
        if (property == null) {
            return null;
        }
        switch (property.toString()) {
            case "hasNext":
                return ((Iter) object).hasNext();
            case "index":
                return ((Iter) object).index();
            case "isFirst":
                return ((Iter) object).isFirst();
            case "next":
                return ((Iter) object).next();
            case "isEven":
                //Note: index start from 0
                return (((Iter) object).index() & 1) != 0;
            case "isOdd":
                //Note: index start from 0
                return (((Iter) object).index() & 1) == 0;
            default:
        }
        throw new ScriptRuntimeException(StringUtil.format("Invalid property or can't read: org.febit.wit.lang.Iter#{}", property));
    }
}
