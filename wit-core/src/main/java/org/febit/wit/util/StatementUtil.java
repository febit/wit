// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.util;

import jakarta.annotation.Nullable;
import lombok.experimental.UtilityClass;
import org.febit.wit.exceptions.ParseException;
import org.febit.wit.lang.LoopMeta;
import org.febit.wit.lang.Loopable;
import org.febit.wit.lang.ast.Expression;
import org.febit.wit.lang.ast.Statement;
import org.febit.wit.lang.ast.expr.DirectValue;
import org.febit.wit.lang.ast.stat.NoopStatement;
import org.febit.wit.lang.ast.stat.StatementGroup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author zqq90
 */
@UtilityClass
public class StatementUtil {

    private static final Statement[] EMPTY_STATEMENTS = new Statement[0];
    private static final Expression[] EMPTY_EXPRESSIONS = new Expression[0];
    private static final LoopMeta[] EMPTY_LOOPS = new LoopMeta[0];

    public static boolean isImmutableDirectValue(Expression expr) {
        return (expr instanceof DirectValue)
                && ALU.isKnownBaseImmutable(((DirectValue) expr).value);
    }

    @Nullable
    public static Object calcConst(Expression expr) {
        return StatementUtil.optimize(expr)
                .getConstValue();
    }

    public static Object[] calcConstArray(Expression[] expressions) {
        final int len = expressions.length;
        final Object[] results = new Object[len];
        for (int i = 0; i < len; i++) {
            results[i] = StatementUtil.calcConst(expressions[i]);
        }
        return results;
    }

    @Nullable
    public static Expression optimize(@Nullable Expression expression) {
        if (expression == null) {
            return null;
        }
        return expression.optimize();
    }

    public static void optimize(Expression[] expression) {
        for (int i = 0; i < expression.length; i++) {
            expression[i] = optimize(expression[i]);
        }
    }

    public static Statement optimize(@Nullable Statement statement) {
        if (statement == null) {
            return NoopStatement.INSTANCE;
        }
        try {
            return statement.optimize();
        } catch (Exception e) {
            throw new ParseException("Exception occur when do optimization", e, statement.getPosition());
        }
    }

    public static List<LoopMeta> asList(LoopMeta... loops) {
        if (loops.length == 0) {
            return Collections.emptyList();
        }
        return Arrays.asList(loops);
    }

    public static List<LoopMeta> collectPossibleLoops(@Nullable Statement statement) {
        if (statement instanceof Loopable) {
            return ((Loopable) statement).collectPossibleLoops();
        }
        return Collections.emptyList();
    }

    public static List<LoopMeta> collectPossibleLoops(Statement... statements) {
        if (statements.length == 0) {
            return Collections.emptyList();
        }
        List<LoopMeta> loops = new ArrayList<>();
        for (Statement statement : statements) {
            loops.addAll(collectPossibleLoops(statement));
        }
        return loops;
    }

    public static LoopMeta[] collectPossibleLoopsForWhile(Statement bodyStatement, Statement elseStatement, int label) {
        List<LoopMeta> list = StatementUtil.collectPossibleLoops(bodyStatement)
                .stream()
                .filter(loop -> !(loop.matchLabel(label)
                        && (loop.type == LoopMeta.BREAK || loop.type == LoopMeta.CONTINUE)))
                .collect(Collectors.toList());

        list.addAll(StatementUtil.collectPossibleLoops(elseStatement));
        return list.isEmpty() ? EMPTY_LOOPS
                : list.toArray(new LoopMeta[0]);
    }

    public static Expression[] emptyExpressions() {
        return EMPTY_EXPRESSIONS;
    }

    public static Expression[] toExpressionArray(@Nullable List<Expression> list) {
        if (list == null || list.isEmpty()) {
            return EMPTY_EXPRESSIONS;
        }
        var arr = list.toArray(new Expression[0]);
        for (int i = 0; i < arr.length; i++) {
            arr[i] = optimize(arr[i]);
        }
        return arr;
    }

    public static Statement[] toStatementArray(@Nullable List<Statement> list) {
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
            if (!(stat instanceof NoopStatement)) {
                temp.add(stat);
            }
        }
        return list.isEmpty()
                ? EMPTY_STATEMENTS
                : temp.toArray(new Statement[0]);
    }
}
