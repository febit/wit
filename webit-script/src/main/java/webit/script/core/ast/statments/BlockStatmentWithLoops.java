// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.core.ast.statments;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import webit.script.Context;
import webit.script.core.ast.AbstractStatment;
import webit.script.core.ast.Statment;
import webit.script.core.ast.loop.LoopCtrl;
import webit.script.core.ast.loop.LoopInfo;
import webit.script.core.ast.loop.Loopable;
import webit.script.core.runtime.variant.VariantMap;
import webit.script.core.runtime.variant.VariantStack;
import webit.script.util.StatmentUtil;

/**
 *
 * @author Zqq
 */
public class BlockStatmentWithLoops extends AbstractStatment implements BlockStatment, Loopable {

    private final VariantMap varMap;
    private final Statment[] statments;
    private final LoopInfo[] possibleLoopsInfo;

    public BlockStatmentWithLoops(VariantMap varMap, Statment[] statments, int line, int column, LoopInfo[] possibleLoopsInfo) {
        super(line, column);
        this.varMap = varMap;
        this.statments = statments;
        this.possibleLoopsInfo = possibleLoopsInfo;
    }

    public Object execute(final Context context) {
        final VariantStack vars = context.vars;
        vars.push(varMap);
        final int len = statments.length;
        final LoopCtrl ctrl = context.loopCtrl;
        for (int i = 0; i < len && ctrl.goon(); i++) {
            StatmentUtil.execute(statments[i], context);
        }
        vars.pop();
        return null;
    }

    public void execute(final Context context, final int[] indexs, final Object[] values) {
        final VariantStack vars = context.vars;
        vars.push(varMap);
        vars.set(indexs, values);
        final int len = statments.length;
        final LoopCtrl ctrl = context.loopCtrl;
        for (int i = 0; i < len && ctrl.goon(); i++) {
            StatmentUtil.execute(statments[i], context);
        }
        vars.pop();
    }

    public List<LoopInfo> collectPossibleLoopsInfo() {
        return new LinkedList<LoopInfo>(Arrays.asList(possibleLoopsInfo));
    }

    public boolean hasLoops() {
        return true;
    }
}
