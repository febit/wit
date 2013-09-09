// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.core.ast.statments;

import webit.script.core.ast.Expression;
import webit.script.core.ast.Statment;
import webit.script.filters.Filter;

/**
 *
 * @author Zqq
 */
public class PlaceHolderStatmentFactory {

    private final Filter filter;

    public PlaceHolderStatmentFactory(Filter filter) {
        this.filter = filter;
    }

    public Statment creatPlaceHolderStatment(final Expression expr) {
        return filter != null
                ? new FilterPlaceHolderStatment(filter, expr)
                : new PlaceHolderStatment(expr);
    }
}
