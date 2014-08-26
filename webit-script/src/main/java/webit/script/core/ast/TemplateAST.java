// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script.core.ast;

import webit.script.Context;
import webit.script.core.VariantIndexer;
import webit.script.util.StatementUtil;

/**
 *
 * @author Zqq
 */
public class TemplateAST {

    private final VariantIndexer varIndexer;
    private final Statement[] statements;

    public TemplateAST(VariantIndexer varIndexer, Statement[] statements) {
        this.varIndexer = varIndexer;
        this.statements = statements;
    }

    public Context execute(final Context context) {
        context.pushWithRootParams(this.varIndexer);
        StatementUtil.executeInverted(this.statements, context);
        //Note: don't vars.pop(), to keep the top vars(s)
        return context;
    }
}
