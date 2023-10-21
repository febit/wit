// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.resolvers.impl;

import org.febit.wit.exceptions.ScriptRuntimeException;
import org.febit.wit.io.Out;
import org.febit.wit.lang.InternalVoid;
import org.febit.wit.resolvers.GetResolver;
import org.febit.wit.resolvers.Serializer;
import org.febit.wit.resolvers.SetResolver;

/**
 * @author zqq90
 */
public class InternalVoidSerializer implements GetResolver<InternalVoid>,
        SetResolver<InternalVoid>, Serializer<InternalVoid> {

    @Override
    public Object get(InternalVoid object, Object property) {
        throw new ScriptRuntimeException("'Void' type has no property.");
    }

    @Override
    public Class<InternalVoid> getMatchClass() {
        return InternalVoid.class;
    }

    @Override
    public void set(InternalVoid object, Object property, Object value) {
        throw new ScriptRuntimeException("'Void' type has no property.");
    }

    @Override
    public void render(Out out, InternalVoid bean) {
        //render nothing
    }

}
