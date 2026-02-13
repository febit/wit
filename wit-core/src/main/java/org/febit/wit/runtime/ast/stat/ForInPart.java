// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.runtime.ast.stat;

import org.febit.wit.core.VariantManager;
import org.febit.wit.runtime.AstUtils;
import org.febit.wit.runtime.Position;
import org.febit.wit.runtime.ast.Expression;
import org.febit.wit.runtime.ast.Statement;
import org.febit.wit.runtime.ast.expr.FunctionDeclareExpr;

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
        itemIndex = vars.assignVar(itemVarName, position);
        return this;
    }

    @Override
    public Statement pop(int label) {
        Objects.requireNonNull(bodyBlock);
        Objects.requireNonNull(targetExpr);
        if (bodyBlock.hasLoopFlags()) {
            var loops = AstUtils.collectLoopFlagsForWhile(bodyBlock, elseBlock, label);
            return new ForIn(functionDeclareExpr, targetExpr, bodyBlock.varIndexer(),
                    iterIndex, itemIndex, bodyBlock.statements(),
                    loops, elseBlock, label, position
            );
        } else {
            return new ForInNoLoops(functionDeclareExpr, targetExpr, bodyBlock.varIndexer(),
                    iterIndex, itemIndex, bodyBlock.statements(), elseBlock, position);
        }
    }
}
