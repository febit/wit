// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.lang.ast.stat;

import org.febit.wit.exceptions.ParseException;
import org.febit.wit.lang.Position;
import org.febit.wit.lang.AstUtils;
import org.febit.wit.lang.ast.Expression;
import org.febit.wit.lang.ast.Statement;

import java.util.HashMap;
import java.util.Map;

/**
 * @author zqq90
 */
public class SwitchPart {

    private Position position;
    private Expression switchExpr;
    private Switch.CaseEntry defaultStatement;
    private final Map<Object, Switch.CaseEntry> caseMap;
    private Switch.CaseEntry currentCaseStatement;

    public SwitchPart() {
        this.currentCaseStatement = null;
        this.caseMap = new HashMap<>();
    }

    public SwitchPart setSwitchExpr(Expression switchExpr, Position position) {
        this.switchExpr = switchExpr;
        this.position = position;
        return this;
    }

    public SwitchPart appendCase(Object key, Statement body, Position position) {
        body = AstUtils.optimize(body);
        Switch.CaseEntry current = currentCaseStatement;
        if (body != null) {
            current = currentCaseStatement = new Switch.CaseEntry(body, current);
        }
        // else use last as current for this key
        if (key == null) {
            if (defaultStatement != null) {
                throw new ParseException("multi default block in one switch", position);
            }
            defaultStatement = current;
        } else if (caseMap.containsKey(key)) {
            throw new ParseException("duplicated case value in one switch", position);
        }

        caseMap.put(key, current);
        return this;
    }

    public Statement pop(int label) {
        Map<Object, Switch.CaseEntry> newCaseMap = new HashMap<>((caseMap.size() + 1) * 4 / 3, 0.75f);
        newCaseMap.putAll(caseMap);
        return AstUtils.optimize(new Switch(switchExpr, defaultStatement, newCaseMap, label, position));
    }
}
