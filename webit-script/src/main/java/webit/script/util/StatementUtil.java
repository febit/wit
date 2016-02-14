// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script.util;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import webit.script.Context;
import webit.script.core.LoopInfo;
import webit.script.core.ast.Expression;
import webit.script.core.ast.Loopable;
import webit.script.core.ast.Optimizable;
import webit.script.core.ast.Statement;
import webit.script.exceptions.ParseException;
import webit.script.exceptions.ScriptRuntimeException;

/**
 *
 * @author Zqq
 */
public class StatementUtil {

    private StatementUtil() {
    }

    public static void execute(final Statement[] statements, final Context context) {
        int i = 0;
        final int len = statements.length;
        while (i < len) {
            statements[i++].execute(context);
        }
    }

    public static void executeInverted(final Statement[] statements, final Context context) {
        int i = statements.length;
        while (i != 0) {
            statements[--i].execute(context);
        }
    }

    public static void executeInvertedAndCheckLoops(final Statement[] statements, final Context context) {
        int i = statements.length;
        do {
            statements[--i].execute(context);
        } while (i != 0 && context.noLoop());
    }

    public static Expression optimize(Expression expression) {
        try {
            return expression instanceof Optimizable
                    ? (Expression) ((Optimizable) expression).optimize()
                    : expression;
        } catch (Exception e) {
            throw new ParseException("Exception occur when do optimization", e, expression);
        }
    }

    public static void optimize(Expression[] expression) {
        for (int i = 0; i < expression.length; i++) {
            expression[i] = optimize(expression[i]);
        }
    }

    public static Statement optimize(Statement statement) {
        try {
            return statement instanceof Optimizable
                    ? ((Optimizable) statement).optimize()
                    : statement;
        } catch (Exception e) {
            throw new ParseException("Exception occur when do optimization", e, statement);
        }
    }

    public static List<LoopInfo> collectPossibleLoopsInfo(Statement statement) {
        if (statement instanceof Loopable) {
            return ((Loopable) statement).collectPossibleLoopsInfo();
        }
        return null;
    }

    public static List<LoopInfo> collectPossibleLoopsInfo(Statement stat1, Statement stat2) {

        List<LoopInfo> list = StatementUtil.collectPossibleLoopsInfo(stat1);
        List<LoopInfo> list2 = StatementUtil.collectPossibleLoopsInfo(stat2);

        if (list == null) {
            return list2;
        }
        if (list2 != null) {
            list.addAll(list2);
        }
        return list;
    }

    public static List<LoopInfo> collectPossibleLoopsInfo(Statement... statements) {
        int i;
        if (statements == null || (i = statements.length) == 0) {
            return null;
        }
        final LinkedList<LoopInfo> loopInfos = new LinkedList<LoopInfo>();
        do {
            List<LoopInfo> list = collectPossibleLoopsInfo(statements[--i]);
            if (list != null) {
                loopInfos.addAll(list);
            }
        } while (i != 0);
        return loopInfos.isEmpty() ? null : loopInfos;
    }

    public static LoopInfo[] collectPossibleLoopsInfoForWhile(Statement bodyStatement, Statement elseStatement, int label) {

        List<LoopInfo> list;
        LoopInfo loopInfo;
        if ((list = StatementUtil.collectPossibleLoopsInfo(bodyStatement)) != null) {
            for (Iterator<LoopInfo> it = list.iterator(); it.hasNext();) {
                loopInfo = it.next();
                if (loopInfo.matchLabel(label)
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
        return list == null || list.isEmpty()
                ? null
                : list.toArray(new LoopInfo[list.size()]);
    }

    public static ScriptRuntimeException castToScriptRuntimeException(final Exception exception, final Statement statement) {
        if (exception instanceof ScriptRuntimeException) {
            ScriptRuntimeException scriptException = (ScriptRuntimeException) exception;
            scriptException.registStatement(statement);
            return scriptException;
        } else {
            return new ScriptRuntimeException(exception.toString(), exception, statement);
        }
    }
}
