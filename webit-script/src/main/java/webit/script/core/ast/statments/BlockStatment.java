// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.core.ast.statments;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import webit.script.Context;
import webit.script.core.ast.AbstractStatment;
import webit.script.core.ast.Optimizable;
import webit.script.core.ast.Statment;
import webit.script.core.ast.loop.LoopInfo;
import webit.script.core.ast.loop.Loopable;
import webit.script.core.runtime.variant.VariantMap;
import webit.script.core.runtime.variant.VariantStack;
import webit.script.util.StatmentUtil;

/**
 *
 * @author Zqq
 */
public final class BlockStatment extends AbstractStatment implements Loopable, Optimizable {

    public final VariantMap varMap;
    public final Statment[] statments;
    public final LoopInfo[] possibleLoopsInfo;

    public BlockStatment(VariantMap varMap, Statment[] statments, LoopInfo[] possibleLoopsInfo, int line, int column) {
        super(line, column);
        this.varMap = varMap;
        this.statments = statments;
        this.possibleLoopsInfo = possibleLoopsInfo;
    }

    public Object execute(final Context context) {
        final VariantStack vars;
        (vars = context.vars).push(varMap);
        StatmentUtil.executeAndCheckLoops(statments, context);
        vars.pop();
        return null;
    }

    public void execute(final Context context, final int[] indexs, final Object[] values) {
        final VariantStack vars;
        (vars = context.vars).push(varMap);
        vars.set(indexs, values);
        StatmentUtil.executeAndCheckLoops(statments, context);
        vars.pop();
    }

    public List<LoopInfo> collectPossibleLoopsInfo() {
        return possibleLoopsInfo != null ? new LinkedList<LoopInfo>(Arrays.asList(possibleLoopsInfo)) : null;
    }

    public boolean hasLoops() {
        return possibleLoopsInfo != null && possibleLoopsInfo.length > 0;
    }

    public Statment optimize() throws Throwable {
        if (statments.length == 0) {
            return null;
        } else if (possibleLoopsInfo == null || possibleLoopsInfo.length == 0) {
            return new BlockStatmentNoLoops(varMap, statments, line, column);
        } else {
            return this;
        }
    }
}
