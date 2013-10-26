// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.core.ast.statments;

import webit.script.core.ast.Expression;
import webit.script.core.ast.Position;
import webit.script.core.ast.Statment;
import webit.script.util.StatmentUtil;

/**
 *
 * @author Zqq
 */
public final class IfStatmentPart extends Position {

    private Expression ifExpr;
    private Statment thenStatment;

    public IfStatmentPart(Expression ifExpr, Statment thenStatment, int line, int column) {
        super(line, column);
        this.ifExpr = ifExpr;
        this.thenStatment = StatmentUtil.optimize(thenStatment);
    }

    public Statment pop() {
        return pop(null);
    }

    public Statment pop(Statment elseStatment) {
        elseStatment = StatmentUtil.optimize(elseStatment);
        if (this.thenStatment != null) {
            if (elseStatment != null) {
                return new IfElseStatment(ifExpr, thenStatment, elseStatment, line, column);
            } else {
                return new IfStatment(ifExpr, thenStatment, line, column);
            }
        } else if (elseStatment != null) {
            return new IfNotStatment(ifExpr, elseStatment, line, column);
        } else {
            return NoneStatment.getInstance();
        }
    }
}
