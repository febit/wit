// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.runtime.ast.template;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import org.febit.wit.runtime.InternalContext;
import org.febit.wit.runtime.Undefined;
import org.febit.wit.runtime.ast.Expression;
import org.febit.wit.runtime.ast.Position;
import org.jspecify.annotations.Nullable;

@Accessors(fluent = true)
@RequiredArgsConstructor
public class TemplateStringValue implements Expression {

    private final Expression[] segments;
    @Getter
    private final Position position;

    @Override
    @Nullable
    public Object execute(InternalContext context) {
        var buf = new StringBuilder();
        for (var segment : segments) {
            var s = segment.execute(context);
            if (s != null && s != Undefined.UNDEFINED) {
                buf.append(s);
            }
        }
        return buf.toString();
    }
}
