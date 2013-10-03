// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.core.ast.method;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import webit.script.core.ast.Position;
import webit.script.core.ast.Statment;
import webit.script.core.ast.loop.LoopInfo;
import webit.script.core.ast.loop.LoopType;
import webit.script.exceptions.ParseException;
import webit.script.util.StatmentUtil;
import webit.script.util.StringUtil;
import webit.script.util.VariantUtil;
import webit.script.util.collection.IntArrayList;

/**
 *
 * @author Zqq
 */
public final class FunctionPart extends Position {

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
        Statment[] statments = statmentList.toArray(new Statment[statmentList.size()]);

        boolean hasReturnLoops = false;
        List<LoopInfo> loopInfos = StatmentUtil.collectPossibleLoopsInfo(statments);

        if (loopInfos != null) {

            for (Iterator<LoopInfo> it = loopInfos.iterator(); it.hasNext();) {
                LoopInfo loopInfo = it.next();
                if (loopInfo.type == LoopType.RETURN) {
                    hasReturnLoops = true;
                    it.remove();
                }
            }
            if (loopInfos.size() > 0) {
                throw new ParseException("Loops overflow in function body: " + StringUtil.join(loopInfos, ","));
            }
        }

        return new Function(argsIndex,
                argIndexList.isEmpty() ? null : argIndexList.toArray(),
                overflowUpstairs != null && overflowUpstairs.length != 0 ? overflowUpstairs : null,
                VariantUtil.toVariantMap(varMap),
                statments,
                hasReturnLoops,
                line, column);
    }
}
