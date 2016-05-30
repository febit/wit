// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script.servlet.resolvers;

import webit.script.resolvers.GetResolver;
import webit.script.servlet.HttpServletRequestHeader;

/**
 *
 * @author Zqq
 */
public class HttpServletRequestHeaderResolver implements GetResolver{

    @Override
    public Object get(Object bean, Object property) {
        return ((HttpServletRequestHeader) bean).get(property.toString());
    }

    @Override
    public Class<?> getMatchClass() {
        return HttpServletRequestHeader.class;
    }
}
