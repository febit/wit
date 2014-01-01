// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script.core.ast.statements;

import webit.script.Context;
import webit.script.core.ast.AbstractStatement;
import webit.script.core.ast.Expression;
import webit.script.filters.Filter;
import webit.script.util.StatementUtil;

/**
 *
 * @author Zqq
 */
public final class FilterPlaceHolder extends AbstractStatement {

    private final Filter filter;
    private final Expression expr;

    public FilterPlaceHolder(Filter filter, Expression expr) {
        super(expr.getLine(), expr.getColumn());
        this.filter = filter;
        this.expr = expr;
    }

    public FilterPlaceHolder(Filter filter, Expression expr, int line, int column) {
        super(line, column);
        this.filter = filter;
        this.expr = expr;
    }

    public Object execute(final Context context) {
        context.out(filter.process(StatementUtil.execute(expr, context)));
        return null;
    }
}
