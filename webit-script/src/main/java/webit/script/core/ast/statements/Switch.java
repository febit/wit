// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.core.ast.statements;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import webit.script.Context;
import webit.script.core.ast.AbstractStatement;
import webit.script.core.ast.Expression;
import webit.script.core.ast.Statement;
import webit.script.core.ast.loop.LoopInfo;
import webit.script.core.ast.loop.Loopable;
import webit.script.util.StatementUtil;

/**
 *
 * @author Zqq
 */
public final class Switch extends AbstractStatement implements Loopable {

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

    public Object execute(final Context context) {
        CaseEntry caseStatement;
        if ((caseStatement = caseMap.get(StatementUtil.execute(switchExpr, context))) == null) {
            caseStatement = defaultStatement; //default
        }
        if (caseStatement != null) {
            caseStatement.execute(context);
            context.loopCtrl.resetBreakLoopIfMatch(label);
        }
        return null;
    }

    public List<LoopInfo> collectPossibleLoopsInfo() {
        
        LinkedList<LoopInfo> loopInfos = new LinkedList<LoopInfo>();
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
            StatementUtil.execute(body, context);
            if (context.loopCtrl.getLoopType() == LoopInfo.NO_LOOP && next != null) {
                next.execute(context);
            }
            return null;
        }
    }
}
