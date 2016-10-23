// Copyright (c) 2013-2016, febit.org. All Rights Reserved.
package org.febit.wit.core.ast.statements;

import org.febit.wit.InternalContext;
import org.febit.wit.Template;
import org.febit.wit.core.ast.Expression;

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
