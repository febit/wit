package org.febit.wit.resolvers;

import jakarta.annotation.Nullable;
import org.febit.wit.lang.Out;

public interface Accessor {

    void write(Out out, @Nullable Object obj);

    Object get(@Nullable Object bean, Object property);

    void set(@Nullable Object bean, Object property, Object value);
}
