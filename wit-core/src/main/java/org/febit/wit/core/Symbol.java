// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.core;

import lombok.RequiredArgsConstructor;
import org.febit.wit.lang.TextPosition;

@RequiredArgsConstructor
final class Symbol {

    final int id;
    final TextPosition pos;
    final Object value;

    /**
     * The parse state.
     */
    int state;
    boolean isOnEdgeOfNewLine = false;
}
