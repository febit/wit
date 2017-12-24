// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.servlet.resolvers;

import javax.servlet.http.HttpSession;
import org.febit.wit.resolvers.GetResolver;
import org.febit.wit.resolvers.SetResolver;

/**
 *
 * @author zqq90
 */
public class HttpSessionResolver implements GetResolver<HttpSession>, SetResolver<HttpSession> {

    @Override
    public Object get(HttpSession bean, Object property) {
        return bean.getAttribute(property.toString());
    }

    @Override
    public void set(HttpSession bean, Object property, Object value) {
        bean.setAttribute(property.toString(), value);
    }

    @Override
    public Class<HttpSession> getMatchClass() {
        return HttpSession.class;
    }
}
