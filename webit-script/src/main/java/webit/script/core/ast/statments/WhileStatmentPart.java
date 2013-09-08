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
public final class WhileStatmentPart extends StatmentPart {

    private Expression whileExpr;
    private BlockStatment bodyStatment;
    private boolean doWhileAtFirst;
    private String label;

    public WhileStatmentPart() {
    }

    public WhileStatmentPart setWhileExpr(Expression whileExpr) {
        this.whileExpr = whileExpr;
        return this;
    }

    public WhileStatmentPart setBodyStatment(BlockStatment bodyStatment) {
        this.bodyStatment = bodyStatment;
        return this;
    }

    public WhileStatmentPart setDoWhileAtFirst(boolean doWhileAtFirst) {
        this.doWhileAtFirst = doWhileAtFirst;
        return this;
    }

    public WhileStatmentPart setLabel(String label) {
        this.label = label;
        return this;
    }

    @Override
    public WhileStatmentPart setPosition(int line, int column) {
        super.setPosition(line, column);
        return this;
    }

    public Statment pop() {
        return StatmentUtil.optimize(new WhileStatment(whileExpr, bodyStatment, doWhileAtFirst, label, line, column));
    }
}
