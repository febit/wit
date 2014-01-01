// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script.web;

import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author Zqq
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
