package webit.script.core.ast.statments;

import webit.script.core.ast.Expression;
import webit.script.core.ast.Statment;
import webit.script.filters.Filter;

/**
 *
 * @author Zqq
 */
public class PlaseHolderStatmentFactory {

    private final Filter filter;
    private final boolean withFilter;

    public PlaseHolderStatmentFactory(Filter filter) {
        this.filter = filter;
        this.withFilter = filter != null;
    }

    public Statment creatPlaseHolderStatment(Expression expr) {
        if (withFilter) {
            return new FilterPlaseHolderStatment(filter, expr);
        } else {
            return new PlaseHolderStatment(expr);
        }
    }
}
