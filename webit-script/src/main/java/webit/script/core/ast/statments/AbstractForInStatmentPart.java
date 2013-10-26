// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.core.ast.statments;

import webit.script.core.VariantManager;
import webit.script.core.ast.Position;
import webit.script.core.ast.Statment;
import webit.script.core.ast.StatmentList;
import webit.script.util.StatmentUtil;

/**
 *
 * @author Zqq
 */
public abstract class AbstractForInStatmentPart extends Position {

    IBlockStatment bodyStatment;
    Statment elseStatment;
    final VariantManager varmgr;
    int iterIndex;

    public AbstractForInStatmentPart(VariantManager varmgr, int line, int column) {
        super(line, column);
        this.varmgr = varmgr;
        varmgr.push();
        this.iterIndex = varmgr.assignVariant("for.iter", line, column);
    }

    public AbstractForInStatmentPart setStatmentList(StatmentList list) {
        this.bodyStatment = list.popIBlockStatment(varmgr.pop(), line, column);
        return this;
    }

    public AbstractForInStatmentPart setElseStatment(Statment elseStatment) {
        this.elseStatment = StatmentUtil.optimize(elseStatment);
        return this;
    }

    public Statment pop() {
        return pop(0);  //default label is zero;
    }

    public abstract Statment pop(int label);
}
