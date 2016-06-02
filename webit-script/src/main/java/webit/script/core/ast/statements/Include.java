// Copyright (c) 2013-2015, Webit Team. All Rights Reserved.
package webit.script.core.ast.statements;

import webit.script.InternalContext;
import webit.script.Template;
import webit.script.core.ast.Expression;

/**
 *
 * @author zqq90
 */
public final class Include extends AbstractInclude {

    public Include(Expression templateNameExpr, Expression paramsExpr, Template template, int line, int column) {
        super(templateNameExpr, paramsExpr, template, line, column);
    }

    @Override
    public Object execute(final InternalContext context) {
        mergeTemplate(context, false);
        return null;
    }
}
