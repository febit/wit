// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script.resolvers.impl;

import webit.script.exceptions.ScriptRuntimeException;
import webit.script.resolvers.GetResolver;
import webit.script.resolvers.MatchMode;
import webit.script.util.StringUtil;
import webit.script.util.collection.Iter;

/**
 *
 * @author Zqq
 */
public class IterGetResolver implements GetResolver {

    public MatchMode getMatchMode() {
        return MatchMode.INSTANCEOF;
    }

    public Class<?> getMatchClass() {
        return Iter.class;
    }

    public Object get(final Object object, final Object property) {

        switch (property.hashCode()) {
            case 696759469:
                if ("hasNext" == property || "hasNext".equals(property)) {
                    return ((Iter) object).hasNext();
                }
                break;
            case 100346066:
                if ("index" == property || "index".equals(property)) {
                    return ((Iter) object).index();
                }
                break;
            case 2058846118:
                if ("isFirst" == property || "isFirst".equals(property)) {
                    return ((Iter) object).isFirst();
                }
                break;
            case 3377907:
                if ("next" == property || "next".equals(property)) {
                    return ((Iter) object).next();
                }
                break;
            case -1180529308:
                if ("isEven" == property || "isEven".equals(property)) {
                    return (((Iter) object).index() & 1) != 0; //Note: when index start by 0
                }
                break;
            case 100474789:
                if ("isOdd" == property || "isOdd".equals(property)) {
                    return (((Iter) object).index() & 1) == 0; //Note: when index start by 0
                }
                break;
        }

        throw new ScriptRuntimeException(StringUtil.concat("Invalid property or can't read: webit.tl.util.collection.Iter#", property));
    }
}
