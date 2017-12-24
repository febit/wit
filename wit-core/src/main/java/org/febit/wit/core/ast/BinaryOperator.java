// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.core.ast;

/**
 *
 * @author zqq90
 */
public abstract class BinaryOperator extends Expression {

    protected final Expression leftExpr;
    protected final Expression rightExpr;

    protected BinaryOperator(Expression leftExpr, Expression rightExpr, int line, int column) {
        super(line, column);
        this.leftExpr = leftExpr;
        this.rightExpr = rightExpr;
    }
}
