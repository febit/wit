// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script.web.resolvers;

import webit.script.resolvers.GetResolver;
import webit.script.resolvers.MatchMode;
import webit.script.web.HttpServletRequestHeaders;

/**
 *
 * @author Zqq
 */
public class HttpServletRequestHeadersResolver implements GetResolver{

    public Object get(Object bean, Object property) {
        return ((HttpServletRequestHeaders) bean).get(property.toString());
    }

    public MatchMode getMatchMode() {
        return MatchMode.EQUALS;
    }

    public Class<?> getMatchClass() {
        return HttpServletRequestHeaders.class;
    }
}
