// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script.resolvers.impl;

import webit.script.exceptions.ScriptRuntimeException;
import webit.script.lang.SimpleUnsetableBag;
import webit.script.resolvers.GetResolver;
import webit.script.resolvers.MatchMode;
import webit.script.resolvers.SetResolver;

/**
 *
 * @author zqq90
 * @since 1.4.0
 */
public class ScriptUnsetableBagResolver implements GetResolver, SetResolver {

    @SuppressWarnings("unchecked")
    public Object get(Object object, Object property) {
        return ((SimpleUnsetableBag) object).get(property);
    }

    public boolean set(Object object, Object property, Object value) {
        throw new ScriptRuntimeException("This is an unsetable Object!");
    }

    public MatchMode getMatchMode() {
        return MatchMode.INSTANCEOF;
    }

    public Class<?> getMatchClass() {
        return SimpleUnsetableBag.class;
    }
}
