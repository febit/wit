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
    public static Object execute(Expression expression, Context context, boolean needReturn, Out out) {
        try {
            context.pushOut(out);
            Object result = expression.execute(context, needReturn);
            context.popOut();
            return result;
        } catch (Exception e) {
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

    public static Object execute(Expression expression, Context context, boolean needReturn) {
        try {
            return expression.execute(context, needReturn);
        } catch (Exception e) {
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

    public static Object execute(Expression expression, Context context) {
        try {
            return expression.execute(context, true);
        } catch (Exception e) {
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

    public static void execute(Statment statment, Context context) {
        try {
            statment.execute(context);
        } catch (Exception e) {
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
    public static void execute(Statment statment, Out out, Context context) {
        try {
            context.pushOut(out);
            statment.execute(context);
            context.popOut();
        } catch (Exception e) {
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

    public static ResetableValue getResetableValue(ResetableValueExpression expression, Context context) {
        try {
            return expression.getResetableValue(context);
        } catch (Exception e) {
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
        if (expression instanceof Optimizable) {
            try {
                expression = (Expression) ((Optimizable) expression).optimize();
            } catch (Exception e) {
                throw new ParserException("Exception(s) occur when do optimize", e, expression);
            }
        }
        return expression;
    }

    public static Statment optimize(Statment statment) {
        if (statment instanceof Optimizable) {
            try {
                statment = ((Optimizable) statment).optimize();
            } catch (Exception e) {
                throw new ParserException("Exception(s) occur when do optimize", e, statment);
            }
        }
        return statment;
    }
}
