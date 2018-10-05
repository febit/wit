// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.support.jfinal;

import com.jfinal.plugin.activerecord.Model;
import org.febit.wit.resolvers.GetResolver;
import org.febit.wit.resolvers.SetResolver;

/**
 * @author zqq90
 */
public class ModelResolver implements GetResolver<Model>, SetResolver<Model> {

    @Override
    public Object get(Model bean, Object property) {
        return bean.get(property.toString());
    }

    @Override
    public void set(Model bean, Object property, Object value) {
        bean.set(property.toString(), value);
    }

    @Override
    public Class<Model> getMatchClass() {
        return Model.class;
    }
}
