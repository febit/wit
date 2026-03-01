// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.runtime.ast.statement;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import org.febit.wit.runtime.InternalContext;
import org.febit.wit.runtime.ast.AstUtils;
import org.febit.wit.runtime.ast.FlowControl;
import org.febit.wit.runtime.ast.Position;
import org.febit.wit.runtime.ast.Statement;
import org.febit.wit.runtime.ast.WithFlowControl;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;

@Accessors(fluent = true)
@RequiredArgsConstructor
public class StatementList implements Statement, WithFlowControl {

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

    @Override
    public void collectFlowControls(Consumer<FlowControl> collector) {
        AstUtils.collectFlowControls(collector, this.list);
    }
}
