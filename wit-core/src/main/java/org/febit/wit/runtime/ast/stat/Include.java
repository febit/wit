// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.runtime.ast.stat;

import org.febit.wit.runtime.InternalContext;
import org.febit.wit.runtime.Position;
import org.febit.wit.runtime.ast.Expression;
import org.febit.wit.runtime.extra.ast.AbstractInclude;
import org.jspecify.annotations.Nullable;

public final class Include extends AbstractInclude {

    public Include(Expression pathExpr, Expression paramsExpr, String refer, Position position) {
        super(pathExpr, paramsExpr, refer, position);
    }

    @Override
    @Nullable
    public Object execute(InternalContext context) {
        mergeTemplate(context, false);
        return null;
    }
}
