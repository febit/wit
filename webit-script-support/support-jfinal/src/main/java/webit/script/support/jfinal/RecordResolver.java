// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script.support.jfinal;

import com.jfinal.plugin.activerecord.Record;
import webit.script.resolvers.GetResolver;
import webit.script.resolvers.MatchMode;
import webit.script.resolvers.SetResolver;

/**
 *
 * @author zqq90
 */
public class RecordResolver implements GetResolver, SetResolver {

    public Object get(Object bean, Object property) {
        return ((Record) bean).get(property.toString());
    }

    public boolean set(Object bean, Object property, Object value) {
        ((Record) bean).set(property.toString(), value);
        return true;
    }

    public MatchMode getMatchMode() {
        return MatchMode.INSTANCEOF;
    }

    public Class<?> getMatchClass() {
        return Record.class;
    }
}
