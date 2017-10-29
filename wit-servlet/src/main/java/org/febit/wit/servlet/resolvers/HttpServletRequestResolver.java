// Copyright (c) 2013-2016, febit.org. All Rights Reserved.
package org.febit.wit.servlet.resolvers;

import javax.servlet.http.HttpServletRequest;
import org.febit.wit.resolvers.GetResolver;
import org.febit.wit.servlet.HttpServletRequestAttributes;
import org.febit.wit.servlet.HttpServletRequestHeader;
import org.febit.wit.servlet.HttpServletRequestHeaders;
import org.febit.wit.servlet.HttpServletRequestParameters;

/**
 *
 * @author zqq90
 */
public class HttpServletRequestResolver implements GetResolver {

    @Override
    public Object get(Object bean, Object property) {
        if (property == null) {
            return null;
        }
        switch (property.toString()) {
            case "attrs":
            case "attributes":
                return new HttpServletRequestAttributes((HttpServletRequest) bean);
            case "parameters":
            case "params":
                return new HttpServletRequestParameters((HttpServletRequest) bean);
            case "headers":
                return new HttpServletRequestHeaders((HttpServletRequest) bean);
            case "header":
                return new HttpServletRequestHeader((HttpServletRequest) bean);
            case "protocol":
                return ((HttpServletRequest) bean).getProtocol();
            case "secure":
                return ((HttpServletRequest) bean).isSecure();
//            case "inputStream":
//                return ((HttpServletRequest) bean).getInputStream();
            case "url":
            case "requestURL":
                return ((HttpServletRequest) bean).getRequestURL();
            case "userPrincipal":
                return ((HttpServletRequest) bean).getUserPrincipal();
            case "locales":
                return ((HttpServletRequest) bean).getLocales();
            case "servletPath":
                return ((HttpServletRequest) bean).getServletPath();
            case "remoteHost":
                return ((HttpServletRequest) bean).getRemoteHost();
            case "remotePort":
                return ((HttpServletRequest) bean).getRemotePort();
            case "remoteUser":
                return ((HttpServletRequest) bean).getRemoteUser();
            case "pathInfo":
                return ((HttpServletRequest) bean).getPathInfo();
            case "session":
                return ((HttpServletRequest) bean).getSession();
            case "requestedSessionIdFromCookie":
                return ((HttpServletRequest) bean).isRequestedSessionIdFromCookie();
            case "attributeNames":
                return ((HttpServletRequest) bean).getAttributeNames();
            case "serverName":
                return ((HttpServletRequest) bean).getServerName();
            case "serverPort":
                return ((HttpServletRequest) bean).getServerPort();
            case "parameterMap":
                return ((HttpServletRequest) bean).getParameterMap();
            case "localPort":
                return ((HttpServletRequest) bean).getLocalPort();
            case "locale":
                return ((HttpServletRequest) bean).getLocale();
            case "requestedSessionIdFromURL":
                return ((HttpServletRequest) bean).isRequestedSessionIdFromURL();
            case "scheme":
                return ((HttpServletRequest) bean).getScheme();
            case "contentLength":
                return ((HttpServletRequest) bean).getContentLength();
            case "contextPath":
                return ((HttpServletRequest) bean).getContextPath();
            case "requestedSessionIdValid":
                return ((HttpServletRequest) bean).isRequestedSessionIdValid();
            case "cookies":
                return ((HttpServletRequest) bean).getCookies();
            case "remoteAddr":
                return ((HttpServletRequest) bean).getRemoteAddr();
            case "headerNames":
                return ((HttpServletRequest) bean).getHeaderNames();
            case "requestedSessionId":
                return ((HttpServletRequest) bean).getRequestedSessionId();
            case "contentType":
                return ((HttpServletRequest) bean).getContentType();
            case "pathTranslated":
                return ((HttpServletRequest) bean).getPathTranslated();
            case "parameterNames":
                return ((HttpServletRequest) bean).getParameterNames();
            case "authType":
                return ((HttpServletRequest) bean).getAuthType();
            case "queryString":
                return ((HttpServletRequest) bean).getQueryString();
            case "localAddr":
                return ((HttpServletRequest) bean).getLocalAddr();
            case "localName":
                return ((HttpServletRequest) bean).getLocalName();
            case "method":
                return ((HttpServletRequest) bean).getMethod();
            case "requestedSessionIdFromUrl":
                return ((HttpServletRequest) bean).isRequestedSessionIdFromURL();
//            case "reader":
//                return ((HttpServletRequest) bean).getReader();
            case "requestURI":
                return ((HttpServletRequest) bean).getRequestURI();
            case "characterEncoding":
                return ((HttpServletRequest) bean).getCharacterEncoding();
            default:
        }
        return null;
    }

    @Override
    public Class<?> getMatchClass() {
        return HttpServletRequest.class;
    }
}
