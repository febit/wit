// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.core;

import lombok.RequiredArgsConstructor;
import org.febit.wit.lang.TextPosition;
import org.jspecify.annotations.Nullable;

@RequiredArgsConstructor
final class Symbol {

    final int id;
    final TextPosition pos;

    @Nullable
    final Object value;

    /**
     * The parse state.
     */
    int state;
    boolean isOnEdgeOfNewLine = false;
}
