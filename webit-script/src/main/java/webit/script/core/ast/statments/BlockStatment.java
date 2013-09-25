// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.core.ast.statments;

import java.util.LinkedList;
import java.util.List;
import webit.script.Context;
import webit.script.core.ast.AbstractStatment;
import webit.script.core.ast.Optimizable;
import webit.script.core.ast.Statment;
import webit.script.core.ast.loop.LoopCtrl;
import webit.script.core.ast.loop.LoopInfo;
import webit.script.core.ast.loop.Loopable;
import webit.script.core.runtime.variant.VariantStack;
import webit.script.core.runtime.variant.VariantMap;
import webit.script.util.StatmentUtil;

/**
 *
 * @author Zqq
 */
public final class BlockStatment extends AbstractStatment implements Optimizable, Loopable {

    private final VariantMap varMap;
    private final Statment[] statments;
    private final List<LoopInfo> possibleLoopsInfo;

    public BlockStatment(VariantMap varMap, Statment[] statments, int line, int column) {
        super(line, column);
        this.varMap = varMap;
        this.statments = statments;
        this.possibleLoopsInfo = StatmentUtil.collectPossibleLoopsInfo(statments);
    }

    public Object execute(final Context context) {
        execute(context, null, null);
        return null;
    }

    public void execute(final Context context, final int[] indexs, final Object[] values) {
        if (statments != null) {
            final VariantStack vars = context.vars;
            vars.push(varMap);
            if (indexs != null) {
                vars.set(indexs, values);
            }
            final int len = statments.length;
            if (possibleLoopsInfo == null) {
                for (int i = 0; i < len; i++) {
                    StatmentUtil.execute(statments[i], context);
                }
            } else {
                final LoopCtrl ctrl = context.loopCtrl;
                for (int i = 0; i < len && ctrl.goon(); i++) {
                    StatmentUtil.execute(statments[i], context);
                }
            }
            vars.pop();
        }
    }

    public BlockStatment optimize() {
        if (statments != null && statments.length > 0) {
            return this;
        } else {
            return null;
        }
    }

    public List<LoopInfo> collectPossibleLoopsInfo() {
        return possibleLoopsInfo != null ? new LinkedList<LoopInfo>(possibleLoopsInfo) : null;
    }
}
