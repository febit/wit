// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script.core.ast.statements;

import webit.script.core.VariantManager;
import webit.script.core.ast.Expression;
import webit.script.core.ast.Statement;
import webit.script.core.ast.expressions.FunctionDeclare;
import webit.script.util.StatementUtil;

/**
 *
 * @author Zqq
 */
public abstract class AbstractForInPart {

    protected final int line;
    protected final int column;
    protected IBlock bodyStatement;
    protected Statement elseStatement;
    protected final VariantManager varmgr;
    protected Expression collectionExpr;
    protected FunctionDeclare functionDeclareExpr;
    protected int iterIndex;

    public AbstractForInPart(VariantManager varmgr, int line, int column) {
        this.line = line;
        this.column = column;
        this.varmgr = varmgr;
    }

    public AbstractForInPart setCollectionExpr(Expression collectionExpr) {
        this.collectionExpr = collectionExpr;
        varmgr.push();
        iterIndex = varmgr.assignVariant("for.iter", line, column);
        return this;
    }

    public AbstractForInPart setBodys(IBlock thenStatement, Statement elseStatement) {
        this.bodyStatement = thenStatement;
        this.elseStatement = StatementUtil.optimize(elseStatement);
        return this;
    }

    public abstract Statement pop(int label);
}
