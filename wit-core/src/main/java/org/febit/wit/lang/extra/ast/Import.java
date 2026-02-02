// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.lang.extra.ast;

import org.febit.wit.InternalContext;
import org.febit.wit.lang.Position;
import org.febit.wit.lang.ast.AssignableExpression;
import org.febit.wit.lang.ast.Expression;
import org.jspecify.annotations.Nullable;

public final class Import extends AbstractInclude {

    private final String[] exportNames;
    private final AssignableExpression @Nullable [] targetAssignables;
    private final boolean exportAll;

    public Import(
            Expression pathExpr,
            @Nullable Expression paramsExpr,
            String @Nullable [] exportNames,
            AssignableExpression @Nullable [] targetAssignables,
            String refer,
            Position position
    ) {
        super(pathExpr, paramsExpr, refer, position);
        if (exportNames == null || exportNames.length == 0) {
            this.exportNames = new String[0];
            this.targetAssignables = null;
            this.exportAll = true;
        } else {
            this.exportNames = exportNames;
            this.targetAssignables = targetAssignables;
            this.exportAll = false;
        }
    }

    @Override
    @Nullable
    @SuppressWarnings("UnnecessaryLocalVariable")
    public Object execute(InternalContext context) {
        var results = mergeTemplate(context, true);
        if (exportAll) {
            var destIndexer = context.getCurrentIndexer();
            var destVars = context.vars;
            results.forEach((key, val) -> {
                int index = destIndexer.getCurrentIndex(key);
                if (index >= 0) {
                    destVars[index] = val;
                }
            });
        } else if (this.targetAssignables != null) {
            var names = this.exportNames;
            var len = names.length;
            var assignables = this.targetAssignables;
            for (int i = 0; i < len; i++) {
                assignables[i].setValue(context, results.get(names[i]));
            }
        }
        return null;
    }
}
