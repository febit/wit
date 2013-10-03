// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.core.ast.statments;

import webit.script.core.ast.Expression;
import webit.script.core.ast.Position;
import webit.script.core.ast.Statment;
import webit.script.core.ast.loop.LoopInfo;
import webit.script.util.StatmentUtil;

/**
 *
 * @author Zqq
 */
public final class WhileStatmentPart extends Position {

    private Expression whileExpr;
    private IBlockStatment bodyStatment;
    private boolean doWhileAtFirst;
    private String label;

    public WhileStatmentPart() {
    }

    public WhileStatmentPart setWhileExpr(Expression whileExpr) {
        this.whileExpr = whileExpr;
        return this;
    }

    public WhileStatmentPart setBodyStatment(IBlockStatment bodyStatment) {
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
        if (bodyStatment.hasLoops()) {
            LoopInfo[] loopInfos = StatmentUtil.collectPossibleLoopsInfoForWhileStatments(bodyStatment, null, label);
            return doWhileAtFirst
                    ? new WhileStatment(whileExpr, bodyStatment.getVarMap(), bodyStatment.getStatments(), loopInfos, label, line, column)
                    : new DoWhileStatment(whileExpr, bodyStatment.getVarMap(), bodyStatment.getStatments(), loopInfos, label, line, column);
        } else {
            return doWhileAtFirst
                    ? new WhileStatmentNoLoops(whileExpr, bodyStatment.getVarMap(), bodyStatment.getStatments(), line, column)
                    : new DoWhileStatmentNoLoops(whileExpr, bodyStatment.getVarMap(), bodyStatment.getStatments(), line, column);
        }
    }
}
