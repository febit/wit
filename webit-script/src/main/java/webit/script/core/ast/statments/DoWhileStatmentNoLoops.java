// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.core.ast.statments;

import webit.script.Context;
import webit.script.core.ast.AbstractStatment;
import webit.script.core.ast.Expression;
import webit.script.core.ast.Statment;
import webit.script.core.runtime.variant.VariantMap;
import webit.script.core.runtime.variant.VariantStack;
import webit.script.util.ALU;
import webit.script.util.StatmentUtil;

/**
 *
 * @author Zqq
 */
public final class DoWhileStatmentNoLoops extends AbstractStatment {

    private final Expression whileExpr;
    private final VariantMap varMap;
    private final Statment[] statments;

    public DoWhileStatmentNoLoops(Expression whileExpr, VariantMap varMap, Statment[] statments, int line, int column) {
        super(line, column);
        this.whileExpr = whileExpr;
        this.varMap = varMap;
        this.statments = statments;
    }

    public Object execute(final Context context) {
        final Statment[] statments = this.statments;
        final VariantStack vars;
        (vars = context.vars).push(varMap);
        do {
            StatmentUtil.execute(statments, context);
            vars.resetCurrent();
        } while (ALU.toBoolean(StatmentUtil.execute(whileExpr, context)));
        vars.pop();
        return null;
    }
}
