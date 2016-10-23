// Copyright (c) 2013-2016, febit.org. All Rights Reserved.
package org.febit.wit.resolvers.impl;

import org.febit.wit.exceptions.ScriptRuntimeException;
import org.febit.wit.io.Out;
import org.febit.wit.lang.InternalVoid;
import org.febit.wit.resolvers.GetResolver;
import org.febit.wit.resolvers.OutResolver;
import org.febit.wit.resolvers.SetResolver;

/**
 *
 * @author zqq90
 */
public class InternalVoidResolver implements GetResolver, SetResolver, OutResolver {

    @Override
    public Object get(Object object, Object property) {
        throw new ScriptRuntimeException("'Void' type has no property.");
    }

    @Override
    public Class getMatchClass() {
        return InternalVoid.class;
    }

    @Override
    public void set(Object object, Object property, Object value) {
        throw new ScriptRuntimeException("'Void' type has no property.");
    }

    @Override
    public void render(Out out, Object bean) {
        //render nothing
    }

}
