// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script.util;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import webit.script.Context;
import webit.script.core.ast.Expression;
import webit.script.core.ast.Optimizable;
import webit.script.core.ast.ResetableValueExpression;
import webit.script.core.ast.Statement;
import webit.script.core.ast.loop.LoopCtrl;
import webit.script.core.ast.loop.LoopInfo;
import webit.script.core.ast.loop.Loopable;
import webit.script.exceptions.ParseException;
import webit.script.io.Out;

/**
 *
 * @author Zqq
 */
public class StatementUtil {

    @SuppressWarnings("deprecation")
    public static Object execute(final Expression expression, final Context context, final Out out) {
        try {
            context.pushOut(out);
            Object result = expression.execute(context);
            context.popOut();
            return result;
        } catch (Throwable e) {
            throw ExceptionUtil.castToScriptRuntimeException(e, expression);
        }
    }

    @SuppressWarnings("deprecation")
    public static void execute(final Statement statement, final Context context, final Out out) {
        try {
            context.pushOut(out);
            statement.execute(context);
            context.popOut();
            return;
        } catch (Throwable e) {
            throw ExceptionUtil.castToScriptRuntimeException(e, statement);
        }
    }

    public static Object execute(final Expression expression, final Context context) {
        try {
            return expression.execute(context);
        } catch (Throwable e) {
            throw ExceptionUtil.castToScriptRuntimeException(e, expression);
        }
    }

    public static Object[] execute(final Expression[] expressions, final Context context) {
        int i = 0;
        final int len;
        final Object[] results = new Object[len = expressions.length];
        try {
            for (i = 0; i < len; i++) {
                results[i] = expressions[i].execute(context);
            }
            return results;
        } catch (Throwable e) {
            throw ExceptionUtil.castToScriptRuntimeException(e, expressions[i]);
        }
    }

    public static Object executeSetValue(final ResetableValueExpression expression, final Context context, final Object value) {
        try {
            return expression.setValue(context, value);
        } catch (Throwable e) {
            throw ExceptionUtil.castToScriptRuntimeException(e, expression);
        }
    }

    public static void execute(final Statement statement, final Context context) {
        try {
            statement.execute(context);
            return;
        } catch (Throwable e) {
            throw ExceptionUtil.castToScriptRuntimeException(e, statement);
        }
    }

    public static void execute(final Statement[] statements, final Context context) {
        int i = 0;
        int len = statements.length;
        try {
            while (i < len) {
                statements[i].execute(context);
                i++;
            }
            return;
        } catch (Throwable e) {
            throw ExceptionUtil.castToScriptRuntimeException(e, statements[i]);
        }
    }

    public static void executeInverted(final Statement[] statements, final Context context) {
        int i = statements.length;
        try {
            while (i != 0) {
                --i;
                statements[i].execute(context);
            }
            return;
        } catch (Throwable e) {
            throw ExceptionUtil.castToScriptRuntimeException(e, statements[i]);
        }
    }

    public static void executeInvertedAndCheckLoops(final Statement[] statements, final Context context) {
        int i = statements.length; //assert >0;
        final LoopCtrl ctrl = context.loopCtrl;
        try {
            do {
                --i;
                statements[i].execute(context);
            } while (i != 0 && ctrl.getLoopType() == LoopInfo.NO_LOOP);
            return;
        } catch (Throwable e) {
            throw ExceptionUtil.castToScriptRuntimeException(e, statements[i]);
        }
    }

    public static Expression optimize(Expression expression) {
        try {
            return expression != null && expression instanceof Optimizable
                    ? (Expression) ((Optimizable) expression).optimize()
                    : expression;
        } catch (Throwable e) {
            throw new ParseException("Exception occur when do optimization", e, expression);
        }
    }

    public static Statement optimize(Statement statement) {
        try {
            return statement != null && statement instanceof Optimizable
                    ? ((Optimizable) statement).optimize()
                    : statement;
        } catch (Throwable e) {
            throw new ParseException("Exception occur when do optimization", e, statement);
        }
    }

    public static List<LoopInfo> collectPossibleLoopsInfo(Statement statement) {
        return (statement != null && statement instanceof Loopable)
                ? ((Loopable) statement).collectPossibleLoopsInfo()
                : null;
    }

    public static List<LoopInfo> collectPossibleLoopsInfo(Statement[] statements) {
        int i;
        if (statements != null && (i = statements.length) > 0) {
            LinkedList<LoopInfo> loopInfos = new LinkedList<LoopInfo>();
            List<LoopInfo> list;
            do {
                --i;
                if ((list = collectPossibleLoopsInfo(statements[i])) != null) {
                    loopInfos.addAll(list);
                }
            } while (i != 0);
            return loopInfos.size() > 0 ? loopInfos : null;
        }
        return null;
    }

    public static LoopInfo[] collectPossibleLoopsInfoForWhile(Statement bodyStatement, Statement elseStatement, int label) {

        List<LoopInfo> list;
        LoopInfo loopInfo;
        if ((list = StatementUtil.collectPossibleLoopsInfo(bodyStatement)) != null) {
            for (Iterator<LoopInfo> it = list.iterator(); it.hasNext();) {
                if ((loopInfo = it.next()).matchLabel(label)
                        && (loopInfo.type == LoopInfo.BREAK
                        || loopInfo.type == LoopInfo.CONTINUE)) {
                    it.remove();
                }
            }
            list = list.isEmpty() ? null : list;
        }

        if (elseStatement != null) {
            List<LoopInfo> list2 = StatementUtil.collectPossibleLoopsInfo(elseStatement);
            if (list == null) {
                list = list2;
            } else if (list2 != null) {
                list.addAll(list2);
            }
        }
        return list != null && list.size() > 0
                ? list.toArray(new LoopInfo[list.size()])
                : null;
    }
}
