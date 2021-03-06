// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.core.ast.statements;

import org.febit.wit.Template;
import org.febit.wit.core.ast.AssignableExpression;
import org.febit.wit.core.ast.Expression;
import org.febit.wit.exceptions.ParseException;
import org.febit.wit.util.StatementUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zqq90
 */
public class ImportPart {

    protected final int line;
    protected final int column;
    private Expression expr;
    private Expression paramsExpr;
    private List<String> exportNameList;
    private List<AssignableExpression> toResetableValueList;

    public ImportPart(Expression expr, Expression paramsExpr, int line, int column) {
        this.line = line;
        this.column = column;
        this.expr = StatementUtil.optimize(expr);
        this.paramsExpr = StatementUtil.optimize(paramsExpr);
        this.exportNameList = new ArrayList<>();
        this.toResetableValueList = new ArrayList<>();
    }

    public ImportPart append(String name, Expression to) {
        to = StatementUtil.optimize(to);
        if (!(to instanceof AssignableExpression)) {
            throw new ParseException("Need a resetable expression.", to);
        }
        this.exportNameList.add(name);
        this.toResetableValueList.add((AssignableExpression) to);
        return this;
    }

    public Import pop(Template template) {
        final String refer = template.getName();
        final int len = exportNameList.size();
        return len == 0
                ? new Import(expr, paramsExpr, null, null, refer, line, column)
                : new Import(expr, paramsExpr,
                exportNameList.toArray(new String[len]),
                toResetableValueList.toArray(new AssignableExpression[len]),
                refer, line, column);
    }
}
