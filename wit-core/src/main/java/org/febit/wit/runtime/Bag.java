// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.runtime;

import org.jspecify.annotations.Nullable;

public interface Bag {

    @Nullable
    Object get(@Nullable Object key);

    void set(@Nullable Object key, @Nullable Object value);
}
