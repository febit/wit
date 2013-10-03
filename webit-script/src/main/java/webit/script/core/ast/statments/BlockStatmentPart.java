// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.core.ast.statments;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import webit.script.core.ast.Position;
import webit.script.core.ast.Statment;
import webit.script.core.ast.loop.LoopInfo;
import webit.script.util.StatmentUtil;
import webit.script.util.VariantUtil;

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

    public IBlockStatment pop() {
        Statment[] statments;

        List<LoopInfo> loopInfoList = StatmentUtil.collectPossibleLoopsInfo(
                statments = statmentList.toArray(new Statment[statmentList.size()]));

        LoopInfo[] loopInfos = loopInfoList != null
                ? loopInfoList.toArray(new LoopInfo[loopInfoList.size()])
                : null;

        return loopInfos != null
                ? new BlockStatment(VariantUtil.toVariantMap(varMap), statments, loopInfos, line, column)
                : new BlockStatmentNoLoops(VariantUtil.toVariantMap(varMap), statments, line, column);
    }
}
