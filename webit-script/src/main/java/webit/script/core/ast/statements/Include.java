// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script.core.ast.statements;

import webit.script.Context;
import webit.script.Template;
import webit.script.core.ast.Expression;

/**
 *
 * @author Zqq
 */
public final class Include extends AbstractInclude {

    public Include(Expression templateNameExpr, Expression paramsExpr, Template template, int line, int column) {
        super(templateNameExpr, paramsExpr, template, line, column);
    }

    @Override
    public Object execute(final Context context) {
        mergeTemplate(context, false);
        return null;
    }
}
