// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.runtime.ast.template;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import org.febit.wit.runtime.InternalContext;
import org.febit.wit.runtime.ast.Position;
import org.febit.wit.runtime.ast.Statement;
import org.jspecify.annotations.Nullable;

@Accessors(fluent = true)
@RequiredArgsConstructor
public final class CharArrayTemplateText implements Statement {

    private final char[] chars;
    @Getter
    private final Position position;

    @Nullable
    @Override
    public Object execute(InternalContext context) {
        context.out().write(chars);
        return null;
    }
}
