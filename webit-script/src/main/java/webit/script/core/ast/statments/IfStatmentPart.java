// Copyright (c) 2013, Webit Team. All Rights Reserved.

package webit.script.core.ast.statments;

import webit.script.core.ast.Expression;
import webit.script.core.ast.Statment;
import webit.script.core.ast.StatmentPart;
import webit.script.util.StatmentUtil;

/**
 *
 * @author Zqq
 */
public final class IfStatmentPart extends StatmentPart {

    private Expression ifExpr;
    private Statment thenStatment;
    private Statment elseStatment;

    public IfStatmentPart(Expression ifExpr, int line, int column) {
        super(line, column);
        this.ifExpr = ifExpr;
    }

    public IfStatmentPart setThenStatment(Statment thenStatment) {
        
        thenStatment = StatmentUtil.optimize(thenStatment);
        this.thenStatment = thenStatment;
        return this;
    }

    public IfStatmentPart setElseStatment(Statment elseStatment) {
        
        elseStatment = StatmentUtil.optimize(elseStatment);
        this.elseStatment = elseStatment;
        return this;
    }

    public Statment pop() {
        return StatmentUtil.optimize(new IfStatment(ifExpr, thenStatment, elseStatment, line, column));
    }
}
