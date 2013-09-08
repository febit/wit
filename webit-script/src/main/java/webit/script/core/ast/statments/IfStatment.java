// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.core.ast.statments;

import webit.script.Context;
import webit.script.core.ast.AbstractStatment;
import webit.script.core.ast.Expression;
import webit.script.core.ast.Optimizable;
import webit.script.core.ast.Statment;
import webit.script.util.ALU;
import webit.script.util.StatmentUtil;

/**
 *
 * @author Zqq
 */
public final class IfStatment extends AbstractStatment implements Optimizable {

    private final Expression ifExpr;
    private final Statment thenStatment;
    private final Statment elseStatment;

    public IfStatment(Expression ifExpr, Statment thenStatment, Statment elseStatment, int line, int column) {
        super(line, column);
        this.ifExpr = ifExpr;
        this.thenStatment = thenStatment;
        this.elseStatment = elseStatment;
    }

    public void execute(final Context context) {
        if (ALU.toBoolean(StatmentUtil.execute(ifExpr, context))) {
            if (thenStatment != null) {
                StatmentUtil.execute(thenStatment, context);
            }
        } else if (elseStatment != null) {
            StatmentUtil.execute(elseStatment, context);
        }
    }

    public Statment optimize() {
        if (thenStatment != null || elseStatment != null) {
            return this;
        }
        return null;
    }
}
