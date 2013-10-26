// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.core.ast.statments;

import java.util.HashMap;
import java.util.Map;
import webit.script.core.ast.Expression;
import webit.script.core.ast.Position;
import webit.script.core.ast.Statment;
import webit.script.core.ast.statments.SwitchStatment.CaseEntry;
import webit.script.exceptions.ParseException;
import webit.script.util.StatmentUtil;

/**
 *
 * @author Zqq
 */
public final class SwitchStatmentPart extends Position {

    private Expression switchExpr;
    private CaseEntry defaultStatment;
    private final Map<Object, CaseEntry> caseMap;
    private CaseEntry currentCaseStatment;

    public SwitchStatmentPart() {
        this.currentCaseStatment = null;
        this.caseMap = new HashMap<Object, CaseEntry>();
    }

    public SwitchStatmentPart setSwitchExpr(Expression switchExpr, int line, int column) {
        this.switchExpr = switchExpr;
        return this;
    }

    public SwitchStatmentPart appendCaseStatment(Object key, Statment body, int line, int column) {
        body = StatmentUtil.optimize(body);
        CaseEntry current = currentCaseStatment;
        if (body != null) {
            current = currentCaseStatment = new CaseEntry(body, current);
        } // else use last as current for this key

        if (key == null) {
            if (defaultStatment != null) {
                throw new ParseException("multi default block in one swith", line, column);
            }
            defaultStatment = current;
        } else if (caseMap.containsKey(key)) {
            throw new ParseException("duplicated case value in one swith", line, column);
        }

        caseMap.put(key, current);
        return this;
    }

    public Statment pop() {
        return pop(0);  //default label is zero;
    }

    public Statment pop(int label) {

        Map<Object, CaseEntry> newCaseMap = new HashMap<Object, CaseEntry>((caseMap.size() + 1) * 4 / 3, 0.75f);

        newCaseMap.putAll(caseMap);

        return StatmentUtil.optimize(new SwitchStatment(switchExpr, defaultStatment, newCaseMap, label, line, column));
    }
}
