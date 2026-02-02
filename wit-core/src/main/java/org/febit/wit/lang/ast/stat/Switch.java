// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.lang.ast.stat;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import org.febit.wit.InternalContext;
import org.febit.wit.lang.AstUtils;
import org.febit.wit.lang.LoopMeta;
import org.febit.wit.lang.Loopable;
import org.febit.wit.lang.Position;
import org.febit.wit.lang.ast.Expression;
import org.febit.wit.lang.ast.Statement;
import org.jspecify.annotations.Nullable;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Accessors(fluent = true)
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
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
            context.resetBreakLoopIfMatch(label);
        }
        return null;
    }

    @Override
    public List<LoopMeta> collectPossibleLoops() {
        List<LoopMeta> loops = new LinkedList<>();
        //XXX: May have duplicated LoopInfo caused by duplicated CaseEntry
        caseMap.values().forEach(entry -> loops.addAll(entry.collectPossibleLoops()));
        //remove loops for this switch
        loops.removeIf(loop -> loop.matchLabel(this.label) && loop.kind().isBreak());
        return loops;
    }

    record CaseEntry(Statement body, @Nullable CaseEntry next) {

        @Nullable
        Object execute(InternalContext context) {
            body.execute(context);
            if (context.loopKind().isNoop() && next != null) {
                return next.execute(context);
            }
            return null;
        }

        List<LoopMeta> collectPossibleLoops() {
            return AstUtils.collectPossibleLoops(body);
        }
    }
}
