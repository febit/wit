// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.web.resolvers;

import webit.script.resolvers.GetResolver;
import webit.script.resolvers.MatchMode;
import webit.script.web.HttpServletRequestHeader;

/**
 *
 * @author Zqq
 */
public class HttpServletRequestHeaderResolver implements GetResolver{

    public Object get(Object bean, Object property) {
        return ((HttpServletRequestHeader) bean).get(property.toString());
    }

    public MatchMode getMatchMode() {
        return MatchMode.EQUALS;
    }

    public Class<?> getMatchClass() {
        return HttpServletRequestHeader.class;
    }
}
