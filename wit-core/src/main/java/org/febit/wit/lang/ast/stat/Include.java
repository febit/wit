// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.lang.ast.stat;

import jakarta.annotation.Nullable;
import org.febit.wit.InternalContext;
import org.febit.wit.lang.Position;
import org.febit.wit.lang.ast.Expression;
import org.febit.wit.lang.extra.ast.AbstractInclude;

/**
 * @author zqq90
 */
public final class Include extends AbstractInclude {

    public Include(Expression pathExpr, Expression paramsExpr, String refer, Position position) {
        super(pathExpr, paramsExpr, refer, position);
    }

    @Override
    @Nullable
    public Object execute(final InternalContext context) {
        mergeTemplate(context, false);
        return null;
    }
}
