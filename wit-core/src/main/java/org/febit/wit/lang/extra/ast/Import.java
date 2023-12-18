// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.lang.extra.ast;

import jakarta.annotation.Nullable;
import org.febit.wit.InternalContext;
import org.febit.wit.lang.Position;
import org.febit.wit.lang.VariantIndexer;
import org.febit.wit.lang.ast.AssignableExpression;
import org.febit.wit.lang.ast.Expression;

import java.util.Map;

/**
 * @author zqq90
 */
public final class Import extends AbstractInclude {

    private final String[] exportNames;
    private final AssignableExpression[] targetAssignables;
    private final boolean exportAll;

    public Import(Expression pathExpr, Expression paramsExpr, @Nullable String[] exportNames,
                  AssignableExpression[] toContextValues, String refer, Position position) {
        super(pathExpr, paramsExpr, refer, position);
        if (exportNames == null || exportNames.length == 0) {
            this.exportNames = null;
            this.targetAssignables = null;
            this.exportAll = true;
        } else {
            this.exportNames = exportNames;
            this.targetAssignables = toContextValues;
            this.exportAll = false;
        }
    }

    @Override
    @Nullable
    public Object execute(final InternalContext context) {
        final Map<String, Object> results = mergeTemplate(context, true);
        if (exportAll) {
            final VariantIndexer destIndexer = context.getCurrentIndexer();
            final Object[] destVars = context.vars;
            results.forEach((key, val) -> {
                int index = destIndexer.getCurrentIndex(key);
                if (index >= 0) {
                    destVars[index] = val;
                }
            });
        } else {
            final String[] names = this.exportNames;
            final int len = names.length;
            final AssignableExpression[] myToResetableValues = this.targetAssignables;
            for (int i = 0; i < len; i++) {
                myToResetableValues[i].setValue(context, results.get(names[i]));
            }
        }
        return null;
    }
}
