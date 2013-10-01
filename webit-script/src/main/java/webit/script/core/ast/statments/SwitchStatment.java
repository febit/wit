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
import webit.script.core.ast.loop.LoopType;
import webit.script.core.ast.loop.Loopable;
import webit.script.util.StatmentUtil;

/**
 *
 * @author Zqq
 */
public final class SwitchStatment extends AbstractStatment implements Loopable {

    private final Expression switchExpr;
    private final CaseEntry defaultStatment;
    private final Map<Object, CaseEntry> caseMap;//TODO: 可实现不可变map
    private final String label;

    SwitchStatment(Expression switchExpr, CaseEntry defaultStatment, Map<Object, CaseEntry> caseMap, String label, int line, int column) {
        super(line, column);
        this.switchExpr = switchExpr;
        this.defaultStatment = defaultStatment;
        this.caseMap = caseMap;
        this.label = label;
    }

    public Object execute(final Context context) {
        final Object result;
        boolean run = false;
        if ((result = StatmentUtil.execute(switchExpr, context)) != null) {
            final CaseEntry caseStatment = caseMap.get(result);
            if (caseStatment != null) {
                caseStatment.execute(context);
                run = true;
            }
        }

        //default
        if (!run && defaultStatment != null) {
            defaultStatment.execute(context);
            run = true;
        }

        //clear break status
        if (run && context.loopCtrl.mathBreakLoop(label)) {
            context.loopCtrl.reset();
        }
        return null;
    }

    public List<LoopInfo> collectPossibleLoopsInfo() {
        //collect
        LinkedList<LoopInfo> loopInfos = new LinkedList<LoopInfo>();

        //XXX: May have duplicated LoopInfo caused by duplicated CaseEntry
        for (Map.Entry<Object, CaseEntry> entry : caseMap.entrySet()) {
            List<LoopInfo> list = StatmentUtil.collectPossibleLoopsInfo(entry.getValue().body);
            if (list != null) {
                loopInfos.addAll(list);
            }
        }
        if (defaultStatment != null) {
            List<LoopInfo> list = StatmentUtil.collectPossibleLoopsInfo(defaultStatment.body);
            if (list != null) {
                loopInfos.addAll(list);
            }
        }

        //check
        for (Iterator<LoopInfo> it = loopInfos.iterator(); it.hasNext();) {
            LoopInfo loopInfo = it.next();
            if (loopInfo.matchLabel(this.label)
                    && loopInfo.type == LoopType.BREAK) {
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
            body.execute(context);
            if (context.loopCtrl.goon() && next != null) {
                next.execute(context);
            }
            return null;
        }
    }
}
