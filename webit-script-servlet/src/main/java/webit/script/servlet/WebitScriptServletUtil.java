// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script.servlet;

import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author Zqq
 */
public class WebitScriptServletUtil {

    private static final String PATH_ATTRIBUTE_KEY = WebitScriptServletUtil.class.getName().concat("$$PATH");

    public static String getTemplatePath(final HttpServletRequest request) {
        String path;
        if ((path = (String) request.getAttribute(PATH_ATTRIBUTE_KEY)) != null) {
            return path;
        }
        return getRequestPath(request);
    }

    public static String getRequestPath(final HttpServletRequest request) {
        final String pathInfo;
        if ((pathInfo = request.getPathInfo()) == null || pathInfo.length() == 0) {
            return request.getServletPath();
        }
        return request.getServletPath().concat(pathInfo);
    }

    public static void setTemplatePath(HttpServletRequest request, String path) {
        request.setAttribute(PATH_ATTRIBUTE_KEY, path);
    }
}
