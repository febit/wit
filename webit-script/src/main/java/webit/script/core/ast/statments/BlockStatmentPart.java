// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.core.ast.statments;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import webit.script.core.ast.Statment;
import webit.script.core.ast.StatmentPart;
import webit.script.core.ast.loop.LoopInfo;
import webit.script.core.runtime.variant.VariantMap;
import webit.script.core.runtime.variant.VariantUtil;
import webit.script.util.StatmentUtil;

/**
 *
 * @author Zqq
 */
public final class BlockStatmentPart extends StatmentPart {

    private List<Statment> statmentList;
    private Map<String, Integer> varMap;

    public BlockStatmentPart(int line, int column) {
        super(line, column);
        this.statmentList = new LinkedList<Statment>();
    }

    public BlockStatmentPart setVarMap(Map<String, Integer> varMap) {
        this.varMap = varMap;
        return this;
    }

    public BlockStatmentPart append(Statment stat) {
        stat = StatmentUtil.optimize(stat);
        if (stat != null) {
            statmentList.add(stat);
        }
        return this;
    }

    public BlockStatment pop() {
        if (statmentList.isEmpty()) {
            return new EmptyBlockStatment(line, column);
        }
        Statment[] statments = statmentList.toArray(new Statment[statmentList.size()]);
        VariantMap variantMap = VariantUtil.toVariantMap(varMap);

        List<LoopInfo> loopInfos = StatmentUtil.collectPossibleLoopsInfo(statments);
        if (loopInfos == null || loopInfos.isEmpty()) {
            return new BlockStatmentNoLoops(variantMap, statments, line, column);
        } else {
            return new BlockStatmentWithLoops(variantMap, statments, line, column,
                    loopInfos.toArray(new LoopInfo[loopInfos.size()]));
        }
    }
}
