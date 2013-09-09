// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.util;

import webit.script.Context;
import webit.script.core.ast.Expression;
import webit.script.core.ast.Optimizable;
import webit.script.core.ast.ResetableValue;
import webit.script.core.ast.ResetableValueExpression;
import webit.script.core.ast.Statment;
import webit.script.exceptions.ParserException;
import webit.script.exceptions.ScriptRuntimeException;
import webit.script.io.Out;

/**
 *
 * @author Zqq
 */
public class StatmentUtil {

    @SuppressWarnings("deprecation")
    public static Object execute(final Expression expression, final Context context, final boolean needReturn, final Out out) {
        try {
            context.pushOut(out);
            Object result = expression.execute(context, needReturn);
            context.popOut();
            return result;
        } catch (Throwable e) {
            ScriptRuntimeException scriptRuntimeException;
            if (e instanceof ScriptRuntimeException) {
                scriptRuntimeException = (ScriptRuntimeException) e;
                scriptRuntimeException.registStatment(expression);
            } else {
                scriptRuntimeException = new ScriptRuntimeException(e, expression);
            }
            throw scriptRuntimeException;
        }
    }

    public static Object execute(final Expression expression, final Context context, final boolean needReturn) {
        try {
            return expression.execute(context, needReturn);
        } catch (Throwable e) {
            ScriptRuntimeException scriptRuntimeException;
            if (e instanceof ScriptRuntimeException) {
                scriptRuntimeException = (ScriptRuntimeException) e;
                scriptRuntimeException.registStatment(expression);
            } else {
                scriptRuntimeException = new ScriptRuntimeException(e, expression);
            }
            throw scriptRuntimeException;
        }
    }

    public static Object execute(final Expression expression, final Context context) {
        try {
            return expression.execute(context, true);
        } catch (Throwable e) {
            ScriptRuntimeException scriptRuntimeException;
            if (e instanceof ScriptRuntimeException) {
                scriptRuntimeException = (ScriptRuntimeException) e;
                scriptRuntimeException.registStatment(expression);
            } else {
                scriptRuntimeException = new ScriptRuntimeException(e, expression);
            }
            throw scriptRuntimeException;
        }
    }

    public static void execute(final Statment statment, final Context context) {
        try {
            statment.execute(context);
        } catch (Throwable e) {
            ScriptRuntimeException scriptRuntimeException;
            if (e instanceof ScriptRuntimeException) {
                scriptRuntimeException = (ScriptRuntimeException) e;
                scriptRuntimeException.registStatment(statment);
            } else {
                scriptRuntimeException = new ScriptRuntimeException(e, statment);
            }
            throw scriptRuntimeException;
        }
    }

    @SuppressWarnings("deprecation")
    public static void execute(final Statment statment, final Out out, final Context context) {
        try {
            context.pushOut(out);
            statment.execute(context);
            context.popOut();
        } catch (Throwable e) {
            ScriptRuntimeException scriptRuntimeException;
            if (e instanceof ScriptRuntimeException) {
                scriptRuntimeException = (ScriptRuntimeException) e;
                scriptRuntimeException.registStatment(statment);
            } else {
                scriptRuntimeException = new ScriptRuntimeException(e, statment);
            }
            throw scriptRuntimeException;
        }
    }

    public static ResetableValue getResetableValue(final ResetableValueExpression expression, final Context context) {
        try {
            return expression.getResetableValue(context);
        } catch (Throwable e) {
            ScriptRuntimeException scriptRuntimeException;
            if (e instanceof ScriptRuntimeException) {
                scriptRuntimeException = (ScriptRuntimeException) e;
                scriptRuntimeException.registStatment(expression);
            } else {
                scriptRuntimeException = new ScriptRuntimeException(e, expression);
            }
            throw scriptRuntimeException;
        }
    }

    public static Expression optimize(Expression expression) {
        try {
            return expression instanceof Optimizable
                    ? (Expression) ((Optimizable) expression).optimize()
                    : expression;
        } catch (Throwable e) {
            throw new ParserException("Exception(s) occur when do optimize", e, expression);
        }
    }

    public static Statment optimize(Statment statment) {
        try {
            return statment instanceof Optimizable
                    ? ((Optimizable) statment).optimize()
                    : statment;
        } catch (Throwable e) {
            throw new ParserException("Exception(s) occur when do optimize", e, statment);
        }
    }
}
