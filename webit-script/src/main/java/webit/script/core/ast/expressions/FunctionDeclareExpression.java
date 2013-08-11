package webit.script.core.ast.expressions;

import webit.script.Context;
import webit.script.core.ast.AbstractExpression;
import webit.script.core.ast.method.Function;
import webit.script.core.ast.method.FunctionMethodDeclare;
import webit.script.core.runtime.variant.VariantContext;

/**
 *
 * @author Zqq
 */
public final class FunctionDeclareExpression extends AbstractExpression {

    private final Function function;

    public FunctionDeclareExpression(Function function, int line, int column) {
        super(line, column);
        this.function = function;
    }

    @Override
    public Object execute(Context context, boolean needReturn) {

        VariantContext[] variantContexts;
        int[] overflowUpstairs = function.overflowUpstairs;
        if (overflowUpstairs != null && overflowUpstairs.length > 0) {
            int len_1 = overflowUpstairs[overflowUpstairs.length - 1] - overflowUpstairs[0];
            variantContexts = new VariantContext[len_1+1];
            for (int i = 0; i < overflowUpstairs.length; i++) {
                int j = overflowUpstairs[i];
                variantContexts[len_1 - j] = context.vars.getContext(j);
            }
        } else {
            variantContexts = null;
        }

        return new FunctionMethodDeclare(function,context.template, variantContexts);
    }
}
