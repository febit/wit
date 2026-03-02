// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.runtime.accessor.impl;

import org.febit.wit.exception.ScriptEvaluateException;
import org.febit.wit.io.Out;
import org.febit.wit.runtime.Undefined;
import org.febit.wit.runtime.accessor.Getter;
import org.febit.wit.runtime.accessor.Render;
import org.febit.wit.runtime.accessor.Setter;
import org.jspecify.annotations.Nullable;

public class InternalVoidAccessor implements Getter<Undefined>,
        Setter<Undefined>, Render<Undefined> {

    @Nullable
    @Override
    public Object get(Undefined obj, @Nullable Object property) {
        throw new ScriptEvaluateException("'Void' type has no property.");
    }

    @Override
    public void set(Undefined obj, @Nullable Object property, @Nullable Object value) {
        throw new ScriptEvaluateException("'Void' type has no property.");
    }

    @Override
    public void render(Out out, Undefined obj) {
        // nothing to render
    }

}
