// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.lang.extra.ast;

import org.febit.wit.Template;
import org.febit.wit.exceptions.ParseException;
import org.febit.wit.lang.AstUtils;
import org.febit.wit.lang.Position;
import org.febit.wit.lang.ast.AssignableExpression;
import org.febit.wit.lang.ast.Expression;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zqq90
 */
public class ImportPart {

    protected final Position position;
    private Expression expr;
    private Expression paramsExpr;
    private List<String> exportNameList;
    private List<AssignableExpression> toResetableValueList;

    public ImportPart(Expression expr, Expression paramsExpr, Position position) {
        this.position = position;
        this.expr = AstUtils.optimize(expr);
        this.paramsExpr = AstUtils.optimize(paramsExpr);
        this.exportNameList = new ArrayList<>();
        this.toResetableValueList = new ArrayList<>();
    }

    public ImportPart append(String name, Expression to) {
        to = AstUtils.optimize(to);
        if (!(to instanceof AssignableExpression)) {
            throw new ParseException("Need a assignable expression.", to.getPosition());
        }
        this.exportNameList.add(name);
        this.toResetableValueList.add((AssignableExpression) to);
        return this;
    }

    public Import pop(Template template) {
        final String refer = template.getName();
        final int len = exportNameList.size();
        return len == 0
                ? new Import(expr, paramsExpr, null, null, refer, position)
                : new Import(expr, paramsExpr,
                exportNameList.toArray(new String[len]),
                toResetableValueList.toArray(new AssignableExpression[len]),
                refer, position);
    }
}
