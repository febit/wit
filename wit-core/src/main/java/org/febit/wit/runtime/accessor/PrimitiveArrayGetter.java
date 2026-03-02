package org.febit.wit.runtime.accessor;

import org.febit.wit.exception.ScriptEvaluateException;
import org.febit.wit.runtime.ALU;
import org.jspecify.annotations.Nullable;

@FunctionalInterface
public interface PrimitiveArrayGetter<T> extends Getter<T> {

    @Nullable
    Object getValue(T array, int idx);

    @Nullable
    @Override
    default Object get(T array, @Nullable Object property) {
        if (property == null) {
            throw new ScriptEvaluateException("Array index is null.");
        }
        if (property instanceof Number idx) {
            return getValue(array, idx.intValue());
        }
        return switch (property.toString()) {
            case "size", "length" -> ALU.size(array);
            case "isEmpty" -> ALU.size(array) == 0;
            default -> throw new ScriptEvaluateException("Invalid property: array#" + property);
        };
    }

}
