// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.runtime.ast.stat;

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

    private final Expression switchExpr;
    @Nullable
    private final CaseEntry defaultBlock;
    private final Map<Object, CaseEntry> caseMap;  //Note: key == null will be default also
    private final int label;
    @Getter
    private final Position position;

    @Override
    @Nullable
    public Object execute(InternalContext context) {
        var caseBlock = caseMap.get(switchExpr.execute(context));
        if (caseBlock == null) {
            caseBlock = defaultBlock; //default
        }
        if (caseBlock != null) {
            caseBlock.execute(context);
            context.loop().resetIfBreak(label);
        }
        return null;
    }

    @Override
    public List<LoopFlag> collectLoopFlags() {
        List<LoopFlag> loops = new LinkedList<>();
        //XXX: May have duplicated LoopInfo caused by duplicated CaseEntry
        caseMap.values().forEach(entry -> loops.addAll(entry.collectPossibleLoops()));
        //remove loops for this switch
        loops.removeIf(loop -> loop.matchLabel(this.label) && loop.kind().isBreak());
        return loops;
    }

    public record CaseEntry(Statement body, @Nullable CaseEntry next) {

        @Nullable
        Object execute(InternalContext context) {
            body.execute(context);
            if (context.loop().isNoop() && next != null) {
                return next.execute(context);
            }
            return null;
        }

        List<LoopFlag> collectPossibleLoops() {
            return AstUtils.collectLoopFlags(body);
        }
    }
}
