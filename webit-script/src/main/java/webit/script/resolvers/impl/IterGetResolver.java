// Copyright (c) 2013-2015, Webit Team. All Rights Reserved.
package webit.script.resolvers.impl;

import webit.script.exceptions.ScriptRuntimeException;
import webit.script.lang.Iter;
import webit.script.resolvers.GetResolver;
import webit.script.util.StringUtil;

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
        }

        throw new ScriptRuntimeException(StringUtil.concat("Invalid property or can't read: webit.script.lang.Iter#", property));
    }
}
