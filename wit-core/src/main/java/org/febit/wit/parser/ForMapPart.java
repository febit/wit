// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.parser;

import org.febit.wit.runtime.ast.AstUtils;
import org.febit.wit.runtime.ast.Expression;
import org.febit.wit.runtime.ast.Position;
import org.febit.wit.runtime.ast.Statement;
import org.febit.wit.runtime.ast.expr.FunctionDeclareExpr;
import org.febit.wit.runtime.ast.stat.ForMap;
import org.febit.wit.runtime.ast.stat.ForMapNoLoops;

import java.util.Objects;

public class ForMapPart extends AbstractForInPart {

    private final String keyVarName;
    private final String valueVarName;
    private int keyIndex;
    private int valueIndex;

    public ForMapPart(String key, String value, Expression collectionExpr,
                      VariantManager variants, Position position) {
        super(variants, position);
        this.keyVarName = key;
        this.valueVarName = value;
        setCollectionExpr(collectionExpr);
    }

    public ForMapPart(String key, String value, FunctionDeclareExpr functionDeclareExpr,
                      VariantManager variants, Position position) {
        super(variants, position);
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
