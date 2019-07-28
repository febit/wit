// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.servlet;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.val;
import org.febit.wit.Vars;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Enumeration;
import java.util.Map;

/**
 * @author zqq90
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ServletUtil {

    @SuppressWarnings("unchecked")
    public static void exportAttributes(final Map<String, Object> map, final HttpServletRequest request) {
        final Enumeration<String> enumeration = request.getAttributeNames();
        while (enumeration.hasMoreElements()) {
            String key = enumeration.nextElement();
            map.put(key, request.getAttribute(key));
        }
    }

    @SuppressWarnings("unchecked")
    public static void exportParameters(final Map<String, Object> map, final HttpServletRequest request) {
        map.putAll(request.getParameterMap());
    }

    public static Vars wrapToKeyValues(HttpServletRequest request, HttpServletResponse response) {
        return wrapToKeyValues(request, response, true, false);
    }

    public static Vars wrapToKeyValues(HttpServletRequest request, HttpServletResponse response,
                                       boolean exportAttributes, boolean exportParameters) {
        return new ServletVars(request, response, exportAttributes, exportParameters);
    }

    private static final class ServletVars implements Vars {

        private final HttpServletRequest request;
        private final HttpServletResponse response;
        private final boolean exportAttributes;
        private final boolean exportParameters;

        ServletVars(HttpServletRequest request, HttpServletResponse response,
                    boolean exportAttributes, boolean exportParameters) {
            this.request = request;
            this.response = response;
            this.exportAttributes = exportAttributes;
            this.exportParameters = exportParameters;
        }

        @SuppressWarnings("unchecked")
        @Override
        public void exportTo(final Vars.Accepter accepter) {
            val req = this.request;

            accepter.set("request", req);
            accepter.set("response", this.response);

            if (this.exportAttributes) {
                final Enumeration<String> enumeration = req.getAttributeNames();
                while (enumeration.hasMoreElements()) {
                    String key = enumeration.nextElement();
                    accepter.set(key, req.getAttribute(key));
                }
            }

            if (this.exportParameters) {
                final Enumeration<String> enumeration = req.getParameterNames();
                while (enumeration.hasMoreElements()) {
                    String key = enumeration.nextElement();
                    accepter.set(key, req.getParameter(key));
                }
            }
        }
    }
}
