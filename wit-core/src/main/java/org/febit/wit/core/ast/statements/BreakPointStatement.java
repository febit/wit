// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.core.ast.statements;

import org.febit.wit.InternalContext;
import org.febit.wit.core.ast.Statement;
import org.febit.wit.debug.BreakPointListener;

/**
 * @author zqq90
 */
public class BreakPointStatement extends Statement {

    private final BreakPointListener listener;
    private final Object label;
    private final Statement statement;

    public BreakPointStatement(BreakPointListener listener, Object label, Statement statement, int line, int column) {
        super(line, column);
        this.listener = listener;
        this.label = label;
        this.statement = statement;
    }

    @Override
    public Object execute(InternalContext context) {
        if (statement != null) {
            statement.execute(context);
        }
        listener.onBreak(label, context, this, null);
        return null;
    }
}
