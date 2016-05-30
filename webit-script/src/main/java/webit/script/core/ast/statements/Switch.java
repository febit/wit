// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script.core.ast.statements;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import webit.script.Context;
import webit.script.core.LoopInfo;
import webit.script.core.ast.Expression;
import webit.script.core.ast.Loopable;
import webit.script.core.ast.Statement;
import webit.script.util.StatementUtil;

/**
 *
 * @author Zqq
 */
public final class Switch extends Statement implements Loopable {

    private final Expression switchExpr;
    private final CaseEntry defaultStatement;
    private final Map<Object, CaseEntry> caseMap;  //Note: key == null will be default also
    private final int label;

    Switch(Expression switchExpr, CaseEntry defaultStatement, Map<Object, CaseEntry> caseMap, int label, int line, int column) {
        super(line, column);
        this.switchExpr = switchExpr;
        this.defaultStatement = defaultStatement;
        this.caseMap = caseMap;
        this.label = label;
    }

    @Override
    public Object execute(final Context context) {
        CaseEntry caseStatement;
        if ((caseStatement = caseMap.get(switchExpr.execute(context))) == null) {
            caseStatement = defaultStatement; //default
        }
        if (caseStatement != null) {
            caseStatement.execute(context);
            context.resetBreakLoopIfMatch(label);
        }
        return null;
    }

    @Override
    public List<LoopInfo> collectPossibleLoopsInfo() {

        LinkedList<LoopInfo> loopInfos = new LinkedList<>();
        List<LoopInfo> list;
        //XXX: May have duplicated LoopInfo caused by duplicated CaseEntry
        for (Map.Entry<Object, CaseEntry> entry : caseMap.entrySet()) {
            if ((list = StatementUtil.collectPossibleLoopsInfo(entry.getValue().body)) != null) {
                loopInfos.addAll(list);
            }
        }

        //remove loops for this switch
        LoopInfo loopInfo;
        for (Iterator<LoopInfo> it = loopInfos.iterator(); it.hasNext();) {
            if ((loopInfo = it.next()).matchLabel(this.label)
                    && loopInfo.type == LoopInfo.BREAK) {
                it.remove();
            }
        }
        return loopInfos.isEmpty() ? null : loopInfos;
    }

    static final class CaseEntry {

        final Statement body;
        final CaseEntry next;

        CaseEntry(Statement body, CaseEntry next) {
            this.body = body;
            this.next = next;
        }

        Object execute(final Context context) {
            body.execute(context);
            if (context.noLoop() && next != null) {
                return next.execute(context);
            }
            return null;
        }
    }
}
