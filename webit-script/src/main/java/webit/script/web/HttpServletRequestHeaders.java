// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.web;

import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author Zqq
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
