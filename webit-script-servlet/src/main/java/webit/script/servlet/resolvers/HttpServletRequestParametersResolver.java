// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script.servlet.resolvers;

import webit.script.resolvers.GetResolver;
import webit.script.servlet.HttpServletRequestParameters;

/**
 *
 * @author Zqq
 */
public class HttpServletRequestParametersResolver implements GetResolver {

    @Override
    public Object get(Object bean, Object property) {
        return ((HttpServletRequestParameters) bean).get(property.toString());
    }

    @Override
    public Class<?> getMatchClass() {
        return HttpServletRequestParameters.class;
    }
}
