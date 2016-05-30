// Copyright (c) 2013-2015, Webit Team. All Rights Reserved.
package webit.script.servlet;

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
