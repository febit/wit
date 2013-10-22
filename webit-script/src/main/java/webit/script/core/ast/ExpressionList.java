// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.core.ast;

import java.util.LinkedList;

/**
 *
 * @author Zqq
 */
public class ExpressionList {

    private final LinkedList<Expression> expressionList;

    public ExpressionList() {
        this.expressionList = new LinkedList<Expression>();
    }

    public ExpressionList add(Expression expr) {
        expressionList.add(expr);
        return this;
    }

    public ExpressionList addFirst(Expression expr) {
        expressionList.addFirst(expr);
        return this;
    }

    public Expression[] toArray() {
        return expressionList.toArray(new Expression[expressionList.size()]);
    }
}
