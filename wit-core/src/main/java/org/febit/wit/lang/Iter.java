// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.lang;

import org.jspecify.annotations.Nullable;

public interface Iter {

    boolean hasNext();

    @Nullable
    Object next();

    int index();
}
