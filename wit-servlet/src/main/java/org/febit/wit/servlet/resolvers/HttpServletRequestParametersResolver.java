// Copyright (c) 2013-2016, febit.org. All Rights Reserved.
package org.febit.wit.servlet.resolvers;

import org.febit.wit.resolvers.GetResolver;
import org.febit.wit.servlet.HttpServletRequestParameters;

/**
 *
 * @author zqq90
 */
public class HttpServletRequestParametersResolver implements GetResolver<HttpServletRequestParameters> {

    @Override
    public Object get(HttpServletRequestParameters bean, Object property) {
        return bean.get(property.toString());
    }

    @Override
    public Class<HttpServletRequestParameters> getMatchClass() {
        return HttpServletRequestParameters.class;
    }
}
