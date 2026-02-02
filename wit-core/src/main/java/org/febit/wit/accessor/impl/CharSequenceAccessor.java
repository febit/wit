// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.accessor.impl;

import org.febit.wit.accessor.Getter;
import org.febit.wit.exceptions.ScriptRuntimeException;
import org.jspecify.annotations.Nullable;

public class CharSequenceAccessor implements Getter<CharSequence> {

    @Nullable
    @Override
    public Object get(final CharSequence seq, @Nullable Object property) {
        if (property == null) {
            throw new ScriptRuntimeException("Property can't be null for CharSequence.");
        }
        if (property instanceof Number number) {
            try {
                return seq.charAt(number.intValue());
            } catch (IndexOutOfBoundsException e) {
                throw new ScriptRuntimeException("index out of bounds: " + number, e);
            }
        }
        return switch (property.toString()) {
            case "size", "length" -> seq.length();
            case "isEmpty" -> seq.isEmpty();
            default -> throw new ScriptRuntimeException(
                    "Invalid property or can't read: java.lang.CharSequence#" + property);
        };
    }
}
