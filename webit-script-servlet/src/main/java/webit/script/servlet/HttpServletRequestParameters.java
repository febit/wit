// Copyright (c) 2013-2015, Webit Team. All Rights Reserved.
package webit.script.servlet;

import javax.servlet.http.HttpServletRequest;

/**
 *
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
