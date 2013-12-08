// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.core.ast;

import webit.script.Context;
import webit.script.core.VariantIndexer;
import webit.script.util.StatmentUtil;

/**
 *
 * @author Zqq
 */
public class TemplateAST {

    private final VariantIndexer varIndexer;
    private final Statment[] statments;

    public TemplateAST(VariantIndexer varIndexer, Statment[] statments) {
        this.varIndexer = varIndexer;
        this.statments = statments;
    }

    public Context execute(final Context context) {
        context.pushRootVars(this.varIndexer);
        StatmentUtil.executeInverted(this.statments, context);
        //Note: don't vars.pop(), to keep the top variant(s)
        return context;
    }
}
