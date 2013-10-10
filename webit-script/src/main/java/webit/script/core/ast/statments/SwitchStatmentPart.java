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
    private Map<Object, CaseEntry> caseMap;
    private CaseEntry currentCaseStatment = null;
    private int label = 0;

    public SwitchStatmentPart() {
        this.caseMap = new HashMap<Object, CaseEntry>();
    }

    @Override
    public SwitchStatmentPart setPosition(int line, int column) {
        this.line = line;
        this.column = column;
        return this;
    }

    public SwitchStatmentPart setLabel(int label) {
        this.label = label;
        return this;
    }

    public SwitchStatmentPart setSwitchExpr(Expression switchExpr) {
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

        Map<Object, CaseEntry> newCaseMap = new HashMap<Object, CaseEntry>((caseMap.size() + 1) * 4 / 3, 0.75f);

        newCaseMap.putAll(caseMap);

        return StatmentUtil.optimize(new SwitchStatment(switchExpr, defaultStatment, newCaseMap, label, line, column));
    }
}
