// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.core.ast.statments;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import webit.script.Context;
import webit.script.core.ast.AbstractStatment;
import webit.script.core.ast.Expression;
import webit.script.core.ast.Statment;
import webit.script.core.ast.loop.LoopInfo;
import webit.script.core.ast.loop.Loopable;
import webit.script.util.StatmentUtil;

/**
 *
 * @author Zqq
 */
public final class SwitchStatment extends AbstractStatment implements Loopable {

    private final Expression switchExpr;
    private final CaseEntry defaultStatment;
    private final Map<Object, CaseEntry> caseMap;  //Note: key == null will be default also
    private final int label;

    SwitchStatment(Expression switchExpr, CaseEntry defaultStatment, Map<Object, CaseEntry> caseMap, int label, int line, int column) {
        super(line, column);
        this.switchExpr = switchExpr;
        this.defaultStatment = defaultStatment;
        this.caseMap = caseMap;
        this.label = label;
    }

    public Object execute(final Context context) {
        CaseEntry caseStatment;
        if ((caseStatment = caseMap.get(StatmentUtil.execute(switchExpr, context))) == null) {
            caseStatment = defaultStatment; //default
        }
        if (caseStatment != null) {
            caseStatment.execute(context);
            context.loopCtrl.resetBreakLoopIfMatch(label);
        }
        return null;
    }

    public List<LoopInfo> collectPossibleLoopsInfo() {
        
        LinkedList<LoopInfo> loopInfos = new LinkedList<LoopInfo>();
        List<LoopInfo> list;
        //XXX: May have duplicated LoopInfo caused by duplicated CaseEntry
        for (Map.Entry<Object, CaseEntry> entry : caseMap.entrySet()) {
            if ((list = StatmentUtil.collectPossibleLoopsInfo(entry.getValue().body)) != null) {
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

        final Statment body;
        final CaseEntry next;

        CaseEntry(Statment body, CaseEntry next) {
            this.body = body;
            this.next = next;
        }

        Object execute(final Context context) {
            StatmentUtil.execute(body, context);
            if (context.loopCtrl.getLoopType() == LoopInfo.NO_LOOP && next != null) {
                next.execute(context);
            }
            return null;
        }
    }
}
