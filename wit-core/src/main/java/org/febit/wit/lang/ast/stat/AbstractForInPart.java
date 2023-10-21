// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.lang.ast.stat;

import org.febit.wit.core.VariantManager;
import org.febit.wit.lang.Position;
import org.febit.wit.lang.ast.Expression;
import org.febit.wit.lang.ast.Statement;
import org.febit.wit.lang.ast.expr.FunctionDeclareExpr;
import org.febit.wit.lang.AstUtils;

/**
 * @author zqq90
 */
public abstract class AbstractForInPart {

    protected final VariantManager vars;
    protected final Position position;
    protected IBlock bodyStatement;
    protected Statement elseStatement;
    protected Expression collectionExpr;
    protected FunctionDeclareExpr functionDeclareExpr;
    protected int iterIndex;

    public AbstractForInPart(VariantManager vars, Position position) {
        this.position = position;
        this.vars = vars;
    }

    public AbstractForInPart setCollectionExpr(Expression collectionExpr) {
        this.collectionExpr = collectionExpr;
        vars.push();
        iterIndex = vars.assignVariant("for.iter", position);
        return this;
    }

    public AbstractForInPart setBodies(IBlock thenStatement, Statement elseStatement) {
        this.bodyStatement = thenStatement;
        this.elseStatement = AstUtils.optimize(elseStatement);
        return this;
    }

    public abstract Statement pop(int label);
}
