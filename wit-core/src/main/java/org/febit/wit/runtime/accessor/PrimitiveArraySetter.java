package org.febit.wit.runtime.accessor;

import org.febit.wit.exception.ScriptEvaluateException;
import org.jspecify.annotations.Nullable;

@FunctionalInterface
public interface PrimitiveArraySetter<T, C> extends Setter<T> {

    void setValue(T array, int idx, @Nullable C value);

    @Override
    @SuppressWarnings("unchecked")
    default void set(T array, @Nullable Object property, @Nullable Object value) {
        if (property == null) {
            throw new ScriptEvaluateException("Array index is null.");
        }
        if (property instanceof Number idx) {
            try {
                setValue(array, idx.intValue(), (C) value);
                return;
            } catch (ArrayIndexOutOfBoundsException e) {
                throw new ScriptEvaluateException("Array index out of bounds, index=" + idx, e);
            } catch (ClassCastException e) {
                throw new ScriptEvaluateException(e.getMessage(), e);
            }
        }
        throw new ScriptEvaluateException("Invalid property or can't write: array#" + property);
    }
}
