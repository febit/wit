package webit.script.core.ast.method;

import webit.script.Context;
import webit.script.asm.AsmMethodCaller;

/**
 *
 * @author Zqq
 */
public class AsmNativeMethodDeclare implements MethodDeclare {

    private final AsmMethodCaller caller;

    public AsmNativeMethodDeclare(AsmMethodCaller caller) {
        this.caller = caller;
    }

    public Object execute(Context context, Object[] args) {
        return caller.execute(args);
    }
}
