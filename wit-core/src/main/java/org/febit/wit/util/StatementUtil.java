// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.febit.wit.InternalContext;
import org.febit.wit.core.LoopInfo;
import org.febit.wit.core.ast.Constable;
import org.febit.wit.core.ast.Expression;
import org.febit.wit.core.ast.Loopable;
import org.febit.wit.core.ast.Optimizable;
import org.febit.wit.core.ast.Statement;
import org.febit.wit.core.ast.expressions.DirectValue;
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

    public static boolean isImmutableDirectValue(Expression expr) {
        return (expr instanceof DirectValue)
                && ALU.isKnownBaseImmutable(((DirectValue) expr).value);
    }

    public static Object calcConst(Expression expr, boolean force) {
        expr = StatementUtil.optimize(expr);
        if (expr instanceof Constable) {
            return ((Constable) expr).getConstValue();
        }
        if (force) {
            throw new ParseException("Can't get a const value from this expression.", expr.line, expr.column);
        }
        return InternalVoid.VOID;
    }

    public static Object[] calcConstArrayForce(Expression[] expressions) {
        final int len = expressions.length;
        final Object[] results = new Object[len];
        for (int i = 0; i < len; i++) {
            results[i] = StatementUtil.calcConst(expressions[i], true);
        }
        return results;
    }

    public static Object[] execute(Expression[] expressions, InternalContext context) {
        final int len = expressions.length;
        final Object[] results = new Object[len];
        for (int i = 0; i < len; i++) {
            results[i] = expressions[i].execute(context);
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

    public static void executeWithLoopCheck(final Statement[] statements, final InternalContext context) {
        int i = 0;
        final int len = statements.length;
        while (i < len && context.noLoop()) {
            statements[i++].execute(context);
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

    public static List<LoopInfo> asList(LoopInfo... loops) {
        if (loops == null || loops.length == 0) {
            return Collections.emptyList();
        }
        return Arrays.asList(loops);
    }

    public static List<LoopInfo> collectPossibleLoops(Statement statement) {
        if (statement instanceof Loopable) {
            return ((Loopable) statement).collectPossibleLoops();
        }
        return Collections.emptyList();
    }

    public static List<LoopInfo> collectPossibleLoops(Statement... statements) {
        if (statements == null || statements.length == 0) {
            return Collections.emptyList();
        }
        List<LoopInfo> loopInfos = new ArrayList<>();
        for (Statement statement : statements) {
            loopInfos.addAll(collectPossibleLoops(statement));
        }
        return loopInfos;
    }

    public static LoopInfo[] collectPossibleLoopsForWhile(Statement bodyStatement, Statement elseStatement, int label) {
        List<LoopInfo> list = StatementUtil.collectPossibleLoops(bodyStatement)
                .stream()
                .filter(loop -> !(loop.matchLabel(label) && (loop.type == LoopInfo.BREAK || loop.type == LoopInfo.CONTINUE)))
                .collect(Collectors.toList());

        list.addAll(StatementUtil.collectPossibleLoops(elseStatement));
        return list.isEmpty() ? null
                : list.toArray(new LoopInfo[list.size()]);
    }

    public static ScriptRuntimeException castToScriptRuntimeException(final Exception exception, final Statement statement) {
        if (exception instanceof ScriptRuntimeException) {
            ScriptRuntimeException scriptException = (ScriptRuntimeException) exception;
            scriptException.addStatement(statement);
            return scriptException;
        } else {
            return new ScriptRuntimeException(exception.toString(), exception, statement);
        }
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
