// Copyright (c) 2013-2015, Webit Team. All Rights Reserved.
package webit.script.support.jfinal;

import com.jfinal.plugin.activerecord.Model;
import webit.script.resolvers.GetResolver;
import webit.script.resolvers.SetResolver;

/**
 *
 * @author zqq90
 */
public class ModelResolver implements GetResolver, SetResolver {

    @Override
    public Object get(Object bean, Object property) {
        return ((Model) bean).get(property.toString());
    }

    @Override
    public void set(Object bean, Object property, Object value) {
        ((Model) bean).set(property.toString(), value);
    }

    @Override
    public Class<?> getMatchClass() {
        return Model.class;
    }
}
