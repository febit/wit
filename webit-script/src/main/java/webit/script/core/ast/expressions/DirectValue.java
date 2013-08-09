package webit.script.core.ast.expressions;

import webit.script.Context;
import webit.script.core.ast.AbstractExpression;

/**
 *
 * @author Zqq
 */
public final class DirectValue extends AbstractExpression {

    public final Object value;

    public DirectValue(Object value, int line, int column) {
        super(line, column);
        this.value = value;
    }

    @Override
    public Object execute(Context context, boolean needReturn) {
        return value;
    }
}
