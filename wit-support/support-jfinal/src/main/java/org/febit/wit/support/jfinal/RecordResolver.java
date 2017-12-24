// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.support.jfinal;

import com.jfinal.plugin.activerecord.Record;
import org.febit.wit.resolvers.GetResolver;
import org.febit.wit.resolvers.SetResolver;

/**
 *
 * @author zqq90
 */
public class RecordResolver implements GetResolver<Record>, SetResolver<Record> {

    @Override
    public Object get(Record bean, Object property) {
        return bean.get(property.toString());
    }

    @Override
    public void set(Record bean, Object property, Object value) {
        bean.set(property.toString(), value);
    }

    @Override
    public Class<Record> getMatchClass() {
        return Record.class;
    }
}
