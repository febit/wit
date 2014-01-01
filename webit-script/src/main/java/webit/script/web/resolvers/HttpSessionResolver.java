// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script.web.resolvers;

import javax.servlet.http.HttpSession;
import webit.script.resolvers.GetResolver;
import webit.script.resolvers.MatchMode;
import webit.script.resolvers.SetResolver;

/**
 *
 * @author Zqq
 */
public class HttpSessionResolver implements GetResolver, SetResolver {

    public Object get(Object bean, Object property) {
        return ((HttpSession) bean).getAttribute(property.toString());
    }

    public boolean set(Object bean, Object property, Object value) {
        ((HttpSession) bean).setAttribute(property.toString(), value);
        return true;
    }

    public MatchMode getMatchMode() {
        return MatchMode.INSTANCEOF;
    }

    public Class<?> getMatchClass() {
        return HttpSession.class;
    }
}
