// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.runtime;

import lombok.experimental.UtilityClass;
import org.febit.wit.exceptions.ParseException;
import org.febit.wit.runtime.ast.Expression;
import org.febit.wit.runtime.ast.Statement;
import org.febit.wit.runtime.ast.expr.DirectValue;
import org.febit.wit.runtime.ast.stat.NoopStatement;
import org.febit.wit.runtime.ast.stat.StatementGroup;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public class AstUtils {

    private static final Statement[] EMPTY_STATEMENTS = new Statement[0];
    private static final Expression[] EMPTY_EXPRESSIONS = new Expression[0];
    private static final LoopFlag[] EMPTY_LOOPS = new LoopFlag[0];

    public static boolean isImmutableDirectValue(Expression expr) {
        return (expr instanceof DirectValue direct)
                && ALU.isKnownBaseImmutable(direct.value);
    }

    @Nullable
    public static Object evalConst(Expression expr) {
        return optimize(expr)
                .evalAsConst();
    }

    public static @Nullable Object[] evalConstArray(Expression[] expressions) {
        final int len = expressions.length;
        @Nullable
        Object[] results = new Object[len];
        for (int i = 0; i < len; i++) {
            results[i] = AstUtils.evalConst(expressions[i]);
        }
        return results;
    }

    public static Expression optimize(Expression expression) {
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
            throw new ParseException("Exception occur when do optimization", e, statement.position());
        }
    }

    public static List<LoopFlag> asList(LoopFlag... loops) {
        if (loops.length == 0) {
            return Collections.emptyList();
        }
        return Arrays.asList(loops);
    }

    public static List<LoopFlag> collectLoopFlags(@Nullable Statement statement) {
        if (statement instanceof Loopable loopable) {
            return loopable.collectLoopFlags();
        }
        return Collections.emptyList();
    }

    public static List<LoopFlag> collectLoopFlags(@Nullable Statement... statements) {
        if (statements.length == 0) {
            return Collections.emptyList();
        }
        List<LoopFlag> loops = new ArrayList<>();
        for (var statement : statements) {
            loops.addAll(collectLoopFlags(statement));
        }
        return loops;
    }

    /**
     * Collect loops exported by while/for statement.
     * <p>
     * Loops target to current while/for statement will be excluded.
     */
    public static LoopFlag[] collectLoopFlagsForWhile(
            @Nullable Statement bodyBlock, @Nullable Statement elseBlock, int label) {
        var list = AstUtils.collectLoopFlags(bodyBlock)
                .stream()
                // Only accept loops that are not targeting to current while/for statement
                .filter(loop -> !(loop.matchLabel(label)
                        && loop.kind().isBreakOrContinue()))
                .collect(Collectors.toList());

        // Loops in else block are all exported
        list.addAll(AstUtils.collectLoopFlags(elseBlock));
        return list.isEmpty() ? EMPTY_LOOPS
                : list.toArray(new LoopFlag[0]);
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

    public static Statement[] flatStatements(@Nullable List<Statement> list) {
        if (list == null || list.isEmpty()) {
            return EMPTY_STATEMENTS;
        }
        List<Statement> temp = new ArrayList<>(list.size());
        for (var stat : list) {
            if (stat instanceof StatementGroup group) {
                temp.addAll(group.list());
                continue;
            }
            stat = AstUtils.optimize(stat);
            if (!(stat instanceof NoopStatement)) {
                temp.add(stat);
            }
        }
        return list.isEmpty()
                ? EMPTY_STATEMENTS
                : temp.toArray(new Statement[0]);
    }
}
