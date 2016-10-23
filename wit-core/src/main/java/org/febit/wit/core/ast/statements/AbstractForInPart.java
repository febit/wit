// Copyright (c) 2013-2016, febit.org. All Rights Reserved.
package org.febit.wit.core.ast.statements;

import org.febit.wit.core.VariantManager;
import org.febit.wit.core.ast.Expression;
import org.febit.wit.core.ast.Statement;
import org.febit.wit.core.ast.expressions.FunctionDeclare;
import org.febit.wit.util.StatementUtil;

/**
 *
 * @author zqq90
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
