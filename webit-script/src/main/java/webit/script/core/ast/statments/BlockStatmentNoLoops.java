// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.core.ast.statments;

import webit.script.Context;
import webit.script.core.ast.AbstractStatment;
import webit.script.core.ast.Statment;
import webit.script.core.runtime.variant.VariantMap;
import webit.script.core.runtime.variant.VariantStack;
import webit.script.util.StatmentUtil;

/**
 *
 * @author Zqq
 */
public class BlockStatmentNoLoops extends AbstractStatment implements BlockStatment {

    private final VariantMap varMap;
    private final Statment[] statments;

    public BlockStatmentNoLoops(VariantMap varMap, Statment[] statments, int line, int column) {
        super(line, column);
        this.varMap = varMap;
        this.statments = statments;
    }

    public Object execute(final Context context) {
        final VariantStack vars = context.vars;
        vars.push(varMap);
        final int len = statments.length;
        for (int i = 0; i < len; i++) {
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
        for (int i = 0; i < len; i++) {
            StatmentUtil.execute(statments[i], context);
        }
        vars.pop();
    }

    public boolean hasLoops() {
        return false;
    }
}
