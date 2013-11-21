// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.support.jfinal;

import com.jfinal.plugin.activerecord.Model;
import webit.script.resolvers.GetResolver;
import webit.script.resolvers.MatchMode;
import webit.script.resolvers.SetResolver;

/**
 *
 * @author Zqq
 */
public class ModelResolver implements GetResolver, SetResolver {

    public Object get(Object bean, Object property) {
        return ((Model) bean).get(property.toString());
    }

    public boolean set(Object bean, Object property, Object value) {
        ((Model) bean).set(property.toString(), value);
        return true;
    }

    public MatchMode getMatchMode() {
        return MatchMode.INSTANCEOF;
    }

    public Class<?> getMatchClass() {
        return Model.class;
    }
}
