// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.core.ast.statements;

import org.febit.wit.InternalContext;
import org.febit.wit.core.LoopInfo;
import org.febit.wit.core.ast.Expression;
import org.febit.wit.core.ast.Loopable;
import org.febit.wit.core.ast.Statement;
import org.febit.wit.util.StatementUtil;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author zqq90
 */
public final class Switch extends Statement implements Loopable {

    private final Expression switchExpr;
    private final CaseEntry defaultStatement;
    private final Map<Object, CaseEntry> caseMap;  //Note: key == null will be default also
    private final int label;

    Switch(Expression switchExpr, CaseEntry defaultStatement, Map<Object, CaseEntry> caseMap,
           int label, int line, int column) {
        super(line, column);
        this.switchExpr = switchExpr;
        this.defaultStatement = defaultStatement;
        this.caseMap = caseMap;
        this.label = label;
    }

    @Override
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
    public List<LoopInfo> collectPossibleLoops() {
        List<LoopInfo> loopInfos = new LinkedList<>();
        //XXX: May have duplicated LoopInfo caused by duplicated CaseEntry
        caseMap.values().forEach(entry -> loopInfos.addAll(entry.collectPossibleLoops()));
        //remove loops for this switch
        loopInfos.removeIf(loop -> loop.matchLabel(this.label) && loop.type == LoopInfo.BREAK);
        return loopInfos;
    }

    static final class CaseEntry {

        final Statement body;
        final CaseEntry next;

        CaseEntry(Statement body, CaseEntry next) {
            this.body = body;
            this.next = next;
        }

        Object execute(final InternalContext context) {
            body.execute(context);
            if (context.noLoop() && next != null) {
                return next.execute(context);
            }
            return null;
        }

        List<LoopInfo> collectPossibleLoops() {
            return StatementUtil.collectPossibleLoops(body);
        }
    }
}
