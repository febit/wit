package org.febit.wit.accessor;

import org.febit.wit.exceptions.ScriptRuntimeException;
import org.febit.wit.util.CollectionUtils;
import org.jspecify.annotations.Nullable;

@FunctionalInterface
public interface PrimitiveArrayGetter<T> extends Getter<T> {

    @Nullable
    Object getValue(T array, int idx);

    @Nullable
    @Override
    default Object get(T array, @Nullable Object property) {
        if (property == null) {
            throw new ScriptRuntimeException("Array index is null.");
        }
        if (property instanceof Number idx) {
            return getValue(array, idx.intValue());
        }
        return switch (property.toString()) {
            case "size", "length" -> CollectionUtils.size(array);
            case "isEmpty" -> CollectionUtils.size(array) == 0;
            default -> throw new ScriptRuntimeException("Invalid property: array#" + property);
        };
    }

}
