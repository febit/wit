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
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;

class HttpServletRequestAccessorTest {

    @Test
    void test() {
        var accessor = new HttpServletRequestAccessor();
        var req = mock(HttpServletRequest.class);

        assertNull(accessor.get(req, null));
        assertNull(accessor.get(req, "x"));

        reset(req);
        accessor.get(req, "protocol");
        verify(req).getProtocol();

        reset(req);
        accessor.get(req, "secure");
        verify(req).isSecure();

        reset(req);
        accessor.get(req, "url");
        verify(req).getRequestURL();

        reset(req);
        accessor.get(req, "requestURL");
        verify(req).getRequestURL();

        reset(req);
        accessor.get(req, "userPrincipal");
        verify(req).getUserPrincipal();

        reset(req);
        accessor.get(req, "locales");
        verify(req).getLocales();

        reset(req);
        accessor.get(req, "servletPath");
        verify(req).getServletPath();

        reset(req);
        accessor.get(req, "remoteHost");
        verify(req).getRemoteHost();

        reset(req);
        accessor.get(req, "remotePort");
        verify(req).getRemotePort();

        reset(req);
        accessor.get(req, "remoteUser");
        verify(req).getRemoteUser();

        reset(req);
        accessor.get(req, "pathInfo");
        verify(req).getPathInfo();

        reset(req);
        accessor.get(req, "session");
        verify(req).getSession();

        reset(req);
        accessor.get(req, "requestedSessionIdFromCookie");
        verify(req).isRequestedSessionIdFromCookie();

        reset(req);
        accessor.get(req, "attributeNames");
        verify(req).getAttributeNames();

        reset(req);
        accessor.get(req, "serverName");
        verify(req).getServerName();

        reset(req);
        accessor.get(req, "serverPort");
        verify(req).getServerPort();

        reset(req);
        accessor.get(req, "parameterMap");
        verify(req).getParameterMap();

        reset(req);
        accessor.get(req, "localPort");
        verify(req).getLocalPort();

        reset(req);
        accessor.get(req, "locale");
        verify(req).getLocale();

        reset(req);
        accessor.get(req, "requestedSessionIdFromURL");
        verify(req).isRequestedSessionIdFromURL();

        reset(req);
        accessor.get(req, "requestedSessionIdFromUrl");
        verify(req).isRequestedSessionIdFromURL();

        reset(req);
        accessor.get(req, "scheme");
        verify(req).getScheme();

        reset(req);
        accessor.get(req, "contentLength");
        verify(req).getContentLength();

        reset(req);
        accessor.get(req, "contextPath");
        verify(req).getContextPath();

        reset(req);
        accessor.get(req, "requestedSessionIdValid");
        verify(req).isRequestedSessionIdValid();

        reset(req);
        accessor.get(req, "cookies");
        verify(req).getCookies();

        reset(req);
        accessor.get(req, "remoteAddr");
        verify(req).getRemoteAddr();

        reset(req);
        accessor.get(req, "headerNames");
        verify(req).getHeaderNames();

        reset(req);
        accessor.get(req, "requestedSessionId");
        verify(req).getRequestedSessionId();

        reset(req);
        accessor.get(req, "contentType");
        verify(req).getContentType();

        reset(req);
        accessor.get(req, "pathTranslated");
        verify(req).getPathTranslated();

        reset(req);
        accessor.get(req, "parameterNames");
        verify(req).getParameterNames();

        reset(req);
        accessor.get(req, "authType");
        verify(req).getAuthType();

        reset(req);
        accessor.get(req, "queryString");
        verify(req).getQueryString();

        reset(req);
        accessor.get(req, "localAddr");
        verify(req).getLocalAddr();

        reset(req);
        accessor.get(req, "localName");
        verify(req).getLocalName();

        reset(req);
        accessor.get(req, "method");
        verify(req).getMethod();

        reset(req);
        accessor.get(req, "requestURI");
        verify(req).getRequestURI();

        reset(req);
        accessor.get(req, "characterEncoding");
        verify(req).getCharacterEncoding();

    }
}
