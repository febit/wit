// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.lang.ast.stat;

import jakarta.annotation.Nullable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.febit.wit.InternalContext;
import org.febit.wit.lang.LoopMeta;
import org.febit.wit.lang.Loopable;
import org.febit.wit.lang.Position;
import org.febit.wit.lang.ast.Expression;
import org.febit.wit.lang.ast.Statement;
import org.febit.wit.util.StatementUtil;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author zqq90
 */
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public final class Switch implements Statement, Loopable {

    private final Expression switchExpr;
    private final CaseEntry defaultStatement;
    private final Map<Object, CaseEntry> caseMap;  //Note: key == null will be default also
    private final int label;
    @Getter
    private final Position position;

    @Override
    @Nullable
    public Object execute(final InternalContext context) {
        CaseEntry caseStatement = caseMap.get(switchExpr.execute(context));
        if (caseStatement == null) {
            caseStatement = defaultStatement; //default
        }
        if (caseStatement != null) {
            caseStatement.execute(context);
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
        loops.removeIf(loop -> loop.matchLabel(this.label) && loop.type == LoopMeta.BREAK);
        return loops;
    }

    @RequiredArgsConstructor(access = AccessLevel.PACKAGE)
    static final class CaseEntry {

        final Statement body;
        @Nullable
        final CaseEntry next;

        @Nullable
        Object execute(final InternalContext context) {
            body.execute(context);
            if (context.noLoop() && next != null) {
                return next.execute(context);
            }
            return null;
        }

        List<LoopMeta> collectPossibleLoops() {
            return StatementUtil.collectPossibleLoops(body);
        }
    }
}
