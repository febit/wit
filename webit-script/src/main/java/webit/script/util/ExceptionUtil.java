// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script.util;

import webit.script.core.ast.Statement;
import webit.script.exceptions.ParseException;
import webit.script.exceptions.ScriptRuntimeException;

/**
 *
 * @author Zqq
 */
public class ExceptionUtil {

    public static ScriptRuntimeException castToScriptRuntimeException(final Exception e, final Statement statement) {
        return (e instanceof ScriptRuntimeException)
                ? ((ScriptRuntimeException) e).registStatement(statement)
                : new ScriptRuntimeException(e, statement);
    }

    public static ScriptRuntimeException castToScriptRuntimeException(final Exception e) {
        return (e instanceof ScriptRuntimeException)
                ? ((ScriptRuntimeException) e)
                : new ScriptRuntimeException(e);
    }

    public static ParseException castToParseException(final Exception e) {
        return (e instanceof ParseException)
                ? ((ParseException) e)
                : new ParseException(e);
    }
}
