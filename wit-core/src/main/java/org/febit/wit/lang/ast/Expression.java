// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.lang.ast;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import org.febit.wit.exceptions.ParseException;

/**
 * @author zqq90
 */
public interface Expression extends Statement {

    @Nullable
    default Object getConstValue() {
        throw new ParseException("Can't get a const value from this expression.", getPosition());
    }

    @Override
    @Nonnull
    default Expression optimize() {
        return this;
    }
}
