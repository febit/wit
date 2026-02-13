// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.runtime.ast.stat;

import org.febit.wit.core.VariantManager;
import org.febit.wit.runtime.AstUtils;
import org.febit.wit.runtime.Position;
import org.febit.wit.runtime.ast.Expression;
import org.febit.wit.runtime.ast.Statement;
import org.febit.wit.runtime.ast.expr.FunctionDeclareExpr;

import java.util.Objects;

public class ForMapPart extends AbstractForInPart {

    protected final String keyVarName;
    protected final String valueVarName;
    protected int keyIndex;
    protected int valueIndex;

    public ForMapPart(String key, String value, Expression collectionExpr,
                      VariantManager varmgr, Position position) {
        super(varmgr, position);
        this.keyVarName = key;
        this.valueVarName = value;
        setCollectionExpr(collectionExpr);
    }

    public ForMapPart(String key, String value, FunctionDeclareExpr functionDeclareExpr,
                      VariantManager varmgr, Position position) {
        super(varmgr, position);
        this.keyVarName = key;
        this.valueVarName = value;
        this.functionDeclareExpr = functionDeclareExpr;
    }

    @Override
    public final AbstractForInPart setCollectionExpr(Expression collectionExpr) {
        super.setCollectionExpr(collectionExpr);
        this.keyIndex = vars.assignVar(keyVarName, position);
        this.valueIndex = vars.assignVar(valueVarName, position);
        return this;
    }

    @Override
    public Statement pop(int label) {
        Objects.requireNonNull(bodyBlock);
        Objects.requireNonNull(targetExpr);
        if (bodyBlock.hasLoopFlags()) {
            var loops = AstUtils.collectLoopFlagsForWhile(bodyBlock, elseBlock, label);
            return new ForMap(functionDeclareExpr, targetExpr, bodyBlock.varIndexer(),
                    iterIndex, keyIndex, valueIndex, bodyBlock.statements(),
                    loops, elseBlock, label, position);
        } else {
            return new ForMapNoLoops(functionDeclareExpr, targetExpr, bodyBlock.varIndexer(),
                    iterIndex, keyIndex, valueIndex, bodyBlock.statements(), elseBlock, position);
        }
    }
}
