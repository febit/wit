// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.parser;

import lombok.RequiredArgsConstructor;
import org.febit.wit.runtime.ast.TextPosition;
import org.jspecify.annotations.Nullable;

@RequiredArgsConstructor
final class Symbol {

    final int kind;
    final TextPosition pos;

    @Nullable
    final Object value;

    /**
     * The parse state.
     */
    int state;
    boolean isOnEdgeOfNewLine = false;
}
