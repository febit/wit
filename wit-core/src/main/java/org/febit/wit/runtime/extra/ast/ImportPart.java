// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.runtime.extra.ast;

import org.febit.wit.Template;
import org.febit.wit.exceptions.ParseException;
import org.febit.wit.runtime.AstUtils;
import org.febit.wit.runtime.Position;
import org.febit.wit.runtime.ast.AssignableExpression;
import org.febit.wit.runtime.ast.Expression;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ImportPart {

    protected final Position position;
    private final Expression expr;
    @Nullable
    private final Expression paramsExpr;
    private final List<String> exportNameList = new ArrayList<>();
    private final List<AssignableExpression> toResetableValueList = new ArrayList<>();

    public ImportPart(Expression expr, @Nullable Expression paramsExpr, Position position) {
        this.position = position;
        this.expr = AstUtils.optimize(expr);
        this.paramsExpr = paramsExpr == null
                ? null : AstUtils.optimize(paramsExpr);
    }

    public ImportPart append(String name, Expression to) {
        to = AstUtils.optimize(to);
        if (!(to instanceof AssignableExpression)) {
            throw new ParseException("Need a assignable expression.", to.position());
        }
        this.exportNameList.add(name);
        this.toResetableValueList.add((AssignableExpression) to);
        return this;
    }

    public Import pop(Template template) {
        var refer = template.path();
        var len = exportNameList.size();
        return len == 0
                ? new Import(expr, paramsExpr, null, null, refer, position)
                : new Import(expr, paramsExpr,
                exportNameList.toArray(new String[len]),
                toResetableValueList.toArray(new AssignableExpression[len]),
                refer, position);
    }
}
