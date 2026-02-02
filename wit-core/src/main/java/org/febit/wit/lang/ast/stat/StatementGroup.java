// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.lang.ast.stat;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import org.febit.wit.InternalContext;
import org.febit.wit.lang.Position;
import org.febit.wit.lang.ast.Statement;
import org.jspecify.annotations.Nullable;

import java.util.List;

@Accessors(fluent = true)
@RequiredArgsConstructor
public class StatementGroup implements Statement {

    private final Statement[] list;
    @Getter
    private final Position position;

    public List<Statement> list() {
        return List.of(this.list);
    }

    @Override
    @Nullable
    public Object execute(InternalContext context) {
        context.visit(this.list);
        return null;
    }

    @Override
    public Statement optimize() {
        if (this.list.length == 0) {
            return NoopStatement.INSTANCE;
        }
        return this;
    }
}
