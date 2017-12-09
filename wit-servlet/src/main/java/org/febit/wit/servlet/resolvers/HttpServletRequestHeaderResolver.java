// Copyright (c) 2013-2016, febit.org. All Rights Reserved.
package org.febit.wit.servlet.resolvers;

import org.febit.wit.resolvers.GetResolver;
import org.febit.wit.servlet.HttpServletRequestHeader;

/**
 *
 * @author zqq90
 */
public class HttpServletRequestHeaderResolver implements GetResolver<HttpServletRequestHeader> {

    @Override
    public Object get(HttpServletRequestHeader bean, Object property) {
        return bean.get(property.toString());
    }

    @Override
    public Class<HttpServletRequestHeader> getMatchClass() {
        return HttpServletRequestHeader.class;
    }
}
