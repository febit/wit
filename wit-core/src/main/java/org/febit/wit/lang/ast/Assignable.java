package org.febit.wit.lang.ast;

import jakarta.annotation.Nullable;
import org.febit.wit.InternalContext;

public interface Assignable {

    @Nullable
    Object setValue(InternalContext context, @Nullable Object value);
}
