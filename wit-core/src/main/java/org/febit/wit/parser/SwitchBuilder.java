// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.parser;

import org.febit.wit.exception.ParseException;
import org.febit.wit.runtime.ast.AstUtils;
import org.febit.wit.runtime.ast.Expression;
import org.febit.wit.runtime.ast.Position;
import org.febit.wit.runtime.ast.Statement;
import org.febit.wit.runtime.ast.stat.Switch;
import org.febit.wit.runtime.ast.stat.Switch.Branch;
import org.jspecify.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class SwitchBuilder {

    @Nullable
    private Position position;
    @Nullable
    private Expression condition;
    @Nullable
    private Branch defaultBranch;
    @Nullable
    private Branch latest;

    private final Map<@Nullable Object, Branch> branches = new HashMap<>();

    public SwitchBuilder condition(Expression condition, Position position) {
        this.condition = condition;
        this.position = position;
        return this;
    }

    public SwitchBuilder branch(@Nullable Object compareTo, Statement body, Position position) {
        var branch = new Branch(AstUtils.optimize(body), latest);
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

    public Statement build(int label) {
        Objects.requireNonNull(condition);
        Objects.requireNonNull(position);

        return AstUtils.optimize(
                new Switch(condition, defaultBranch, Map.copyOf(branches), label, position)
        );
    }
}
