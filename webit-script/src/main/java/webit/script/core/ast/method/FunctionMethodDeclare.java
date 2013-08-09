package webit.script.core.ast.method;

import webit.script.Context;
import webit.script.core.runtime.variant.VariantContext;

/**
 *
 * @author Zqq
 */
public final class FunctionMethodDeclare implements MethodDeclare {

    private final Function function;
    private final VariantContext[] parentVarContexts;

    public FunctionMethodDeclare(Function function, VariantContext[] parentVarContexts) {
        this.function = function;
        this.parentVarContexts = parentVarContexts;
    }

    public Object execute(Context context, Object[] args) {
        Context functionContext = new Context(context, parentVarContexts);

        return function.execute(functionContext, args);
    }
}
