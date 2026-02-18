// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.parser;

import org.febit.wit.runtime.ast.AstUtils;
import org.febit.wit.runtime.ast.Expression;
import org.febit.wit.runtime.ast.Position;
import org.febit.wit.runtime.ast.Statement;
import org.febit.wit.runtime.ast.expr.FunctionDeclareExpr;
import org.febit.wit.runtime.ast.stat.ForIn;
import org.febit.wit.runtime.ast.stat.ForInNoLoops;

import java.util.Objects;

public class ForInPart extends AbstractForInPart {

    private final String itemVarName;
    private int itemIndex;

    public ForInPart(String item, Expression collectionExpr, VariantManager variants, Position position) {
        super(variants, position);
        this.itemVarName = item;
        setCollectionExpr(collectionExpr);
    }

    public ForInPart(String item, FunctionDeclareExpr functionDeclareExpr, VariantManager variants, Position position) {
        super(variants, position);
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
