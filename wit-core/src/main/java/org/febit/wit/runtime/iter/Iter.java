// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.runtime.iter;

import org.jspecify.annotations.Nullable;

public interface Iter {

    int index();

    boolean hasNext();

    @Nullable
    Object next();
}
