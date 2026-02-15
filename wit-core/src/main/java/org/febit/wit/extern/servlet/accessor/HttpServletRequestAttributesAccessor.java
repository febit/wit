// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.extern.servlet.accessor;

import org.febit.wit.extern.servlet.facade.HttpServletRequestAttributes;
import org.febit.wit.runtime.accessor.Getter;
import org.febit.wit.runtime.accessor.Setter;
import org.jspecify.annotations.Nullable;

public class HttpServletRequestAttributesAccessor
        implements Getter<HttpServletRequestAttributes>, Setter<HttpServletRequestAttributes> {
    @Nullable
    @Override
    public Object get(HttpServletRequestAttributes bean, @Nullable Object property) {
        if (property == null) {
            return null;
        }
        return bean.get(property.toString());
    }

    @Override
    public void set(HttpServletRequestAttributes bean, @Nullable Object property, @Nullable Object value) {
        if (property == null) {
            return;
        }
        bean.set(property.toString(), value);
    }
}
