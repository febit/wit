package org.febit.wit.runtime.ast;

import lombok.experimental.UtilityClass;
import org.jspecify.annotations.Nullable;

import java.util.function.Consumer;
import java.util.function.Predicate;

@UtilityClass
public class FlowControls {

    public static void collect(Consumer<FlowControl> collector, @Nullable Statement statement) {
        if (statement instanceof WithFlowControl with) {
            with.bubbleFlowControls(collector);
        }
    }

    public static void collect(Consumer<FlowControl> collector, @Nullable Statement... statements) {
        for (var statement : statements) {
            collect(collector, statement);
        }
    }

    public static Predicate<FlowControl> loopBubbleFilter(int label) {
        return f -> !f.matchesLabel(label)
                || !f.state().isBreakOrContinue();
    }
}
