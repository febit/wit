// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.runtime.ast;

import lombok.experimental.UtilityClass;
import org.febit.wit.exception.ParseException;
import org.febit.wit.runtime.ALU;
import org.febit.wit.runtime.ast.expr.DirectValue;
import org.febit.wit.runtime.ast.stat.NoopStatement;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public class AstUtils {

    private static final LoopFlag[] EMPTY_LOOPS = new LoopFlag[0];

    public static boolean isImmutableDirectValue(Expression expr) {
        return (expr instanceof DirectValue direct)
                && ALU.isKnownBaseImmutable(direct.value());
    }

    @Nullable
    public static Object evalConst(Expression expr) {
        return optimize(expr)
                .evalAsConst();
    }

    public static @Nullable Object[] evalConstArray(Expression[] expressions) {
        var len = expressions.length;
        var results = new Object[len];
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

}
