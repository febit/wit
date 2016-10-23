// Copyright (c) 2013-2016, febit.org. All Rights Reserved.
package org.febit.wit.support.jfinal;

import com.jfinal.plugin.activerecord.Model;
import org.febit.wit.resolvers.GetResolver;
import org.febit.wit.resolvers.SetResolver;

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
