/*
 * Copyright 2013-present febit.org (support@febit.org)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.febit.wit.extern.servlet.accessor;

import jakarta.servlet.http.HttpServletRequest;
import org.febit.wit.extern.servlet.facade.HttpServletRequestAttributes;
import org.febit.wit.extern.servlet.facade.HttpServletRequestHeader;
import org.febit.wit.extern.servlet.facade.HttpServletRequestHeaders;
import org.febit.wit.extern.servlet.facade.HttpServletRequestParameters;
import org.febit.wit.runtime.accessor.Getter;
import org.jspecify.annotations.Nullable;

public class HttpServletRequestAccessor implements Getter<HttpServletRequest> {

    @Override
    @Nullable
    @SuppressWarnings({
            "java:S1479" // "switch" statements should not have too many "case" clauses
    })
    public Object get(HttpServletRequest req, @Nullable Object property) {
        if (property == null) {
            return null;
        }
        return switch (property.toString()) {
            case "attrs", "attributes" -> new HttpServletRequestAttributes(req);
            case "parameters", "params" -> new HttpServletRequestParameters(req);
            case "headers" -> new HttpServletRequestHeaders(req);
            case "header" -> new HttpServletRequestHeader(req);
            case "protocol" -> req.getProtocol();
            case "secure" -> req.isSecure();
            case "url", "requestURL" -> req.getRequestURL();
            case "userPrincipal" -> req.getUserPrincipal();
            case "locales" -> req.getLocales();
            case "servletPath" -> req.getServletPath();
            case "remoteHost" -> req.getRemoteHost();
            case "remotePort" -> req.getRemotePort();
            case "remoteUser" -> req.getRemoteUser();
            case "pathInfo" -> req.getPathInfo();
            case "session" -> req.getSession();
            case "requestedSessionIdFromCookie" -> req.isRequestedSessionIdFromCookie();
            case "attributeNames" -> req.getAttributeNames();
            case "serverName" -> req.getServerName();
            case "serverPort" -> req.getServerPort();
            case "parameterMap" -> req.getParameterMap();
            case "localPort" -> req.getLocalPort();
            case "locale" -> req.getLocale();
            case "requestedSessionIdFromURL", "requestedSessionIdFromUrl" -> req.isRequestedSessionIdFromURL();
            case "scheme" -> req.getScheme();
            case "contentLength" -> req.getContentLength();
            case "contextPath" -> req.getContextPath();
            case "requestedSessionIdValid" -> req.isRequestedSessionIdValid();
            case "cookies" -> req.getCookies();
            case "remoteAddr" -> req.getRemoteAddr();
            case "headerNames" -> req.getHeaderNames();
            case "requestedSessionId" -> req.getRequestedSessionId();
            case "contentType" -> req.getContentType();
            case "pathTranslated" -> req.getPathTranslated();
            case "parameterNames" -> req.getParameterNames();
            case "authType" -> req.getAuthType();
            case "queryString" -> req.getQueryString();
            case "localAddr" -> req.getLocalAddr();
            case "localName" -> req.getLocalName();
            case "method" -> req.getMethod();
            case "requestURI" -> req.getRequestURI();
            case "characterEncoding" -> req.getCharacterEncoding();
            default -> null;
        };
    }

}
