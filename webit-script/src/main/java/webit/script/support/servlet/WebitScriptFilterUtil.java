// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.support.servlet;

import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author Zqq
 */
public class WebitScriptFilterUtil {

    private final static String PATH_ATTRIBUTE_KEY = WebitScriptFilterUtil.class.getName().concat("$$PATH");

    public static String getTemplatePath(HttpServletRequest request) {
        String path;
        if ((path = (String) request.getAttribute(PATH_ATTRIBUTE_KEY)) != null) {
            return path;
        } else {
            return request.getServletPath();
        }
    }

    public static void setTemplatePath(HttpServletRequest request, String path) {
        request.setAttribute(PATH_ATTRIBUTE_KEY, path);
    }
}
