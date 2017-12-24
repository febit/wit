// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.resolvers.impl;

import java.util.Collection;
import java.util.List;
import org.febit.wit.exceptions.ScriptRuntimeException;
import org.febit.wit.resolvers.GetResolver;
import org.febit.wit.resolvers.SetResolver;
import org.febit.wit.util.StringUtil;

/**
 *
 * @author zqq90
 */
public class CollectionResolver implements GetResolver<Collection>, SetResolver<Collection> {

    @Override
    public Class<Collection> getMatchClass() {
        return Collection.class;
    }

    @Override
    public Object get(Collection collection, Object property) {
        if (property instanceof Number && collection instanceof List) {
            try {
                return ((List) collection).get(((Number) property).intValue());
            } catch (IndexOutOfBoundsException e) {
                throw new ScriptRuntimeException(StringUtil.format("index out of bounds:{}", property), e);
            }
        }
        switch (property.toString()) {
            case "size":
            case "length":
                return collection.size();
            case "isEmpty":
                return collection.isEmpty();
            default:
        }
        throw new ScriptRuntimeException(StringUtil.format("Invalid property or can't read: java.util.Collection#{}", property));
    }

    @SuppressWarnings("unchecked")
    @Override
    public void set(Collection collection, Object property, Object value) {
        if (property instanceof Number) {
            final int index = ((Number) property).intValue();
            final int size = collection.size();
            if (index >= size) {
                for (int i = index - size; i != 0; i--) {
                    collection.add(null);
                }
                collection.add(value);
                return;
            } else if (collection instanceof List) {
                ((List) collection).set(index, value);
                return;
            }
        }
        throw new ScriptRuntimeException(StringUtil.format("Invalid property or can't write: java.util.Collection#{}", property));
    }
}
