// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.runtime.ast.statement;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import org.febit.wit.runtime.InternalContext;
import org.febit.wit.runtime.ast.AstUtils;
import org.febit.wit.runtime.ast.Expression;
import org.febit.wit.runtime.ast.LoopFlag;
import org.febit.wit.runtime.ast.Loopable;
import org.febit.wit.runtime.ast.Position;
import org.febit.wit.runtime.ast.Statement;
import org.jspecify.annotations.Nullable;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Accessors(fluent = true)
@RequiredArgsConstructor
public final class Switch implements Statement, Loopable {

    private final Expression condition;
    @Nullable
    private final Branch defaultBranch;
    private final Map<Object, Branch> branches;
    private final int label;
    @Getter
    private final Position position;

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
            context.loop().resetIfBreak(label);
        }
        return null;
    }

    @Override
    public List<LoopFlag> collectLoopFlags() {
        List<LoopFlag> loops = new LinkedList<>();
        //XXX: May have duplicated LoopInfo caused by duplicated CaseEntry
        branches.values().forEach(entry -> loops.addAll(entry.collectPossibleLoops()));
        //remove loops for this switch
        loops.removeIf(loop -> loop.matchLabel(this.label) && loop.kind().isBreak());
        return loops;
    }

    public record Branch(Statement body, @Nullable Branch next) {

        @Nullable
        Object execute(InternalContext context) {
            body.execute(context);
            if (context.loop().isNone() && next != null) {
                return next.execute(context);
            }
            return null;
        }

        List<LoopFlag> collectPossibleLoops() {
            return AstUtils.collectLoopFlags(body);
        }
    }
}
