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
package org.febit.wit.ir.switches;

import org.febit.wit.exception.ScriptEvaluateException;
import org.febit.wit.ir.Expression;
import org.febit.wit.ir.JumpAware;
import org.febit.wit.ir.Position;
import org.febit.wit.ir.flow.Jump;
import org.febit.wit.ir.support.Jumps;
import org.febit.wit.runtime.RuntimeContext;
import org.jspecify.annotations.Nullable;

import java.util.Map;
import java.util.function.Consumer;

public record SwitchStatement(
        int label,
        Expression condition,
        Map<@Nullable Object, SwitchBranch> branches,
        @Nullable SwitchBranch defaultBranch,
        Position position
) implements Expression, JumpAware {

    @Override
    @Nullable
    public Object execute(RuntimeContext context) {
        var key = condition.execute(context);
        var branch = branches.get(key);
        if (branch == null) {
            branch = defaultBranch;
        }
        if (branch == null) {
            throw new ScriptEvaluateException("switch expression did not match any branch");
        }
        return branch.execute(context);
    }

    @Override
    public void collectJumps(Consumer<Jump> collector) {
        var filtered = (Consumer<Jump>) jump -> {
            if (!jump.matchesLabel(this.label)
                    || !jump.state().isBreak()) {
                collector.accept(jump);
            }
        };

        branches.values().forEach(branch -> {
            if (branch instanceof JumpAwareSwitchBranch jumpAware) {
                Jumps.collect(filtered, jumpAware.body());
            }
        });
        if (defaultBranch instanceof JumpAwareSwitchBranch jumpAware) {
            Jumps.collect(filtered, jumpAware.body());
        }
    }

}
