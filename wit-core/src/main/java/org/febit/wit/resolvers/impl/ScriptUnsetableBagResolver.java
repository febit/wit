// Copyright (c) 2013-2016, febit.org. All Rights Reserved.
package org.febit.wit.resolvers.impl;

import org.febit.wit.exceptions.ScriptRuntimeException;
import org.febit.wit.lang.SimpleUnsetableBag;
import org.febit.wit.resolvers.GetResolver;
import org.febit.wit.resolvers.SetResolver;

/**
 *
 * @author zqq90
 * @since 1.4.0
 */
public class ScriptUnsetableBagResolver implements GetResolver, SetResolver {

    @SuppressWarnings("unchecked")
    @Override
    public Object get(Object object, Object property) {
        return ((SimpleUnsetableBag) object).get(property);
    }

    @Override
    public void set(Object object, Object property, Object value) {
        throw new ScriptRuntimeException("This is an unsetable Object.s");
    }

    @Override
    public Class getMatchClass() {
        return SimpleUnsetableBag.class;
    }
}
