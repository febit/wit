// Copyright (c) 2013-2015, Webit Team. All Rights Reserved.
package webit.script.core.ast;

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
