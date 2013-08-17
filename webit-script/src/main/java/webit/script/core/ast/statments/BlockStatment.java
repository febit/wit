// Copyright (c) 2013, Webit Team. All Rights Reserved.

package webit.script.core.ast.statments;

import webit.script.Context;
import webit.script.core.ast.AbstractStatment;
import webit.script.core.ast.Optimizable;
import webit.script.core.ast.Statment;
import webit.script.core.runtime.LoopCtrl;
import webit.script.core.runtime.VariantStack;
import webit.script.core.runtime.variant.VariantMap;
import webit.script.util.StatmentUtil;

/**
 *
 * @author Zqq
 */
public final class BlockStatment extends AbstractStatment implements Optimizable {

    private final VariantMap varMap;
    private final Statment[] statments;

    public BlockStatment(VariantMap varMap, Statment[] statments, int line, int column) {
        super(line, column);
        this.varMap = varMap;
        this.statments = statments;
    }

    @Override
    public void execute(Context context) {
        execute(context, null, null);
    }

    public void execute(Context context, int[] indexs, Object[] values) {
        if (statments != null) {
            VariantStack vars = context.vars;
            vars.push(varMap);
            if (indexs != null) {
                vars.set(indexs, values);
            }
            int len = statments.length;
            LoopCtrl ctrl = context.loopCtrl;
            for (int i = 0; i < len && ctrl.goon(); i++) {
                StatmentUtil.execute(statments[i], context);
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
}
