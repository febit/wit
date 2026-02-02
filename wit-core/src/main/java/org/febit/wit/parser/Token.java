// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.parser;

import lombok.RequiredArgsConstructor;
import org.febit.wit.runtime.ast.TextPosition;
import org.jspecify.annotations.Nullable;

@RequiredArgsConstructor
public final class Token {

    public final int kind;
    public final TextPosition pos;

    @Nullable
    public final Object value;

    /**
     * The parse state.
     */
    int state;
    boolean isOnEdgeOfNewLine = false;
}
