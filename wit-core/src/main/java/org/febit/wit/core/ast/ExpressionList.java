// Copyright (c) 2013-2016, febit.org. All Rights Reserved.
package org.febit.wit.core.ast;

import java.util.LinkedList;

/**
 *
 * @author zqq90
 */
public final class ExpressionList {

    private final LinkedList<Expression> expressionList;

    public ExpressionList() {
        this.expressionList = new LinkedList<>();
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
