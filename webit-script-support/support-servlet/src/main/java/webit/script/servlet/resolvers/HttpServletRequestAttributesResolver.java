// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script.servlet.resolvers;

import webit.script.resolvers.GetResolver;
import webit.script.resolvers.SetResolver;
import webit.script.servlet.HttpServletRequestAttributes;

/**
 *
 * @author Zqq
 */
public class HttpServletRequestAttributesResolver implements GetResolver, SetResolver {

    public Object get(Object bean, Object property) {
        return ((HttpServletRequestAttributes) bean).get(property.toString());
    }

    public boolean set(Object bean, Object property, Object value) {
        ((HttpServletRequestAttributes) bean).set(property.toString(), value);
        return true;
    }

    public Class<?> getMatchClass() {
        return HttpServletRequestAttributes.class;
    }
}
