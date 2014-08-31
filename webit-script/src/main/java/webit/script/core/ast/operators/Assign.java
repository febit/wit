// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script.core.ast.operators;

import webit.script.Context;
import webit.script.core.ast.Expression;
import webit.script.core.ast.ResetableValueExpression;

/**
 *
 * @author Zqq
 */
public final class Assign extends Expression {

    private final Expression rexpr;
    private final ResetableValueExpression lexpr;

    public Assign(ResetableValueExpression lexpr, Expression rexpr, int line, int column) {
        super(line, column);
        this.lexpr = lexpr;
        this.rexpr = rexpr;
    }

    public Object execute(final Context context) {
        return lexpr.setValue(context, rexpr.execute(context));
    }
}
