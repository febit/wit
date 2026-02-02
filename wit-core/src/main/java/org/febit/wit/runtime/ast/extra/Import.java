// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.runtime.ast.extra;

import org.febit.wit.runtime.InternalContext;
import org.febit.wit.runtime.ast.AssignableExpression;
import org.febit.wit.runtime.ast.Expression;
import org.febit.wit.runtime.ast.Position;
import org.jspecify.annotations.Nullable;

public final class Import extends AbstractInclude {

    private final String[] exportVars;
    private final AssignableExpression @Nullable [] targets;
    private final boolean exportAll;

    public Import(
            Expression pathExpr,
            @Nullable Expression paramsExpr,
            String @Nullable [] exportVars,
            AssignableExpression @Nullable [] targets,
            String refer,
            Position position
    ) {
        super(pathExpr, paramsExpr, refer, position);
        if (exportVars == null || exportVars.length == 0) {
            this.exportVars = new String[0];
            this.targets = null;
            this.exportAll = true;
        } else {
            this.exportVars = exportVars;
            this.targets = targets;
            this.exportAll = false;
        }
    }

    @Override
    @Nullable
    @SuppressWarnings("UnnecessaryLocalVariable")
    public Object execute(InternalContext context) {
        var results = mergeScript(context, true);
        if (exportAll) {
            results.forEach(context.heap()::set);
            return null;
        }
        if (this.targets != null) {
            var names = this.exportVars;
            var len = names.length;
            var assignables = this.targets;
            for (int i = 0; i < len; i++) {
                assignables[i].set(context, results.get(names[i]));
            }
        }
        return null;
    }
}
