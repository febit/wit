// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.core.ast;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import webit.script.core.runtime.variant.VariantUtil;
import webit.script.util.StatmentUtil;

/**
 *
 * @author Zqq
 */
public class TemplatePart {

    protected final List<Statment> statmentList;
    private Map<String, Integer> varMap;

    public TemplatePart() {
        this.statmentList = new LinkedList<Statment>();
    }

    public TemplatePart setVarMap(Map<String, Integer> varMap) {
        this.varMap = varMap;
        return this;
    }

    public TemplatePart append(Statment stat) {
        
        stat = StatmentUtil.optimize(stat);
        
        if (stat != null) {
            statmentList.add(stat);
        }
        return this;
    }

    public TemplateAST pop() {
        Statment[] statments = new Statment[statmentList.size()];
        statmentList.toArray(statments);
        return new TemplateAST(VariantUtil.toVariantMap(varMap), statments);
    }
}
