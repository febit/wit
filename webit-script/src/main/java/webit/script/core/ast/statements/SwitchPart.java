// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script.core.ast.statements;

import java.util.HashMap;
import java.util.Map;
import webit.script.core.ast.Expression;
import webit.script.core.ast.Statement;
import webit.script.core.ast.statements.Switch.CaseEntry;
import webit.script.exceptions.ParseException;
import webit.script.util.StatementUtil;

/**
 *
 * @author Zqq
 */
public final class SwitchPart {

    protected int line;
    protected int column;
    private Expression switchExpr;
    private CaseEntry defaultStatement;
    private final Map<Object, CaseEntry> caseMap;
    private CaseEntry currentCaseStatement;

    public SwitchPart() {
        this.currentCaseStatement = null;
        this.caseMap = new HashMap<>();
    }

    public SwitchPart setSwitchExpr(Expression switchExpr, int line, int column) {
        this.switchExpr = switchExpr;
        return this;
    }

    public SwitchPart appendCase(Object key, Statement body, int line, int column) {
        body = StatementUtil.optimize(body);
        CaseEntry current = currentCaseStatement;
        if (body != null) {
            current = currentCaseStatement = new CaseEntry(body, current);
        } // else use last as current for this key

        if (key == null) {
            if (defaultStatement != null) {
                throw new ParseException("multi default block in one swith", line, column);
            }
            defaultStatement = current;
        } else if (caseMap.containsKey(key)) {
            throw new ParseException("duplicated case value in one swith", line, column);
        }

        caseMap.put(key, current);
        return this;
    }

    public Statement pop(int label) {

        Map<Object, CaseEntry> newCaseMap = new HashMap<>((caseMap.size() + 1) * 4 / 3, 0.75f);

        newCaseMap.putAll(caseMap);

        return StatementUtil.optimize(new Switch(switchExpr, defaultStatement, newCaseMap, label, line, column));
    }
}
