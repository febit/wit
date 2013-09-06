// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.core.ast.statments;

import java.util.Map;
import webit.script.Context;
import webit.script.core.ast.AbstractStatment;
import webit.script.core.ast.Expression;
import webit.script.util.StatmentUtil;

/**
 *
 * @author Zqq
 */
public final class SwitchStatment extends AbstractStatment {

    private final Expression switchExpr;
    private final CaseStatment defaultStatment;
    private final Map<Object, CaseStatment> caseMap;//TODO: 可实现不可变map
    private final boolean hasDefaultStatment;
    private final String label;

    public SwitchStatment(Expression switchExpr, CaseStatment defaultStatment, Map<Object, CaseStatment> caseMap, String label, int line, int column) {
        super(line, column);
        this.switchExpr = switchExpr;
        this.defaultStatment = defaultStatment;
        this.caseMap = caseMap;
        this.label = label;
        this.hasDefaultStatment = defaultStatment != null;
    }

    public void execute(Context context) {
        Object result = StatmentUtil.execute(switchExpr, context);
        boolean run = false;
        if (result != null) {
            CaseStatment caseStatment = caseMap.get(result);
            if (caseStatment != null) {
                StatmentUtil.execute(caseStatment, context);
                run = true;
            }
        }

        //default
        if (!run && hasDefaultStatment) {
            StatmentUtil.execute(defaultStatment, context);
            run = true;
        }

        //clear break status
        if (run && context.loopCtrl.mathBreakLoop(label)) {
            context.loopCtrl.reset();
        }
    }
}
