// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.core.ast.statements;

import webit.script.Context;
import webit.script.core.ast.Statement;
import webit.script.debug.BreakPointListener;
import webit.script.util.StatementUtil;

/**
 *
 * @author zqq
 */
public class BreakPointStatement extends Statement {

    private final BreakPointListener listener;
    private final String label;
    private final Statement statement;

    public BreakPointStatement(BreakPointListener listener, String label, Statement statement, int line, int column) {
        super(line, column);
        this.listener = listener;
        this.label = label;
        this.statement = statement;
    }

    public Object execute(Context context) {
        if (statement != null) {
            StatementUtil.execute(statement, context);
        }
        listener.onBreak(label, context, this, null);
        return null;
    }
}
