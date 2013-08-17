// Copyright (c) 2013, Webit Team. All Rights Reserved.

package webit.script.core.ast.statments;

import java.util.HashMap;
import java.util.Map;
import webit.script.core.ast.Expression;
import webit.script.core.ast.Statment;
import webit.script.core.ast.StatmentPart;
import webit.script.exceptions.ParserException;
import webit.script.util.StatmentUtil;

/**
 *
 * @author Zqq
 */
public final class SwitchStatmentPart extends StatmentPart {

    private Expression switchExpr;
    private CaseStatment defaultStatment;
    private Map<Object, CaseStatment> caseMap;
    private CaseStatment currentCaseStatment;
    private String label;

    public SwitchStatmentPart() {
        this.caseMap = new HashMap<Object, CaseStatment>();
    }

    @Override
    public SwitchStatmentPart setPosition(int line, int column) {
        this.line = line;
        this.column = column;
        return this;
    }

    public SwitchStatmentPart setLabel(String label) {
        this.label = label;
        return this;
    }

    public SwitchStatmentPart setSwitchExpr(Expression switchExpr) {
        this.switchExpr = switchExpr;
        return this;
    }

    public SwitchStatmentPart appendCaseStatment(Object key, BlockStatment body, int line, int column) {
        
        body = body.optimize();
        
        currentCaseStatment = new CaseStatment(body, currentCaseStatment, line, column);
        if (key == null) {
            if (defaultStatment != null) {
                throw new ParserException("multi default block in one swith", line, column);
            }
            defaultStatment = currentCaseStatment;
        } else {
            if (!caseMap.containsKey(key)) {
                caseMap.put(key, currentCaseStatment);
            } else {
                throw new ParserException("duplicated case value in one swith", line, column);
            }
        }

        return this;
    }

    @Override
    public Statment pop() {
        
        if (defaultStatment.isBodyEmpty()) {
            defaultStatment = null;
        }
        
        Map<Object, CaseStatment> newCaseMap = new HashMap<Object, CaseStatment>((caseMap.size()+1)*4/3 ,0.75f);
        
        newCaseMap.putAll(caseMap);

        return StatmentUtil.optimize(new SwitchStatment(switchExpr, defaultStatment, newCaseMap, label, line, column));
    }
}
