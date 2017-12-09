// Copyright (c) 2013-2016, febit.org. All Rights Reserved.
package org.febit.wit.servlet.resolvers;

import org.febit.wit.resolvers.GetResolver;
import org.febit.wit.servlet.HttpServletRequestHeaders;

/**
 *
 * @author zqq90
 */
public class HttpServletRequestHeadersResolver implements GetResolver<HttpServletRequestHeaders> {

    @Override
    public Object get(HttpServletRequestHeaders bean, Object property) {
        return bean.get(property.toString());
    }

    @Override
    public Class<HttpServletRequestHeaders> getMatchClass() {
        return HttpServletRequestHeaders.class;
    }
}
