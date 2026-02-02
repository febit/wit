package org.febit.wit.lang.ast;

import org.febit.wit.InternalContext;
import org.jspecify.annotations.Nullable;

public interface Assignable {

    @Nullable
    Object setValue(InternalContext context, @Nullable Object value);
}
