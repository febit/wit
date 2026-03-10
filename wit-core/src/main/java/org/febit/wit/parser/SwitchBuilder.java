// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.parser;

import lombok.Setter;
import lombok.experimental.Accessors;
import org.febit.wit.exception.ParseException;
import org.febit.wit.runtime.ast.Expression;
import org.febit.wit.runtime.ast.Position;
import org.febit.wit.runtime.ast.Statement;
import org.febit.wit.runtime.ast.StatementUtils;
import org.febit.wit.runtime.ast.statement.Switch;
import org.febit.wit.runtime.ast.statement.Switch.Branch;
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
            throw new ParseException("duplicated case value in one switch", position);
        }

        if (compareTo == null) {
            if (defaultBranch != null) {
                throw new ParseException("multi default block in one switch", position);
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
