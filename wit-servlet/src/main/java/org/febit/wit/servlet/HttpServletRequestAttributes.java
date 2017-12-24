// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.servlet;

import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author zqq90
 */
public final class HttpServletRequestAttributes {

    private final HttpServletRequest request;

    public HttpServletRequestAttributes(HttpServletRequest request) {
        this.request = request;
    }

    public Object get(String key) {
        return request.getAttribute(key);
    }

    public void set(String key, Object value) {
        request.setAttribute(key, value);
    }
}
