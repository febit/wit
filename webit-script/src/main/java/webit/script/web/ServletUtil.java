// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.web;

import java.util.Enumeration;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author Zqq
 */
public class ServletUtil {

    @SuppressWarnings("unchecked")
    public static void exportAttributes(final Map<String, Object> map, final HttpServletRequest request) {
        final Enumeration<String> enumeration = request.getAttributeNames();
        String key;
        while (enumeration.hasMoreElements()) {
            map.put(key = enumeration.nextElement(), request.getAttribute(key));
        }
    }

    @SuppressWarnings("unchecked")
    public static void exportParameters(final Map<String, Object> map, final HttpServletRequest request) {
        map.putAll(request.getParameterMap());
    }
}
