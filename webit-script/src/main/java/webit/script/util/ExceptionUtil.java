// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script.util;

import webit.script.core.ast.Statement;
import webit.script.exceptions.ScriptRuntimeException;

/**
 *
 * @author Zqq
 */
public class ExceptionUtil {

    public static ScriptRuntimeException castToScriptRuntimeException(final Exception e, final Statement statement) {
        if (e instanceof ScriptRuntimeException) {
            ScriptRuntimeException exception = (ScriptRuntimeException) e;
            exception.registStatement(statement);
            return exception;
        } else {
            return new ScriptRuntimeException(e, statement);
        }
    }
}
