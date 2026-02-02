// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.extern.servlet.accessor;

import org.febit.wit.accessor.Getter;
import org.febit.wit.extern.servlet.facade.HttpServletRequestHeader;
import org.jspecify.annotations.Nullable;

public class HttpServletRequestHeaderAccessor implements Getter<HttpServletRequestHeader> {
    @Nullable
    @Override
    public Object get(HttpServletRequestHeader bean,@Nullable Object property) {
        if (property == null) {
            return null;
        }
        return bean.get(property.toString());
    }

}
