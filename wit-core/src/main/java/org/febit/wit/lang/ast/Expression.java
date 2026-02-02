// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.lang.ast;

import org.febit.wit.exceptions.ParseException;
import org.jspecify.annotations.Nullable;

public interface Expression extends Statement {

    @Nullable
    default Object calcAsConst() {
        throw new ParseException("Can't calculate as const", position());
    }

    @Override
    default Expression optimize() {
        return this;
    }
}
