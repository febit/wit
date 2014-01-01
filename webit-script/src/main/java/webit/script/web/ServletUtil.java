// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script.web;

import java.util.Enumeration;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import webit.script.util.keyvalues.KeyValueAccepter;
import webit.script.util.keyvalues.KeyValues;

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

    public static KeyValues wrapToKeyValues(HttpServletRequest request, HttpServletResponse response) {
        return new ServletKeyValues(request, response);
    }

    public static KeyValues wrapToKeyValues(HttpServletRequest request, HttpServletResponse response, boolean exportAttributes, boolean exportParameters) {
        return new ServletKeyValues(request, response, exportAttributes, exportParameters);
    }

    private static final class ServletKeyValues implements KeyValues {

        private final HttpServletRequest request;
        private final HttpServletResponse response;
        private final boolean exportAttributes;
        private final boolean exportParameters;

        public ServletKeyValues(HttpServletRequest request, HttpServletResponse response) {
            this(request, response, true, false);
        }

        public ServletKeyValues(HttpServletRequest request, HttpServletResponse response, boolean exportAttributes, boolean exportParameters) {
            this.request = request;
            this.response = response;
            this.exportAttributes = exportAttributes;
            this.exportParameters = exportParameters;
        }

        @SuppressWarnings("unchecked")
        public void exportTo(final KeyValueAccepter accepter) {
            final HttpServletRequest myRequest = this.request;

            accepter.set("request", myRequest);
            accepter.set("response", this.response);

            if (this.exportAttributes) {
                final Enumeration<String> enumeration = myRequest.getAttributeNames();
                String key;
                while (enumeration.hasMoreElements()) {
                    accepter.set(key = enumeration.nextElement(), myRequest.getAttribute(key));
                }
            }

            if (this.exportParameters) {
                final Enumeration<String> enumeration = myRequest.getParameterNames();
                String key;
                while (enumeration.hasMoreElements()) {
                    accepter.set(key = enumeration.nextElement(), myRequest.getAttribute(key));
                }
            }
        }
    }
}
