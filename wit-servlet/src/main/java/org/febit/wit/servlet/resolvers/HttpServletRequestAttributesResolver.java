// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.servlet.resolvers;

import org.febit.wit.resolvers.GetResolver;
import org.febit.wit.resolvers.SetResolver;
import org.febit.wit.servlet.HttpServletRequestAttributes;

/**
 *
 * @author zqq90
 */
public class HttpServletRequestAttributesResolver implements GetResolver<HttpServletRequestAttributes>, SetResolver<HttpServletRequestAttributes> {

    @Override
    public Object get(HttpServletRequestAttributes bean, Object property) {
        return bean.get(property.toString());
    }

    @Override
    public void set(HttpServletRequestAttributes bean, Object property, Object value) {
        bean.set(property.toString(), value);
    }

    @Override
    public Class<HttpServletRequestAttributes> getMatchClass() {
        return HttpServletRequestAttributes.class;
    }
}
