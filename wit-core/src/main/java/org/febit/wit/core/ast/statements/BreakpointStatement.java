// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.core.ast.statements;

import org.febit.wit.InternalContext;
import org.febit.wit.core.ast.Statement;
import org.febit.wit.debug.BreakpointListener;

/**
 * @author zqq90
 */
public class BreakpointStatement extends Statement {

    private final BreakpointListener listener;
    private final Object label;
    private final Statement statement;

    public BreakpointStatement(BreakpointListener listener, Object label, Statement statement, int line, int column) {
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
        listener.onBreakpoint(label, context, this, null);
        return null;
    }
}
