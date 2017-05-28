// Copyright (c) 2013-2016, febit.org. All Rights Reserved.
package org.febit.wit.core.ast;

import java.util.LinkedList;

/**
 *
 * @author zqq90
 */
public final class ExpressionList {

    private final LinkedList<Expression> exprList;

    public ExpressionList() {
        this.exprList = new LinkedList<>();
    }

    public ExpressionList add(Expression expr) {
        exprList.add(expr);
        return this;
    }

    public ExpressionList addFirst(Expression expr) {
        exprList.addFirst(expr);
        return this;
    }

    public Expression[] toArray() {
        return exprList.toArray(new Expression[exprList.size()]);
    }
}
