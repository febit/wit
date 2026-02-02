// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.accessor.impl;

import org.febit.wit.accessor.Getter;
import org.febit.wit.accessor.Setter;
import org.febit.wit.exceptions.ScriptRuntimeException;
import org.jspecify.annotations.Nullable;

public class ObjectArrayAccessor implements Getter<Object[]>, Setter<Object[]> {

    @Nullable
    @Override
    public Object get(@Nullable Object[] array, @Nullable Object property) {
        if (property == null) {
            throw new ScriptRuntimeException("Array index is null.");
        }
        if (property instanceof Number idx) {
            return array[idx.intValue()];
        }
        return switch (property.toString()) {
            case "size", "length" -> array.length;
            case "isEmpty" -> array.length == 0;
            default -> throw new ScriptRuntimeException("Invalid property: array#" + property);
        };
    }

    @Override
    public void set(@Nullable Object[] array, @Nullable Object property, @Nullable Object value) {
        if (property instanceof Number idx) {
            try {
                array[idx.intValue()] = value;
                return;
            } catch (ArrayIndexOutOfBoundsException e) {
                throw new ScriptRuntimeException("Array index out of bounds: " + property, e);
            }
        }
        throw new ScriptRuntimeException("Invalid property: array#" + property);
    }
}
