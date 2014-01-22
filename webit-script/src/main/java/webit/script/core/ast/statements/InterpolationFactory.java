// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script.core.ast.statements;

import webit.script.core.ast.Expression;
import webit.script.core.ast.Statement;
import webit.script.filters.Filter;

/**
 *
 * @author Zqq
 */
public class InterpolationFactory {

    private final Filter filter;

    public InterpolationFactory(Filter filter) {
        this.filter = filter;
    }

    public Statement createInterpolation(final Expression expr) {
        return filter != null
                ? new FilterInterpolation(filter, expr)
                : new Interpolation(expr);
    }
}
