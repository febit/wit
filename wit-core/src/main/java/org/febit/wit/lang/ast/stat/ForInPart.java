// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.lang.ast.stat;

import org.febit.wit.core.VariantManager;
import org.febit.wit.lang.AstUtils;
import org.febit.wit.lang.Position;
import org.febit.wit.lang.ast.Expression;
import org.febit.wit.lang.ast.Statement;
import org.febit.wit.lang.ast.expr.FunctionDeclareExpr;

import java.util.Objects;

public class ForInPart extends AbstractForInPart {

    protected final String itemVarName;
    protected int itemIndex;

    public ForInPart(String item, Expression collectionExpr, VariantManager varmgr, Position position) {
        super(varmgr, position);
        this.itemVarName = item;
        setCollectionExpr(collectionExpr);
    }

    public ForInPart(String item, FunctionDeclareExpr functionDeclareExpr, VariantManager varmgr, Position position) {
        super(varmgr, position);
        this.itemVarName = item;
        this.functionDeclareExpr = functionDeclareExpr;
    }

    @Override
    public final AbstractForInPart setCollectionExpr(Expression collectionExpr) {
        super.setCollectionExpr(collectionExpr);
        itemIndex = vars.assignVariant(itemVarName, position);
        return this;
    }

    @Override
    public Statement pop(int label) {
        Objects.requireNonNull(bodyBlock);
        Objects.requireNonNull(targetExpr);
        if (bodyBlock.hasLoops()) {
            return new ForIn(functionDeclareExpr, targetExpr, bodyBlock.varIndexer(),
                    iterIndex, itemIndex, bodyBlock.statements(),
                    AstUtils.collectPossibleLoopsForWhile(bodyBlock, elseBlock, label),
                    elseBlock, label, position
            );
        } else {
            return new ForInNoLoops(functionDeclareExpr, targetExpr, bodyBlock.varIndexer(),
                    iterIndex, itemIndex, bodyBlock.statements(), elseBlock, position);
        }
    }
}
