// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.runtime.ast;

import lombok.experimental.UtilityClass;
import org.febit.wit.exception.ParseException;
import org.febit.wit.runtime.ALU;
import org.febit.wit.runtime.ast.expr.DirectValue;
import org.febit.wit.runtime.ast.statement.NoopStatement;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

@UtilityClass
public class AstUtils {

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

    public static void collectFlowControls(Consumer<FlowControl> collector, @Nullable Statement statement) {
        if (statement instanceof WithFlowControl with) {
            with.collectFlowControls(collector);
        }
    }

    public static void collectFlowControls(Consumer<FlowControl> collector, @Nullable Statement... statements) {
        for (var statement : statements) {
            collectFlowControls(collector, statement);
        }
    }

    public static boolean hasFlowControls(Statement @Nullable ... statements) {
        if (statements == null) {
            return false;
        }
        var has = new AtomicBoolean(false);
        AstUtils.collectFlowControls(ctrl -> has.set(true), statements);
        return has.get();
    }

    /**
     * Collect flow controls for loop body and else block.
     * <p>
     * Flow controls in loop body that targeting to current loop statement will be ignored.
     */
    public static List<FlowControl> flowControlsOverLoop(
            int label, List<Statement> body, @Nullable Statement elseBody) {
        var list = new ArrayList<FlowControl>();
        // Only accept controls that are not targeting to current while/for statement
        body.forEach(stat -> collectFlowControls(f -> {
            if (f.matchesLabel(label) && f.kind().isBreakOrContinue()) {
                return;
            }
            list.add(f);
        }, stat));
        // Controls in else block are all exported
        collectFlowControls(list::add, elseBody);
        return List.copyOf(list);
    }

}
