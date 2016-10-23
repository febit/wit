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

        switch (property.hashCode()) {
            case 696759469:
                if ("hasNext".equals(property)) {
                    return ((Iter) object).hasNext();
                }
                break;
            case 100346066:
                if ("index".equals(property)) {
                    return ((Iter) object).index();
                }
                break;
            case 2058846118:
                if ("isFirst".equals(property)) {
                    return ((Iter) object).isFirst();
                }
                break;
            case 3377907:
                if ("next".equals(property)) {
                    return ((Iter) object).next();
                }
                break;
            case -1180529308:
                if ("isEven".equals(property)) {
                    return (((Iter) object).index() & 1) != 0; //Note: when index start by 0
                }
                break;
            case 100474789:
                if ("isOdd".equals(property)) {
                    return (((Iter) object).index() & 1) == 0; //Note: when index start by 0
                }
                break;
            default:
        }

        throw new ScriptRuntimeException(StringUtil.format("Invalid property or can't read: org.febit.wit.lang.Iter#{}", property));
    }
}
