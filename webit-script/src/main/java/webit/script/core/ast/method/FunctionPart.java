// Copyright (c) 2013, Webit Team. All Rights Reserved.

package webit.script.core.ast.method;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import webit.script.core.ast.Statment;
import webit.script.core.ast.StatmentPart;
import webit.script.core.runtime.variant.VariantUtil;
import webit.script.util.StatmentUtil;

/**
 *
 * @author Zqq
 */
public final class FunctionPart extends StatmentPart {

    protected List<Integer> argIndexList; //with arguments at first
    private List<Statment> statmentList;
    private Map<String, Integer> varMap;
    private int[] overflowUpstairs;
    private int argsIndex;

    public FunctionPart setVarMap(Map<String, Integer> varMap) {
        this.varMap = varMap;
        return this;
    }

    public FunctionPart setArgsIndex(int argsIndex) {
        this.argsIndex = argsIndex;
        return this;
    }

    public FunctionPart setOverflowUpstairs(int[] overflowUpstairs) {
        this.overflowUpstairs = overflowUpstairs;
        return this;
    }

    public FunctionPart append(Statment stat) {

        stat = StatmentUtil.optimize(stat);

        if (stat != null) {
            statmentList.add(stat);
        }
        return this;
    }

    public FunctionPart(int line, int column) {
        super(line, column);
        this.statmentList = new ArrayList();
        this.argIndexList = new ArrayList<Integer>();
    }

    public FunctionPart appendArgIndexs(int index) {
        argIndexList.add(index);
        return this;
    }

    @Override
    public Function pop() {
        //TODO: 待优化
        int[] argIndexs = new int[argIndexList.size()];
        for (int i = 0; i < argIndexs.length; i++) {
            argIndexs[i] = argIndexList.get(i);
        }
        Statment[] statments = new Statment[statmentList.size()];
        statmentList.toArray(statments);
        return new Function(argsIndex, argIndexs, overflowUpstairs, VariantUtil.toVariantMap(varMap), statments, line, column);
    }
}
