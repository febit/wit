// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.extern.servlet.facade;

import jakarta.servlet.http.HttpServletRequest;
import org.jspecify.annotations.Nullable;

public record HttpServletRequestHeaders(HttpServletRequest request) {

    @Nullable
    public Object get(String key) {
        return request.getHeaders(key);
    }
}
