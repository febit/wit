// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.util;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import webit.script.Context;
import webit.script.core.ast.Expression;
import webit.script.core.ast.Optimizable;
import webit.script.core.ast.ResetableValue;
import webit.script.core.ast.ResetableValueExpression;
import webit.script.core.ast.Statment;
import webit.script.core.ast.loop.LoopCtrl;
import webit.script.core.ast.loop.LoopInfo;
import webit.script.core.ast.loop.LoopType;
import webit.script.core.ast.loop.Loopable;
import webit.script.exceptions.ParseException;
import webit.script.io.Out;

/**
 *
 * @author Zqq
 */
public class StatmentUtil {

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
    public static void execute(final Statment statment, final Context context, final Out out) {
        try {
            context.pushOut(out);
            statment.execute(context);
            context.popOut();
        } catch (Throwable e) {
            throw ExceptionUtil.castToScriptRuntimeException(e, statment);
        }
    }

    public static Object execute(final Expression expression, final Context context) {
        try {
            return expression.execute(context);
        } catch (Throwable e) {
            throw ExceptionUtil.castToScriptRuntimeException(e, expression);
        }
    }

    public static void executeSetValue(final ResetableValueExpression expression, final Context context, final Object value) {
        try {
            expression.setValue(context, value);
        } catch (Throwable e) {
            throw ExceptionUtil.castToScriptRuntimeException(e, expression);
        }
    }

    public static void execute(final Statment statment, final Context context) {
        try {
            statment.execute(context);
        } catch (Throwable e) {
            throw ExceptionUtil.castToScriptRuntimeException(e, statment);
        }
    }

    public static void execute(final Statment[] statments, final Context context) {
        int i = 0;
        final int len = statments.length;
        try {
            while (i < len) {
                statments[i++].execute(context);
            }
        } catch (Throwable e) {
            throw ExceptionUtil.castToScriptRuntimeException(e, statments[i - 1]);
        }
    }

    public static void executeAndCheckLoops(final Statment[] statments, final Context context) {
        int i = 0;
        final LoopCtrl ctrl = context.loopCtrl;
        final int len = statments.length;
        //assert len >0;
        try {
            do {
                statments[i++].execute(context);
            } while (i < len && ctrl.goon());
        } catch (Throwable e) {
            throw ExceptionUtil.castToScriptRuntimeException(e, statments[i]);
        }
    }

    public static ResetableValue getResetableValue(final ResetableValueExpression expression, final Context context) {
        try {
            return expression.getResetableValue(context);
        } catch (Throwable e) {
            throw ExceptionUtil.castToScriptRuntimeException(e, expression);
        }
    }

    public static Expression optimize(Expression expression) {
        try {
            return expression instanceof Optimizable
                    ? (Expression) ((Optimizable) expression).optimize()
                    : expression;
        } catch (Throwable e) {
            throw new ParseException("Exception occur when do optimization", e, expression);
        }
    }

    public static Statment optimize(Statment statment) {
        if (statment != null) {
            try {
                return statment instanceof Optimizable
                        ? ((Optimizable) statment).optimize()
                        : statment;
            } catch (Throwable e) {
                throw new ParseException("Exception occur when do optimization", e, statment);
            }
        }
        return null;
    }

    public static List<LoopInfo> collectPossibleLoopsInfo(Statment statment) {
        return (statment != null && statment instanceof Loopable)
                ? ((Loopable) statment).collectPossibleLoopsInfo()
                : null;
    }

    public static List<LoopInfo> collectPossibleLoopsInfo(Statment[] statments) {
        if (statments != null && statments.length > 0) {
            LinkedList<LoopInfo> loopInfos = new LinkedList<LoopInfo>();
            for (int i = 0; i < statments.length; i++) {
                List<LoopInfo> list = collectPossibleLoopsInfo(statments[i]);
                if (list != null) {
                    loopInfos.addAll(list);
                }
            }
            return loopInfos.size() > 0 ? loopInfos : null;
        }
        return null;
    }

    public static LoopInfo[] collectPossibleLoopsInfoForWhileStatments(Statment bodyStatment, Statment elseStatment, String label) {

        List<LoopInfo> list = StatmentUtil.collectPossibleLoopsInfo(bodyStatment);
        if (list != null) {
            for (Iterator<LoopInfo> it = list.iterator(); it.hasNext();) {
                LoopInfo loopInfo = it.next();
                if (loopInfo.matchLabel(label)
                        && (loopInfo.type == LoopType.BREAK
                        || loopInfo.type == LoopType.CONTINUE)) {
                    it.remove();
                }
            }
            list = list.isEmpty() ? null : list;
        }

        if (elseStatment != null) {
            List<LoopInfo> list2 = StatmentUtil.collectPossibleLoopsInfo(elseStatment);
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
