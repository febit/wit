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
    private final boolean withFilter;

    public PlaceHolderStatmentFactory(Filter filter) {
        this.filter = filter;
        this.withFilter = filter != null;
    }

    public Statment creatPlaceHolderStatment(Expression expr) {
        if (withFilter) {
            return new FilterPlaceHolderStatment(filter, expr);
        } else {
            return new PlaceHolderStatment(expr);
        }
    }
}
