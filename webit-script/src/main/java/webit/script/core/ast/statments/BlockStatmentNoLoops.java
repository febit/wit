// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.core.ast.statments;

import webit.script.Context;
import webit.script.core.VariantIndexer;
import webit.script.core.ast.AbstractStatment;
import webit.script.core.ast.Optimizable;
import webit.script.core.ast.Statment;
import webit.script.core.runtime.VariantStack;
import webit.script.util.StatmentUtil;

/**
 *
 * @author Zqq
 */
public class BlockStatmentNoLoops extends AbstractStatment implements IBlockStatment, Optimizable {

    private final VariantIndexer varIndexer;
    private final Statment[] statments;

    public BlockStatmentNoLoops(VariantIndexer varIndexer, Statment[] statments, int line, int column) {
        super(line, column);
        this.varIndexer = varIndexer;
        this.statments = statments;
    }

    public Object execute(final Context context) {
        final VariantStack vars;
        (vars = context.vars).push(varIndexer);
        StatmentUtil.executeInverted(statments, context);
        vars.pop();
        return null;
    }

    public VariantIndexer getVarMap() {
        return varIndexer;
    }

    public Statment[] getStatments() {
        return statments;
    }

    public boolean hasLoops() {
        return false;
    }

    public Statment optimize(){
        return statments.length == 0 ? null : this;
    }
}
