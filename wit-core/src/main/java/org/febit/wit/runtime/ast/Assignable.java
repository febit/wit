package org.febit.wit.runtime.ast;

import org.febit.wit.runtime.InternalContext;
import org.jspecify.annotations.Nullable;

public interface Assignable {

    @Nullable
    Object set(InternalContext context, @Nullable Object value);
}
