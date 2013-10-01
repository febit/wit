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
public class BlockStatmentNoLoops extends AbstractStatment {

    private final VariantMap varMap;
    private final Statment[] statments;

    public BlockStatmentNoLoops(VariantMap varMap, Statment[] statments, int line, int column) {
        super(line, column);
        this.varMap = varMap;
        this.statments = statments;
    }

    public Object execute(final Context context) {
        final VariantStack vars;
        (vars = context.vars).push(varMap);
        StatmentUtil.execute(statments, context);
        vars.pop();
        return null;
    }
}
