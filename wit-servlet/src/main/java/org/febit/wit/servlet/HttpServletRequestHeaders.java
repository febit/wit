// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.servlet;

import javax.servlet.http.HttpServletRequest;

/**
 * @author zqq90
 */
public final class HttpServletRequestHeaders {

    private final HttpServletRequest request;

    public HttpServletRequestHeaders(HttpServletRequest request) {
        this.request = request;
    }

    public Object get(String key) {
        return request.getHeaders(key);
    }
}
