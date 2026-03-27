/*
 * Copyright 2013-present febit.org (support@febit.org)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.febit.wit.runtime.ast.statement;

import org.febit.wit.runtime.InternalContext;
import org.febit.wit.runtime.ast.Expression;
import org.febit.wit.runtime.ast.FlowControl;
import org.febit.wit.runtime.ast.FlowControls;
import org.febit.wit.runtime.ast.Position;
import org.febit.wit.runtime.ast.Statement;
import org.febit.wit.runtime.ast.WithFlowControl;
import org.jspecify.annotations.Nullable;

import java.util.Map;
import java.util.function.Consumer;

public record Switch(
        int label,
        Expression condition,
        Map<Object, Branch> branches,
        @Nullable Branch defaultBranch,
        Position position
) implements Statement, WithFlowControl {

    @Override
    @Nullable
    public Object execute(InternalContext context) {
        var key = condition.execute(context);
        var branch = key != null
                ? branches.get(key)
                : defaultBranch;
        if (branch == null) {
            branch = defaultBranch;
        }
        if (branch != null) {
            branch.execute(context);
            context.flow().resetIfBreak(label);
        }
        return null;
    }

    @Override
    public void bubbleFlowControls(Consumer<FlowControl> collector) {
        //XXX: May have duplicated controls caused by duplicated Branch
        var filtered = (Consumer<FlowControl>) ctrl -> {
            if (!ctrl.matchesLabel(this.label) || !ctrl.state().isBreak()) {
                collector.accept(ctrl);
            }
        };

        branches.values().forEach(entry ->
                FlowControls.bubble(filtered, entry.body)
        );
        if (defaultBranch != null) {
            FlowControls.bubble(filtered, defaultBranch.body);
        }
    }

    public record Branch(Statement body, @Nullable Branch next) {

        @Nullable
        Object execute(InternalContext context) {
            body.execute(context);
            if (context.flow().isNoop() && next != null) {
                return next.execute(context);
            }
            return null;
        }

    }
}
