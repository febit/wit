// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script.core.ast.statements;

import webit.script.core.ast.Expression;
import webit.script.core.ast.Statement;
import webit.script.filters.Filter;

/**
 *
 * @author Zqq
 */
public class PlaceHolderFactory {

    private final Filter filter;

    public PlaceHolderFactory(Filter filter) {
        this.filter = filter;
    }

    public Statement creatPlaceHolder(final Expression expr) {
        return filter != null
                ? new FilterPlaceHolder(filter, expr)
                : new PlaceHolder(expr);
    }
}
