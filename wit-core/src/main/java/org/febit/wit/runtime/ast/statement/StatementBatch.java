// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.runtime.ast.statement;

import lombok.RequiredArgsConstructor;
import org.febit.wit.runtime.InternalContext;
import org.febit.wit.runtime.ast.Statement;

import java.util.List;

/**
 * A batch of statements, used for internal optimization, not for AST.
 * <p>
 * NOTICE: Except the last one, statements in batch should not have flow control.
 */
@RequiredArgsConstructor(staticName = "of")
public class StatementBatch {

    private final Statement[] statements;

    public static StatementBatch empty() {
        return new StatementBatch(new Statement[0]);
    }

    public static StatementBatch of(List<Statement> statements) {
        return new StatementBatch(statements.toArray(Statement[]::new));
    }

    public void execute(InternalContext context) {
        for (var stat : statements) {
            stat.execute(context);
        }
    }

    public List<Statement> asList() {
        return List.of(this.statements);
    }

    public boolean isEmpty() {
        return this.statements.length == 0;
    }
}
