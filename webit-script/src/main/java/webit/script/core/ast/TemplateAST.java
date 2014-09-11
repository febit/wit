// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script.core.ast;

import webit.script.Context;
import webit.script.core.VariantIndexer;
import webit.script.util.StatementUtil;

/**
 *
 * @author Zqq
 */
public final class TemplateAST {

    private final Statement[] statements;
    private final VariantIndexer[] indexers;
    private final int varSize;

    public TemplateAST(VariantIndexer[] indexers, Statement[] statements, int varSize) {
        this.indexers = indexers;
        this.statements = statements;
        this.varSize = varSize;
    }

    public Context execute(final Context context) {
        context.indexers = this.indexers;
        context.indexer = 0;
        context.vars = new Object[varSize];
        context.pushRootParams();
        StatementUtil.executeInverted(this.statements, context);
        //assert context.indexer = 0
        return context;
    }
}
