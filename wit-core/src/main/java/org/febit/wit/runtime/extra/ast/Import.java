// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.runtime.extra.ast;

import org.febit.wit.runtime.InternalContext;
import org.febit.wit.runtime.Position;
import org.febit.wit.runtime.ast.AssignableExpression;
import org.febit.wit.runtime.ast.Expression;
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
        var results = mergeScript(context, true);
        if (exportAll) {
            var targetIndexer = context.heap().currentIndexer();
            var targetHeap = context.heap();
            results.forEach((key, val) -> {
                int index = targetIndexer.lookup(key);
                if (index >= 0) {
                    targetHeap.set(index, val);
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
