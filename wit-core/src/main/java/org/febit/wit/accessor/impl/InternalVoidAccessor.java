// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.accessor.impl;

import org.febit.wit.Out;
import org.febit.wit.accessor.Getter;
import org.febit.wit.accessor.Render;
import org.febit.wit.accessor.Setter;
import org.febit.wit.exceptions.ScriptRuntimeException;
import org.febit.wit.runtime.Undefined;
import org.jspecify.annotations.Nullable;

public class InternalVoidAccessor implements Getter<Undefined>,
        Setter<Undefined>, Render<Undefined> {

    @Nullable
    @Override
    public Object get(Undefined obj, @Nullable Object property) {
        throw new ScriptRuntimeException("'Void' type has no property.");
    }

    @Override
    public void set(Undefined obj, @Nullable Object property, @Nullable Object value) {
        throw new ScriptRuntimeException("'Void' type has no property.");
    }

    @Override
    public void render(Out out, Undefined obj) {
        // nothing to render
    }

}
