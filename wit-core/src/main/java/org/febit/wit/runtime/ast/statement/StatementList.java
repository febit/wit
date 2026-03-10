// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.runtime.ast.statement;

import org.febit.wit.runtime.InternalContext;
import org.febit.wit.runtime.ast.FlowControl;
import org.febit.wit.runtime.ast.Position;
import org.febit.wit.runtime.ast.Statement;
import org.febit.wit.runtime.ast.WithFlowControl;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;

public record StatementList(
        List<Statement> statements,
        Position position
) implements Statement, WithFlowControl {

    @Override
    @Nullable
    public Object execute(InternalContext context) {
        throw new IllegalStateException("StatementList should be optimized before execute");
    }

    @Override
    public Statement optimize() {
        if (this.statements.isEmpty()) {
            return NoopStatement.INSTANCE;
        }
        return this;
    }

    @Override
    public void bubbleFlowControls(Consumer<FlowControl> collector) {
        throw new IllegalStateException("StatementList should be optimized before bubble flow controls");
    }
}
