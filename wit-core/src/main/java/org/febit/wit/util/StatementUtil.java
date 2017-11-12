// Copyright (c) 2013-2016, febit.org. All Rights Reserved.
package org.febit.wit.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import org.febit.wit.InternalContext;
import org.febit.wit.core.LoopInfo;
import org.febit.wit.core.ast.Constable;
import org.febit.wit.core.ast.Expression;
import org.febit.wit.core.ast.Loopable;
import org.febit.wit.core.ast.Optimizable;
import org.febit.wit.core.ast.Statement;
import org.febit.wit.core.ast.statements.StatementGroup;
import org.febit.wit.exceptions.ParseException;
import org.febit.wit.exceptions.ScriptRuntimeException;
import org.febit.wit.lang.InternalVoid;

/**
 *
 * @author zqq90
 */
public class StatementUtil {

    private static final Statement[] EMPTY_STATEMENTS = new Statement[0];
    private static final Expression[] EMPTY_EXPRESSIONS = new Expression[0];

    private StatementUtil() {
    }

    public static Object calcConst(Expression expr, boolean force) {
        expr = StatementUtil.optimize(expr);
        if (expr instanceof Constable) {
            Object result = ((Constable) expr).getConstValue();
            if (result != InternalVoid.VOID) {
                return result;
            }
        }
        if (force) {
            throw new ParseException("Can't get a const value from this expression.", expr.line, expr.column);
        }
        return InternalVoid.VOID;
    }

    public static Object[] calcConstArrayForce(Expression[] expr) {
        final int len = expr.length;
        final Object[] results = new Object[len];
        for (int i = 0; i < len; i++) {
            results[i] = StatementUtil.calcConst(expr[i], true);
        }
        return results;
    }

    public static void execute(final Statement[] statements, final InternalContext context) {
        int i = 0;
        final int len = statements.length;
        while (i < len) {
            statements[i++].execute(context);
        }
    }

    public static void executeInverted(final Statement[] statements, final InternalContext context) {
        int i = statements.length;
        while (i != 0) {
            statements[--i].execute(context);
        }
    }

    public static void executeInvertedAndCheckLoops(final Statement[] statements, final InternalContext context) {
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
        if (statements == null || statements.length == 0) {
            return null;
        }
        final LinkedList<LoopInfo> loopInfos = new LinkedList<>();
        int i = statements.length;
        do {
            List<LoopInfo> list = collectPossibleLoopsInfo(statements[--i]);
            if (list != null) {
                loopInfos.addAll(list);
            }
        } while (i != 0);
        return loopInfos.isEmpty() ? null : loopInfos;
    }

    public static LoopInfo[] collectPossibleLoopsInfoForWhile(Statement bodyStatement, Statement elseStatement, int label) {

        List<LoopInfo> list = StatementUtil.collectPossibleLoopsInfo(bodyStatement);
        if (list != null) {
            LoopInfo loopInfo;
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

    public static Statement[] toStatementInvertArray(List<Statement> list) {
        Statement[] array = toStatementArray(list);
        ArrayUtil.invert(array);
        return array;
    }

    public static Expression[] emptyExpressions() {
        return EMPTY_EXPRESSIONS;
    }

    public static Expression[] toExpressionArray(List<Expression> list) {
        if (list == null || list.isEmpty()) {
            return EMPTY_EXPRESSIONS;
        }
        Expression[] arr = list.toArray(new Expression[list.size()]);
        for (int i = 0; i < arr.length; i++) {
            arr[i] = optimize(arr[i]);
        }
        return arr;
    }

    public static Statement[] toStatementArray(List<Statement> list) {
        if (list == null || list.isEmpty()) {
            return EMPTY_STATEMENTS;
        }
        List<Statement> temp = new ArrayList<>(list.size());
        for (Statement stat : list) {
            if (stat instanceof StatementGroup) {
                temp.addAll(Arrays.asList(((StatementGroup) stat).getList()));
                continue;
            }
            stat = StatementUtil.optimize(stat);
            if (stat != null) {
                temp.add(stat);
            }
        }
        return list.isEmpty()
                ? EMPTY_STATEMENTS
                : temp.toArray(new Statement[temp.size()]);
    }
}
