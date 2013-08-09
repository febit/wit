package webit.script.core.ast;

import webit.script.Context;

/**
 *
 * @author Zqq
 */
public interface ResetableValueExpression extends Expression{

    ResetableValue getResetableValue(Context context);
}
