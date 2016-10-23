// Copyright (c) 2013-2016, febit.org. All Rights Reserved.
package org.febit.wit.servlet.resolvers;

import org.febit.wit.resolvers.GetResolver;
import org.febit.wit.servlet.HttpServletRequestHeader;

/**
 *
 * @author zqq90
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
