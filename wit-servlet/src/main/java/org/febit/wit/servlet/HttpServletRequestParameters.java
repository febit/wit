// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.servlet;

import javax.servlet.http.HttpServletRequest;

/**
 * @author zqq90
 */
public final class HttpServletRequestParameters {

    private final HttpServletRequest request;

    public HttpServletRequestParameters(HttpServletRequest request) {
        this.request = request;
    }

    public Object get(String key) {
        return request.getParameter(key);
    }
}
