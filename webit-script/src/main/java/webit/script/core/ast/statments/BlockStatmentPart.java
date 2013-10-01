// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.core.ast.statments;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import webit.script.core.ast.Position;
import webit.script.core.ast.Statment;
import webit.script.core.ast.loop.LoopInfo;
import webit.script.core.runtime.variant.VariantUtil;
import webit.script.util.StatmentUtil;

/**
 *
 * @author Zqq
 */
public final class BlockStatmentPart extends Position {

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
        if ((stat = StatmentUtil.optimize(stat)) != null) {
            statmentList.add(stat);
        }
        return this;
    }

    public BlockStatment pop() {
        Statment[] statments;

        List<LoopInfo> loopInfos = StatmentUtil.collectPossibleLoopsInfo(
                statments = statmentList.toArray(new Statment[statmentList.size()]));

        return new BlockStatment(
                VariantUtil.toVariantMap(varMap),
                statments,
                loopInfos == null ? null : loopInfos.toArray(new LoopInfo[loopInfos.size()]),
                line, column);
    }
}
