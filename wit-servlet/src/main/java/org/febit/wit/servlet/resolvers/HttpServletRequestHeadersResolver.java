// Copyright (c) 2013-2016, febit.org. All Rights Reserved.
package org.febit.wit.servlet.resolvers;

import org.febit.wit.resolvers.GetResolver;
import org.febit.wit.servlet.HttpServletRequestHeaders;

/**
 *
 * @author zqq90
 */
public class HttpServletRequestHeadersResolver implements GetResolver {

    @Override
    public Object get(Object bean, Object property) {
        return ((HttpServletRequestHeaders) bean).get(property.toString());
    }

    @Override
    public Class<?> getMatchClass() {
        return HttpServletRequestHeaders.class;
    }
}
