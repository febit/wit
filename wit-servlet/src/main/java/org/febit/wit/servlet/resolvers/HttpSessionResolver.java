// Copyright (c) 2013-2016, febit.org. All Rights Reserved.
package org.febit.wit.servlet.resolvers;

import javax.servlet.http.HttpSession;
import org.febit.wit.resolvers.GetResolver;
import org.febit.wit.resolvers.SetResolver;

/**
 *
 * @author zqq90
 */
public class HttpSessionResolver implements GetResolver, SetResolver {

    @Override
    public Object get(Object bean, Object property) {
        return ((HttpSession) bean).getAttribute(property.toString());
    }

    @Override
    public void set(Object bean, Object property, Object value) {
        ((HttpSession) bean).setAttribute(property.toString(), value);
    }

    @Override
    public Class<?> getMatchClass() {
        return HttpSession.class;
    }
}
