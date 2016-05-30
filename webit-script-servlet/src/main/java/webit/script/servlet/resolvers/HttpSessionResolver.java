// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script.servlet.resolvers;

import javax.servlet.http.HttpSession;
import webit.script.resolvers.GetResolver;
import webit.script.resolvers.SetResolver;

/**
 *
 * @author Zqq
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
