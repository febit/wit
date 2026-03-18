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
    public Object get(HttpServletRequest bean, @Nullable Object property) {
        if (property == null) {
            return null;
        }
        return switch (property.toString()) {
            case "attrs", "attributes" -> new HttpServletRequestAttributes(bean);
            case "parameters", "params" -> new HttpServletRequestParameters(bean);
            case "headers" -> new HttpServletRequestHeaders(bean);
            case "header" -> new HttpServletRequestHeader(bean);
            case "protocol" -> bean.getProtocol();
            case "secure" -> bean.isSecure();
            case "url", "requestURL" -> bean.getRequestURL();
            case "userPrincipal" -> bean.getUserPrincipal();
            case "locales" -> bean.getLocales();
            case "servletPath" -> bean.getServletPath();
            case "remoteHost" -> bean.getRemoteHost();
            case "remotePort" -> bean.getRemotePort();
            case "remoteUser" -> bean.getRemoteUser();
            case "pathInfo" -> bean.getPathInfo();
            case "session" -> bean.getSession();
            case "requestedSessionIdFromCookie" -> bean.isRequestedSessionIdFromCookie();
            case "attributeNames" -> bean.getAttributeNames();
            case "serverName" -> bean.getServerName();
            case "serverPort" -> bean.getServerPort();
            case "parameterMap" -> bean.getParameterMap();
            case "localPort" -> bean.getLocalPort();
            case "locale" -> bean.getLocale();
            case "requestedSessionIdFromURL", "requestedSessionIdFromUrl" -> bean.isRequestedSessionIdFromURL();
            case "scheme" -> bean.getScheme();
            case "contentLength" -> bean.getContentLength();
            case "contextPath" -> bean.getContextPath();
            case "requestedSessionIdValid" -> bean.isRequestedSessionIdValid();
            case "cookies" -> bean.getCookies();
            case "remoteAddr" -> bean.getRemoteAddr();
            case "headerNames" -> bean.getHeaderNames();
            case "requestedSessionId" -> bean.getRequestedSessionId();
            case "contentType" -> bean.getContentType();
            case "pathTranslated" -> bean.getPathTranslated();
            case "parameterNames" -> bean.getParameterNames();
            case "authType" -> bean.getAuthType();
            case "queryString" -> bean.getQueryString();
            case "localAddr" -> bean.getLocalAddr();
            case "localName" -> bean.getLocalName();
            case "method" -> bean.getMethod();
            case "requestURI" -> bean.getRequestURI();
            case "characterEncoding" -> bean.getCharacterEncoding();
            default -> null;
        };
    }

}
