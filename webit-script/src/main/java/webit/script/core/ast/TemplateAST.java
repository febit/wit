// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.core.ast;

import webit.script.Context;
import webit.script.core.runtime.variant.VariantMap;
import webit.script.util.StatmentUtil;

/**
 *
 * @author Zqq
 */
public class TemplateAST {

    private final VariantMap varMap;
    private final Statment[] statments;

    public TemplateAST(VariantMap varMap, Statment[] statments) {
        this.varMap = varMap;
        this.statments = statments;
    }

    public Context execute(final Context context) {
        context.pushRootVars(this.varMap);
        StatmentUtil.executeInverted(this.statments, context);
        //Note: don't vars.pop(), to keep the top variant(s)
        return context;
    }
}
