// Copyright (c) 2013-present, febit.org. All Rights Reserved.
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
public class HttpServletRequestResolver implements GetResolver<HttpServletRequest> {

    @Override
    public Object get(HttpServletRequest bean, Object property) {
        if (property == null) {
            return null;
        }
        switch (property.toString()) {
            case "attrs":
            case "attributes":
                return new HttpServletRequestAttributes(bean);
            case "parameters":
            case "params":
                return new HttpServletRequestParameters(bean);
            case "headers":
                return new HttpServletRequestHeaders(bean);
            case "header":
                return new HttpServletRequestHeader(bean);
            case "protocol":
                return bean.getProtocol();
            case "secure":
                return bean.isSecure();
//            case "inputStream":
//                return bean.getInputStream();
            case "url":
            case "requestURL":
                return bean.getRequestURL();
            case "userPrincipal":
                return bean.getUserPrincipal();
            case "locales":
                return bean.getLocales();
            case "servletPath":
                return bean.getServletPath();
            case "remoteHost":
                return bean.getRemoteHost();
            case "remotePort":
                return bean.getRemotePort();
            case "remoteUser":
                return bean.getRemoteUser();
            case "pathInfo":
                return bean.getPathInfo();
            case "session":
                return bean.getSession();
            case "requestedSessionIdFromCookie":
                return bean.isRequestedSessionIdFromCookie();
            case "attributeNames":
                return bean.getAttributeNames();
            case "serverName":
                return bean.getServerName();
            case "serverPort":
                return bean.getServerPort();
            case "parameterMap":
                return bean.getParameterMap();
            case "localPort":
                return bean.getLocalPort();
            case "locale":
                return bean.getLocale();
            case "requestedSessionIdFromURL":
                return bean.isRequestedSessionIdFromURL();
            case "scheme":
                return bean.getScheme();
            case "contentLength":
                return bean.getContentLength();
            case "contextPath":
                return bean.getContextPath();
            case "requestedSessionIdValid":
                return bean.isRequestedSessionIdValid();
            case "cookies":
                return bean.getCookies();
            case "remoteAddr":
                return bean.getRemoteAddr();
            case "headerNames":
                return bean.getHeaderNames();
            case "requestedSessionId":
                return bean.getRequestedSessionId();
            case "contentType":
                return bean.getContentType();
            case "pathTranslated":
                return bean.getPathTranslated();
            case "parameterNames":
                return bean.getParameterNames();
            case "authType":
                return bean.getAuthType();
            case "queryString":
                return bean.getQueryString();
            case "localAddr":
                return bean.getLocalAddr();
            case "localName":
                return bean.getLocalName();
            case "method":
                return bean.getMethod();
            case "requestedSessionIdFromUrl":
                return bean.isRequestedSessionIdFromURL();
//            case "reader":
//                return bean.getReader();
            case "requestURI":
                return bean.getRequestURI();
            case "characterEncoding":
                return bean.getCharacterEncoding();
            default:
        }
        return null;
    }

    @Override
    public Class<HttpServletRequest> getMatchClass() {
        return HttpServletRequest.class;
    }
}
