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
package org.febit.wit.parser.support;

import lombok.Setter;
import lombok.experimental.Accessors;
import org.febit.wit.exception.ScriptParseException;
import org.febit.wit.ir.Expression;
import org.febit.wit.ir.Position;
import org.febit.wit.ir.Statement;
import org.febit.wit.ir.statement.Switch;
import org.febit.wit.ir.statement.Switch.Branch;
import org.febit.wit.ir.support.StatementUtils;
import org.jspecify.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Accessors(fluent = true, chain = true)
public class SwitchBuilder {

    @Nullable
    private Position position;
    @Nullable
    private Expression condition;
    @Nullable
    private Branch defaultBranch;
    @Nullable
    private Branch latest;

    @Setter
    private int label;

    private final Map<@Nullable Object, Branch> branches = new HashMap<>();

    public SwitchBuilder condition(Expression condition, Position position) {
        this.condition = condition;
        this.position = position;
        return this;
    }

    public SwitchBuilder branch(@Nullable Object compareTo, Statement body, Position position) {
        var branch = new Branch(StatementUtils.optimize(body), latest);
        // else use last as current for this key
        if (branches.containsKey(compareTo)) {
            throw new ScriptParseException("duplicated case value in one switch", position);
        }

        if (compareTo == null) {
            if (defaultBranch != null) {
                throw new ScriptParseException("multi default block in one switch", position);
            }
            defaultBranch = branch;
        } else {
            branches.put(compareTo, branch);
        }

        latest = branch;
        return this;
    }

    public Statement build() {
        Objects.requireNonNull(condition);
        Objects.requireNonNull(position);

        return StatementUtils.optimize(
                new Switch(label, condition, Map.copyOf(branches), defaultBranch, position)
        );
    }
}
