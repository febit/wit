// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script.core.ast;

/**
 *
 * @author Zqq
 */
public abstract class BinaryOperator extends AbstractExpression {

    public final Expression leftExpr;
    public final Expression rightExpr;

    public BinaryOperator(Expression leftExp, Expression rightExp, int line, int column) {
        super(line, column);
        this.leftExpr = leftExp;
        this.rightExpr = rightExp;
    }
}
