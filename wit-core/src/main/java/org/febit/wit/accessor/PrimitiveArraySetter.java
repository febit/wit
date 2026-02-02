package org.febit.wit.accessor;

import org.febit.wit.exceptions.ScriptRuntimeException;
import org.jspecify.annotations.Nullable;

@FunctionalInterface
public interface PrimitiveArraySetter<T, C> extends Setter<T> {

    void setValue(T array, int idx, @Nullable C value);

    @Override
    @SuppressWarnings("unchecked")
    default void set(T array, @Nullable Object property, @Nullable Object value) {
        if (property == null) {
            throw new ScriptRuntimeException("Array index is null.");
        }
        if (property instanceof Number idx) {
            try {
                setValue(array, idx.intValue(), (C) value);
                return;
            } catch (ArrayIndexOutOfBoundsException e) {
                throw new ScriptRuntimeException("Array index out of bounds, index=" + idx, e);
            } catch (ClassCastException e) {
                throw new ScriptRuntimeException(e.getMessage(), e);
            }
        }
        throw new ScriptRuntimeException("Invalid property or can't write: array#" + property);
    }
}
