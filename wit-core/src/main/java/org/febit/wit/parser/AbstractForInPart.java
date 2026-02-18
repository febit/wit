// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.parser;

import org.febit.wit.runtime.ast.AstUtils;
import org.febit.wit.runtime.ast.Expression;
import org.febit.wit.runtime.ast.IBlock;
import org.febit.wit.runtime.ast.Position;
import org.febit.wit.runtime.ast.Statement;
import org.febit.wit.runtime.ast.expr.FunctionDeclareExpr;
import org.jspecify.annotations.Nullable;

public abstract class AbstractForInPart {

    protected final VariantManager vars;
    protected final Position position;

    @Nullable
    protected IBlock bodyBlock;
    @Nullable
    protected Statement elseBlock;
    @Nullable
    protected Expression targetExpr;
    @Nullable
    protected FunctionDeclareExpr functionDeclareExpr;

    protected int iterIndex;

    protected AbstractForInPart(VariantManager vars, Position position) {
        this.position = position;
        this.vars = vars;
    }

    public AbstractForInPart setCollectionExpr(Expression collectionExpr) {
        this.targetExpr = collectionExpr;
        vars.shiftFrame();
        iterIndex = vars.assignVar("for.iter", position);
        return this;
    }

    public AbstractForInPart setBodies(IBlock thenStatement, Statement elseStatement) {
        this.bodyBlock = thenStatement;
        this.elseBlock = AstUtils.optimize(elseStatement);
        return this;
    }

    public abstract Statement pop(int label);
}
