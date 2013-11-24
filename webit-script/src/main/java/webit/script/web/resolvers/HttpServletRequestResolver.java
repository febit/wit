// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.web.resolvers;

import javax.servlet.http.HttpServletRequest;
import webit.script.resolvers.GetResolver;
import webit.script.resolvers.MatchMode;
import webit.script.web.HttpServletRequestAttributes;
import webit.script.web.HttpServletRequestHeader;
import webit.script.web.HttpServletRequestHeaders;
import webit.script.web.HttpServletRequestParameters;

/**
 *
 * @author Zqq
 */
public class HttpServletRequestResolver implements GetResolver {

    public Object get(Object bean, Object property) {
        switch (property.hashCode()) {
            case 405645655:
                if ("attributes" == property || "attributes".equals(property)) {
                    return new HttpServletRequestAttributes((HttpServletRequest) bean);
                }
                break;
            case 458736106:
                if ("parameters" == property || "parameters".equals(property)) {
                    return new HttpServletRequestParameters((HttpServletRequest) bean);
                }
                break;
            case 795307910:
                if ("headers" == property || "headers".equals(property)) {
                    return new HttpServletRequestHeaders((HttpServletRequest) bean);
                }
                break;
            case -1221270899:
                if ("header" == property || "header".equals(property)) {
                    return new HttpServletRequestHeader((HttpServletRequest) bean);
                }
                break;
            case -989163880:
                if ("protocol" == property || "protocol".equals(property)) {
                    return ((HttpServletRequest) bean).getProtocol();
                }
                break;
            case -906273929:
                if ("secure" == property || "secure".equals(property)) {
                    return ((HttpServletRequest) bean).isSecure();
                }
                break;
//            case -305982486:
//                if ("inputStream" == property || "inputStream".equals(property)) {
//                    return ((HttpServletRequest) bean).getInputStream();
//                }
//                break;
            case 37099616:
                if ("requestURL" == property || "requestURL".equals(property)) {
                    return ((HttpServletRequest) bean).getRequestURL();
                }
                break;
            case 151959715:
                if ("userPrincipal" == property || "userPrincipal".equals(property)) {
                    return ((HttpServletRequest) bean).getUserPrincipal();
                }
                break;
            case 338410841:
                if ("locales" == property || "locales".equals(property)) {
                    return ((HttpServletRequest) bean).getLocales();
                }
                break;
            case 805109770:
                if ("servletPath" == property || "servletPath".equals(property)) {
                    return ((HttpServletRequest) bean).getServletPath();
                }
                break;
            case 1040961294:
                if ("remoteHost" == property || "remoteHost".equals(property)) {
                    return ((HttpServletRequest) bean).getRemoteHost();
                }
                break;
            case 1041199591:
                if ("remotePort" == property || "remotePort".equals(property)) {
                    return ((HttpServletRequest) bean).getRemotePort();
                }
                break;
            case 1041351985:
                if ("remoteUser" == property || "remoteUser".equals(property)) {
                    return ((HttpServletRequest) bean).getRemoteUser();
                }
                break;
            case 1234084467:
                if ("pathInfo" == property || "pathInfo".equals(property)) {
                    return ((HttpServletRequest) bean).getPathInfo();
                }
                break;
            case 1984987798:
                if ("session" == property || "session".equals(property)) {
                    return ((HttpServletRequest) bean).getSession();
                }
                break;
            case -2012070511:
                if ("requestedSessionIdFromCookie" == property || "requestedSessionIdFromCookie".equals(property)) {
                    return ((HttpServletRequest) bean).isRequestedSessionIdFromCookie();
                }
                break;
            case -1977647604:
                if ("attributeNames" == property || "attributeNames".equals(property)) {
                    return ((HttpServletRequest) bean).getAttributeNames();
                }
                break;
            case -1826110354:
                if ("serverName" == property || "serverName".equals(property)) {
                    return ((HttpServletRequest) bean).getServerName();
                }
                break;
            case -1826037148:
                if ("serverPort" == property || "serverPort".equals(property)) {
                    return ((HttpServletRequest) bean).getServerPort();
                }
                break;
            case -1536267021:
                if ("parameterMap" == property || "parameterMap".equals(property)) {
                    return ((HttpServletRequest) bean).getParameterMap();
                }
                break;
            case -1205322100:
                if ("localPort" == property || "localPort".equals(property)) {
                    return ((HttpServletRequest) bean).getLocalPort();
                }
                break;
            case -1097462182:
                if ("locale" == property || "locale".equals(property)) {
                    return ((HttpServletRequest) bean).getLocale();
                }
                break;
            case -947968670:
                if ("requestedSessionIdFromURL" == property || "requestedSessionIdFromURL".equals(property)) {
                    return ((HttpServletRequest) bean).isRequestedSessionIdFromURL();
                }
                break;
            case -907987547:
                if ("scheme" == property || "scheme".equals(property)) {
                    return ((HttpServletRequest) bean).getScheme();
                }
                break;
            case -540713793:
                if ("contentLength" == property || "contentLength".equals(property)) {
                    return ((HttpServletRequest) bean).getContentLength();
                }
                break;
            case -102982028:
                if ("contextPath" == property || "contextPath".equals(property)) {
                    return ((HttpServletRequest) bean).getContextPath();
                }
                break;
            case 808810297:
                if ("requestedSessionIdValid" == property || "requestedSessionIdValid".equals(property)) {
                    return ((HttpServletRequest) bean).isRequestedSessionIdValid();
                }
                break;
            case 952189583:
                if ("cookies" == property || "cookies".equals(property)) {
                    return ((HttpServletRequest) bean).getCookies();
                }
                break;
            case 1040741719:
                if ("remoteAddr" == property || "remoteAddr".equals(property)) {
                    return ((HttpServletRequest) bean).getRemoteAddr();
                }
                break;
            case 1167889595:
                if ("headerNames" == property || "headerNames".equals(property)) {
                    return ((HttpServletRequest) bean).getHeaderNames();
                }
                break;
            case -2095562109:
                if ("requestedSessionId" == property || "requestedSessionId".equals(property)) {
                    return ((HttpServletRequest) bean).getRequestedSessionId();
                }
                break;
            case -389131437:
                if ("contentType" == property || "contentType".equals(property)) {
                    return ((HttpServletRequest) bean).getContentType();
                }
                break;
            case 977555163:
                if ("pathTranslated" == property || "pathTranslated".equals(property)) {
                    return ((HttpServletRequest) bean).getPathTranslated();
                }
                break;
            case 1117066527:
                if ("parameterNames" == property || "parameterNames".equals(property)) {
                    return ((HttpServletRequest) bean).getParameterNames();
                }
                break;
            case 1432276226:
                if ("authType" == property || "authType".equals(property)) {
                    return ((HttpServletRequest) bean).getAuthType();
                }
                break;
            case -1261064455:
                if ("queryString" == property || "queryString".equals(property)) {
                    return ((HttpServletRequest) bean).getQueryString();
                }
                break;
            case -1205779972:
                if ("localAddr" == property || "localAddr".equals(property)) {
                    return ((HttpServletRequest) bean).getLocalAddr();
                }
                break;
            case -1205395306:
                if ("localName" == property || "localName".equals(property)) {
                    return ((HttpServletRequest) bean).getLocalName();
                }
                break;
            case -1077554975:
                if ("method" == property || "method".equals(property)) {
                    return ((HttpServletRequest) bean).getMethod();
                }
                break;
            case -947967646:
                if ("requestedSessionIdFromUrl" == property || "requestedSessionIdFromUrl".equals(property)) {
                    //return ((HttpServletRequest) bean).isRequestedSessionIdFromUrl();
                    return ((HttpServletRequest) bean).isRequestedSessionIdFromURL();
                }
                break;
//            case -934979389:
//                if ("reader" == property || "reader".equals(property)) {
//                    return ((HttpServletRequest) bean).getReader();
//                }
//                break;
            case 37099613:
                if ("requestURI" == property || "requestURI".equals(property)) {
                    return ((HttpServletRequest) bean).getRequestURI();
                }
                break;
            case 404755356:
                if ("characterEncoding" == property || "characterEncoding".equals(property)) {
                    return ((HttpServletRequest) bean).getCharacterEncoding();
                }
                break;
        }
        return false;
    }

    public MatchMode getMatchMode() {
        return MatchMode.INSTANCEOF;
    }

    public Class<?> getMatchClass() {
        return HttpServletRequest.class;
    }
}
