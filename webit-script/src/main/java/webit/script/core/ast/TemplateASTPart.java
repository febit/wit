// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.core.ast;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import webit.script.core.ast.loop.LoopInfo;
import webit.script.exceptions.ParseException;
import webit.script.util.ArrayUtil;
import webit.script.util.StatmentUtil;
import webit.script.util.StringUtil;
import webit.script.util.VariantUtil;

/**
 *
 * @author Zqq
 */
public class TemplateASTPart {

    protected final List<Statment> statmentList;
    private Map<String, Integer> varMap;

    public TemplateASTPart() {
        this.statmentList = new LinkedList<Statment>();
    }

    public TemplateASTPart setVarMap(Map<String, Integer> varMap) {
        this.varMap = varMap;
        return this;
    }

    public TemplateASTPart append(Statment stat) {

        stat = StatmentUtil.optimize(stat);

        if (stat != null) {
            statmentList.add(stat);
        }
        return this;
    }

    public TemplateAST pop() {
        Statment[] statments =
                statmentList.toArray(new Statment[statmentList.size()]);

        ArrayUtil.invert(statments);

        List<LoopInfo> loopInfos = StatmentUtil.collectPossibleLoopsInfo(statments);
        if (loopInfos != null) {
            throw new ParseException("loop overflow: " + StringUtil.join(loopInfos, ","));
        }

        return new TemplateAST(VariantUtil.toVariantMap(varMap), statments);
    }
}
