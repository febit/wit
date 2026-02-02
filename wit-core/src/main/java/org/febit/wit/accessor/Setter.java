// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.accessor;

import org.jspecify.annotations.Nullable;

public non-sealed interface Setter<T> extends Accessor<T> {

    void set(T obj, @Nullable Object property, @Nullable Object value);
}
