// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script.core.ast;

/**
 *
 * @author Zqq
 */
public abstract class BinaryOperator extends Expression {

    public final Expression leftExpr;
    public final Expression rightExpr;

    public BinaryOperator(Expression leftExpr, Expression rightExpr, int line, int column) {
        super(line, column);
        this.leftExpr = leftExpr;
        this.rightExpr = rightExpr;
    }
}
