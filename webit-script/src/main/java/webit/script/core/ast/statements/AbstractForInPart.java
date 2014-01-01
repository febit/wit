// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script.core.ast.statements;

import webit.script.core.VariantManager;
import webit.script.core.ast.Position;
import webit.script.core.ast.Statement;
import webit.script.core.ast.StatementList;
import webit.script.exceptions.ParseException;
import webit.script.util.StatementUtil;

/**
 *
 * @author Zqq
 */
public abstract class AbstractForInPart extends Position {

    IBlock bodyStatement;
    Statement elseStatement;
    final VariantManager varmgr;
    public AbstractForInPart(VariantManager varmgr, int line, int column) {
        super(line, column);
        this.varmgr = varmgr;
        varmgr.push();
        if (varmgr.assignVariant("for.iter", line, column) != 0) {
            throw new ParseException("assignVariant failed!");
        }
    }

    public AbstractForInPart setStatementList(StatementList list) {
        this.bodyStatement = list.popIBlock(varmgr.pop(), line, column);
        return this;
    }

    public AbstractForInPart setElse(Statement elseStatement) {
        this.elseStatement = StatementUtil.optimize(elseStatement);
        return this;
    }

    public Statement pop() {
        return pop(0);  //default label is zero;
    }

    public abstract Statement pop(int label);
}
