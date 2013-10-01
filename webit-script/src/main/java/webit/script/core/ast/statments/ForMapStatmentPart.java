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
public final class ForMapStatmentPart extends Position {

    private int keyIndex;
    private int valueIndex;
    private int iterIndex;
    private Expression mapExpr;
    private BlockStatment bodyStatment;
    private Statment elseStatment;
    private String label;

    public ForMapStatmentPart(int keyIndex, int valueIndex, int iterIndex, Expression mapExpr, int line, int column) {
        super(line, column);
        this.keyIndex = keyIndex;
        this.valueIndex = valueIndex;
        this.iterIndex = iterIndex;
        this.mapExpr = mapExpr;
    }

    public ForMapStatmentPart setLabel(String label) {
        this.label = label;
        return this;
    }

    public ForMapStatmentPart setBodyStatment(BlockStatment bodyStatment) {
        this.bodyStatment = bodyStatment;
        return this;
    }

    public ForMapStatmentPart setElseStatment(Statment elseStatment) {
        this.elseStatment = StatmentUtil.optimize(elseStatment);
        return this;
    }

    public Statment pop() {
        if (bodyStatment.hasLoops()) {
            return new ForMapStatment(iterIndex, keyIndex, valueIndex, mapExpr, bodyStatment.varMap, bodyStatment.statments,
                    StatmentUtil.collectPossibleLoopsInfoForWhileStatments(bodyStatment, elseStatment, label),
                    elseStatment, label, line, column);
        } else {
            return new ForMapStatmentNoLoops(iterIndex, keyIndex, valueIndex, mapExpr, bodyStatment.varMap, bodyStatment.statments, elseStatment, line, column);
        }
    }
}
