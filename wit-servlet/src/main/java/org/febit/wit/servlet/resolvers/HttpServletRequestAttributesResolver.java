// Copyright (c) 2013-2016, febit.org. All Rights Reserved.
package org.febit.wit.servlet.resolvers;

import org.febit.wit.resolvers.GetResolver;
import org.febit.wit.resolvers.SetResolver;
import org.febit.wit.servlet.HttpServletRequestAttributes;

/**
 *
 * @author zqq90
 */
public class HttpServletRequestAttributesResolver implements GetResolver, SetResolver {

    @Override
    public Object get(Object bean, Object property) {
        return ((HttpServletRequestAttributes) bean).get(property.toString());
    }

    @Override
    public void set(Object bean, Object property, Object value) {
        ((HttpServletRequestAttributes) bean).set(property.toString(), value);
    }

    @Override
    public Class<?> getMatchClass() {
        return HttpServletRequestAttributes.class;
    }
}
