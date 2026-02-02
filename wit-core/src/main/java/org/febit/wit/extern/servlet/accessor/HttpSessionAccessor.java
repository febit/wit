// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.extern.servlet.accessor;

import jakarta.servlet.http.HttpSession;
import org.febit.wit.runtime.accessor.Getter;
import org.febit.wit.runtime.accessor.Setter;
import org.jspecify.annotations.Nullable;

public class HttpSessionAccessor implements Getter<HttpSession>, Setter<HttpSession> {

    @Nullable
    @Override
    public Object get(HttpSession bean, @Nullable Object property) {
        if (property == null) {
            return null;
        }
        return bean.getAttribute(property.toString());
    }

    @Override
    public void set(HttpSession bean, @Nullable Object property, @Nullable Object value) {
        if (property == null) {
            return;
        }
        bean.setAttribute(property.toString(), value);
    }
}
