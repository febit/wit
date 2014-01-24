// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script.resolvers.impl;

import webit.script.resolvers.GetResolver;
import webit.script.resolvers.MatchMode;
import webit.script.resolvers.SetResolver;
import webit.script.util.SimpleBag;

/**
 *
 * @author zqq90
 */
public class SimpleBagResolver implements GetResolver, SetResolver {

    public Object get(Object object, Object property) {
        return ((SimpleBag) object).get(property);
    }

    public boolean set(Object object, Object property, Object value) {
        ((SimpleBag) object).set(property, value);
        return true;
    }

    public MatchMode getMatchMode() {
        return MatchMode.INSTANCEOF;
    }

    public Class<?> getMatchClass() {
        return SimpleBag.class;
    }
}
