// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.util;

import webit.script.core.ast.Statment;
import webit.script.exceptions.ScriptRuntimeException;

/**
 *
 * @author Zqq
 */
public class ExceptionUtil {

    public static ScriptRuntimeException castToScriptRuntimeException(final Throwable e, final Statment statment) {
        return (e instanceof ScriptRuntimeException)
                ? ((ScriptRuntimeException) e).registStatment(statment)
                : new ScriptRuntimeException(e, statment);
    }

    public static ScriptRuntimeException castToScriptRuntimeException(final Throwable e) {
        return (e instanceof ScriptRuntimeException)
                ? ((ScriptRuntimeException) e)
                : new ScriptRuntimeException(e);
    }
}
