// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.extern.servlet.accessor;

import org.febit.wit.accessor.Getter;
import org.febit.wit.extern.servlet.facade.HttpServletRequestParameters;
import org.jspecify.annotations.Nullable;

public class HttpServletRequestParametersAccessor implements Getter<HttpServletRequestParameters> {

    @Nullable
    @Override
    public Object get(HttpServletRequestParameters bean, @Nullable Object property) {
        if (property == null) {
            return null;
        }
        return bean.get(property.toString());
    }
}
