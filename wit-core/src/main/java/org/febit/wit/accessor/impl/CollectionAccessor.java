// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.accessor.impl;

import org.febit.wit.accessor.Getter;
import org.febit.wit.accessor.Setter;
import org.febit.wit.exceptions.ScriptRuntimeException;
import org.jspecify.annotations.Nullable;

import java.util.Collection;
import java.util.List;

public class CollectionAccessor<T extends Collection<?>> implements Getter<T>, Setter<T> {

    @Nullable
    @Override
    public Object get(T collection, @Nullable Object property) {
        if (property == null) {
            throw new ScriptRuntimeException("Property can't be null for collections.");
        }
        if (property instanceof Number number && collection instanceof List<?> list) {
            try {
                return list.get(number.intValue());
            } catch (IndexOutOfBoundsException e) {
                throw new ScriptRuntimeException("Index out of bounds: " + number, e);
            }
        }
        return switch (property.toString()) {
            case "size", "length" -> collection.size();
            case "isEmpty" -> collection.isEmpty();
            default -> throw new ScriptRuntimeException(
                    "Invalid property or can't read: java.util.Collection#" + property);
        };
    }

    @Override
    @SuppressWarnings("unchecked")
    public void set(T collection, @Nullable Object property, @Nullable Object value) {
        if (!(property instanceof Number number)) {
            throw new ScriptRuntimeException("Property must be a number for collections.");
        }
        var index = number.intValue();
        var size = collection.size();
        if (index >= size) {
            for (int i = index - size; i != 0; i--) {
                collection.add(null);
            }
            ((Collection<Object>) collection).add(value);
            return;
        }
        if (collection instanceof List<?> list) {
            ((List<Object>) list).set(index, value);
            return;
        }
        throw new ScriptRuntimeException("Invalid property or can't write: java.util.Collection#" + property);
    }
}
