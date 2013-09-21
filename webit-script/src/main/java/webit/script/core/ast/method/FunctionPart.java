// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.core.ast.method;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import webit.script.core.ast.Statment;
import webit.script.core.ast.StatmentPart;
import webit.script.core.runtime.variant.VariantUtil;
import webit.script.util.StatmentUtil;
import webit.script.util.collection.IntArrayList;

/**
 *
 * @author Zqq
 */
public final class FunctionPart extends StatmentPart {

    private IntArrayList argIndexList; //with arguments at first
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
        this.statmentList = new LinkedList<Statment>();
        this.argIndexList = new IntArrayList(6);
    }

    public FunctionPart appendArgIndexs(int index) {
        this.argIndexList.add(index);
        return this;
    }

    public Function pop() {
        return new Function(argsIndex,
                argIndexList.isEmpty() ? null : argIndexList.toArray(),
                overflowUpstairs,
                VariantUtil.toVariantMap(varMap),
                statmentList.toArray(new Statment[statmentList.size()]),
                line, column);
    }
}
