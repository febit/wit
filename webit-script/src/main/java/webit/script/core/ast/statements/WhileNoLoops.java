// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.core.ast.statements;

import webit.script.Context;
import webit.script.core.VariantIndexer;
import webit.script.core.ast.AbstractStatement;
import webit.script.core.ast.Expression;
import webit.script.core.ast.Statement;
import webit.script.core.runtime.VariantStack;
import webit.script.util.ALU;
import webit.script.util.StatementUtil;

/**
 *
 * @author Zqq
 */
public final class WhileNoLoops extends AbstractStatement {

    private final Expression whileExpr;
    private final VariantIndexer varIndexer;
    private final Statement[] statements;

    public WhileNoLoops(Expression whileExpr, VariantIndexer varIndexer, Statement[] statements, int line, int column) {
        super(line, column);
        this.whileExpr = whileExpr;
        this.varIndexer = varIndexer;
        this.statements = statements;
    }

    public Object execute(final Context context) {
        final VariantStack vars;
        (vars = context.vars).push(varIndexer);
        final Statement[] statements = this.statements;
        while (ALU.isTrue(StatementUtil.execute(whileExpr, context))) {
            StatementUtil.executeInverted(statements, context);
            vars.resetCurrent();
        }
        vars.pop();
        return null;
    }
}
