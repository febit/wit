// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.resolvers.impl;

import webit.script.exceptions.ScriptRuntimeException;
import webit.script.resolvers.GetResolver;
import webit.script.resolvers.MatchMode;
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

    public Object get(Object object, Object property) {
        Iter iter = (Iter) object;

        switch (property.hashCode()) {
            case 696759469:
                if ("hasNext".equals(property)) {
                    return iter.hasNext();
                }
                break;
            case 100346066:
                if ("index".equals(property)) {
                    return iter.index();
                }
                break;
            case 2058846118:
                if ("isFirst".equals(property)) {
                    return iter.isFirst();
                }
                break;
            case 3377907:
                if ("next".equals(property)) {
                    return iter.next();
                }
                break;
            case -1180529308:
                if ("isEven".equals(property)) {
                    return iter.index() % 2 == 1; //Note: when index start by 0
                }
                break;
            case 100474789:
                if ("isOdd".equals(property)) {
                    return iter.index() % 2 == 0; //Note: when index start by 0
                }
                break;
        }

        throw new ScriptRuntimeException("Invalid property or can't read: webit.tl.util.collection.Iter#" + property);
    }
}
