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
import org.febit.wit.ir.Located;
import org.febit.wit.ir.Statement;
import org.febit.wit.ir.support.Jumps;
import org.febit.wit.ir.support.StatementUtils;
import org.febit.wit.ir.switches.ExpressionSwitchBranch;
import org.febit.wit.ir.switches.JumpAwareSwitchBranch;
import org.febit.wit.ir.switches.NoJumpSwitchBranch;
import org.febit.wit.ir.switches.SwitchBranch;
import org.febit.wit.ir.switches.SwitchExpression;
import org.febit.wit.ir.switches.SwitchStatement;
import org.febit.wit.ir.switches.YieldSwitchBranch;
import org.jspecify.annotations.Nullable;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;

import static java.util.Objects.requireNonNull;

@Accessors(fluent = true, chain = true)
public class EnhancedSwitchBuilder {

    @Nullable
    private Located located;
    @Nullable
    private Expression condition;
    private @Nullable Statement defaultBranch;

    @Setter
    private int label;

    private final Map<@Nullable Object, Statement> branches = new HashMap<>();

    public EnhancedSwitchBuilder condition(Expression condition, Located located) {
        this.condition = condition;
        this.located = located;
        return this;
    }

    private void attachBranch(@Nullable Object compareTo, Statement branch) {
        if (branches.putIfAbsent(compareTo, branch) != null) {
            throw new ScriptParseException("duplicated case value in one switch", branch);
        }
    }

    public EnhancedSwitchBuilder branch(List<Object> compareToList, Statement body) {
        var optimized = StatementUtils.optimize(body);
        for (var compareTo : compareToList) {
            attachBranch(compareTo, optimized);
        }
        return this;
    }

    public EnhancedSwitchBuilder defaultBranch(Statement body) {
        defaultBranch = StatementUtils.optimize(body);
        return this;
    }

    private SwitchBranch toBranch(Statement statement) {
        if (statement instanceof Expression expr) {
            return new ExpressionSwitchBranch(expr);
        }

        var withYield = new AtomicBoolean(false);
        var withGenericJump = new AtomicBoolean(false);

        Jumps.collect(jump -> {
            switch (jump.state()) {
                case YIELD -> withYield.set(true);
                case BREAK, RETURN, CONTINUE -> withGenericJump.set(true);
                case NOOP -> {
                    // noop
                }
                default -> throw new IllegalStateException("unexpected jump state: " + jump.state());
            }
        }, statement);

        if (withGenericJump.get()) {
            return new JumpAwareSwitchBranch(label, statement);
        }
        if (withYield.get()) {
            return new YieldSwitchBranch(statement);
        }
        return new NoJumpSwitchBranch(statement);
    }

    private Map<@Nullable Object, SwitchBranch> buildBranches(Function<Statement, SwitchBranch> transformer) {
        var result = new HashMap<@Nullable Object, SwitchBranch>();
        branches.forEach((compareTo, statement) ->
                result.put(compareTo, transformer.apply(statement))
        );
        return result.containsKey(null)
                ? Collections.unmodifiableMap(result)
                : Map.copyOf(result);
    }

    private SwitchBranch transformForExpression(Statement statement) {
        var branch = toBranch(statement);
        if (branch instanceof ExpressionSwitchBranch
                || branch instanceof NoJumpSwitchBranch
                || branch instanceof YieldSwitchBranch
        ) {
            return branch;
        }
        throw new ScriptParseException("Unsupported switch branch for expression: " + branch.getClass(), branch);
    }

    private SwitchBranch transformForStatement(Statement statement) {
        var branch = toBranch(statement);
        if (branch instanceof ExpressionSwitchBranch
                || branch instanceof NoJumpSwitchBranch
                || branch instanceof JumpAwareSwitchBranch
        ) {
            return branch;
        }
        if (branch instanceof YieldSwitchBranch yieldBranch) {
            return new JumpAwareSwitchBranch(label, yieldBranch.body());
        }
        throw new ScriptParseException("Unsupported switch branch for statement: " + branch.getClass(), branch);
    }

    public Statement buildAsStatement() {
        requireNonNull(condition);
        requireNonNull(located);
        return new SwitchStatement(
                label,
                condition,
                buildBranches(this::transformForStatement),
                defaultBranch != null ? transformForStatement(defaultBranch) : null,
                located.position()
        );
    }

    public Expression buildAsExpression() {
        requireNonNull(condition);
        requireNonNull(located);
        if (label != 0) {
            throw new ScriptParseException("label is not allowed in switch expression", located);
        }
        return new SwitchExpression(
                condition,
                buildBranches(this::transformForExpression),
                defaultBranch != null ? transformForExpression(defaultBranch) : null,
                located.position()
        );
    }
}
