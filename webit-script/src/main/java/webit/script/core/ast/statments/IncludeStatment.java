// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.core.ast.statments;

import webit.script.Context;
import webit.script.Template;
import webit.script.core.ast.Expression;

/**
 *
 * @author Zqq
 */
public final class IncludeStatment extends AbstractIncludeStatment {

    public IncludeStatment(Expression templateNameExpr, Expression paramsExpr, Template template, int line, int column) {
        super(templateNameExpr, paramsExpr, template, line, column);
    }

    public Object execute(final Context context) {
        mergeTemplate(context);
        return null;
    }
}
