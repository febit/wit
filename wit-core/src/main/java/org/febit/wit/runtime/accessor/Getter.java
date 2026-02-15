// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.runtime.accessor;

import org.jspecify.annotations.Nullable;

public non-sealed interface Getter<T> extends Accessor<T> {

    @Nullable
    Object get(T obj, @Nullable Object property);
}
