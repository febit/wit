// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.accessor.impl;

import org.febit.wit.accessor.Getter;
import org.febit.wit.accessor.Render;
import org.febit.wit.accessor.Setter;
import org.febit.wit.exceptions.ScriptRuntimeException;
import org.febit.wit.lang.InternalVoid;
import org.febit.wit.lang.Out;
import org.jspecify.annotations.Nullable;

public class InternalVoidAccessor implements Getter<InternalVoid>,
        Setter<InternalVoid>, Render<InternalVoid> {

    @Nullable
    @Override
    public Object get(InternalVoid obj, @Nullable Object property) {
        throw new ScriptRuntimeException("'Void' type has no property.");
    }

    @Override
    public void set(InternalVoid obj, @Nullable Object property,@Nullable  Object value) {
        throw new ScriptRuntimeException("'Void' type has no property.");
    }

    @Override
    public void render(Out out, InternalVoid obj) {
        // nothing to render
    }

}
