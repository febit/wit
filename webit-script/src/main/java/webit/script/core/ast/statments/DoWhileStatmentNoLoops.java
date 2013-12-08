// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.core.ast.statments;

import webit.script.Context;
import webit.script.core.ast.AbstractStatment;
import webit.script.core.ast.Expression;
import webit.script.core.ast.Statment;
import webit.script.core.VariantIndexer;
import webit.script.core.runtime.VariantStack;
import webit.script.util.ALU;
import webit.script.util.StatmentUtil;

/**
 *
 * @author Zqq
 */
public final class DoWhileStatmentNoLoops extends AbstractStatment {

    private final Expression whileExpr;
    private final VariantIndexer varIndexer;
    private final Statment[] statments;

    public DoWhileStatmentNoLoops(Expression whileExpr, VariantIndexer varIndexer, Statment[] statments, int line, int column) {
        super(line, column);
        this.whileExpr = whileExpr;
        this.varIndexer = varIndexer;
        this.statments = statments;
    }

    public Object execute(final Context context) {
        final Statment[] statments = this.statments;
        final VariantStack vars;
        (vars = context.vars).push(varIndexer);
        do {
            StatmentUtil.executeInverted(statments, context);
            vars.resetCurrent();
        } while (ALU.isTrue(StatmentUtil.execute(whileExpr, context)));
        vars.pop();
        return null;
    }
}
