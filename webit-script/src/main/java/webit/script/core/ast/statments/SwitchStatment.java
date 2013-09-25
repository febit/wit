// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.core.ast.statments;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import webit.script.Context;
import webit.script.core.ast.AbstractStatment;
import webit.script.core.ast.Expression;
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
    private final CaseStatment defaultStatment;
    private final Map<Object, CaseStatment> caseMap;//TODO: 可实现不可变map
    private final String label;

    public SwitchStatment(Expression switchExpr, CaseStatment defaultStatment, Map<Object, CaseStatment> caseMap, String label, int line, int column) {
        super(line, column);
        this.switchExpr = switchExpr;
        this.defaultStatment = defaultStatment;
        this.caseMap = caseMap;
        this.label = label;
    }

    public Object execute(final Context context) {
        final Object result = StatmentUtil.execute(switchExpr, context);
        boolean run = false;
        if (result != null) {
            final CaseStatment caseStatment = caseMap.get(result);
            if (caseStatment != null) {
                StatmentUtil.execute(caseStatment, context);
                run = true;
            }
        }

        //default
        if (!run && defaultStatment != null) {
            StatmentUtil.execute(defaultStatment, context);
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

        for (Map.Entry<Object, CaseStatment> entry : caseMap.entrySet()) {
            List<LoopInfo> list = entry.getValue().collectPossibleLoopsInfo();
            if (list != null) {
                loopInfos.addAll(list);
            }
        }
        if (defaultStatment != null) {
            List<LoopInfo> list = defaultStatment.collectPossibleLoopsInfo();
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
}
