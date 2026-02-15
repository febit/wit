// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.core;

import org.febit.wit.exception.ParseException;
import org.febit.wit.runtime.ast.AstUtils;
import org.febit.wit.runtime.ast.Expression;
import org.febit.wit.runtime.ast.Position;
import org.febit.wit.runtime.ast.Statement;
import org.febit.wit.runtime.ast.stat.Switch;
import org.jspecify.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class SwitchPart {
    @Nullable
    private Position position;
    @Nullable
    private Expression switchExpr;

    private Switch.@Nullable CaseEntry defaultStatement;
    private Switch.@Nullable CaseEntry currentCaseStatement;

    private final Map<@Nullable Object, Switch.CaseEntry> caseMap = new HashMap<>();

    public SwitchPart setSwitchExpr(Expression switchExpr, Position position) {
        this.switchExpr = switchExpr;
        this.position = position;
        return this;
    }

    public SwitchPart appendCase(@Nullable Object key, Statement body, Position position) {
        body = AstUtils.optimize(body);
        var current = new Switch.CaseEntry(body, currentCaseStatement);
        currentCaseStatement = current;
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
        Objects.requireNonNull(switchExpr);
        Objects.requireNonNull(position);

        Map<Object, Switch.CaseEntry> newCaseMap = new HashMap<>((caseMap.size() + 1) * 4 / 3, 0.75f);
        newCaseMap.putAll(caseMap);
        return AstUtils.optimize(new Switch(switchExpr, defaultStatement, newCaseMap, label, position));
    }
}
