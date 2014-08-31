// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script.util;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import webit.script.Context;
import webit.script.core.ast.Expression;
import webit.script.core.ast.Optimizable;
import webit.script.core.ast.Statement;
import webit.script.core.ast.loop.LoopCtrl;
import webit.script.core.ast.loop.LoopInfo;
import webit.script.core.ast.loop.Loopable;
import webit.script.exceptions.ParseException;
import webit.script.exceptions.ScriptRuntimeException;

/**
 *
 * @author Zqq
 */
public class StatementUtil {

    public static void execute(final Statement[] statements, final Context context) {
        int i = 0;
        int len = statements.length;
        try {
            while (i < len) {
                statements[i].execute(context);
                i++;
            }
        } catch (Exception e) {
            throw castToScriptRuntimeException(e, statements[i]);
        }
    }

    public static void executeInverted(final Statement[] statements, final Context context) {
        int i = statements.length;
        try {
            while (i != 0) {
                --i;
                statements[i].execute(context);
            }
        } catch (Exception e) {
            throw castToScriptRuntimeException(e, statements[i]);
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
        } catch (Exception e) {
            throw castToScriptRuntimeException(e, statements[i]);
        }
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
        return statement instanceof Loopable
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
