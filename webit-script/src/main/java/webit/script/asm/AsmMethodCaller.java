
package webit.script.asm;

import webit.script.exceptions.ScriptRuntimeException;

/**
 *
 * @author Zqq
 */
public abstract class AsmMethodCaller {
    public abstract Object execute(Object[] args);
    
    protected static ScriptRuntimeException createException(String message){
        return new ScriptRuntimeException(message);
    }
}
