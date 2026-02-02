// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.accessor.impl;

import org.febit.wit.accessor.Getter;
import org.febit.wit.accessor.Render;
import org.febit.wit.accessor.Setter;
import org.febit.wit.exceptions.ScriptRuntimeException;
import org.febit.wit.lang.Out;
import org.febit.wit.util.bean.BeanUtils;
import org.jspecify.annotations.Nullable;

public class BeanReflectAccessor implements Getter<Object>, Setter<Object>, Render<Object> {

    @Nullable
    @Override
    public Object get(Object obj, @Nullable Object property) {
        if (property == null) {
            throw new ScriptRuntimeException("Property should not be null for bean access.");
        }
        try {
            return BeanUtils.get(obj, property.toString());
        } catch (Exception e) {
            throw new ScriptRuntimeException(e.getMessage(), e);
        }
    }

    @Override
    public void set(Object obj, @Nullable Object property, @Nullable Object value) {
        if (property == null) {
            throw new ScriptRuntimeException("Property should not be null for bean access.");
        }
        try {
            BeanUtils.set(obj, property.toString(), value);
        } catch (Exception e) {
            throw new ScriptRuntimeException(e.getMessage(), e);
        }
    }

    @Override
    public void render(Out out, Object obj) {
        out.write(obj.toString());
    }
}
