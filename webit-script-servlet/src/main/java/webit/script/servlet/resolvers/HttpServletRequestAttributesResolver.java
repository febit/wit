// Copyright (c) 2013-2015, Webit Team. All Rights Reserved.
package webit.script.servlet.resolvers;

import webit.script.resolvers.GetResolver;
import webit.script.resolvers.SetResolver;
import webit.script.servlet.HttpServletRequestAttributes;

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
