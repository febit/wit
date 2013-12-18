// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.core.ast.statements;

import webit.script.Context;
import webit.script.core.VariantIndexer;
import webit.script.core.ast.AbstractStatement;
import webit.script.core.ast.Optimizable;
import webit.script.core.ast.Statement;
import webit.script.core.runtime.VariantStack;
import webit.script.util.StatementUtil;

/**
 *
 * @author Zqq
 */
public class BlockNoLoops extends AbstractStatement implements IBlock, Optimizable {

    private final VariantIndexer varIndexer;
    private final Statement[] statements;

    public BlockNoLoops(VariantIndexer varIndexer, Statement[] statements, int line, int column) {
        super(line, column);
        this.varIndexer = varIndexer;
        this.statements = statements;
    }

    public Object execute(final Context context) {
        final VariantStack vars;
        (vars = context.vars).push(varIndexer);
        StatementUtil.executeInverted(statements, context);
        vars.pop();
        return null;
    }

    public VariantIndexer getVarMap() {
        return varIndexer;
    }

    public Statement[] getStatements() {
        return statements;
    }

    public boolean hasLoops() {
        return false;
    }

    public Statement optimize(){
        return statements.length == 0 ? null : this;
    }
}
