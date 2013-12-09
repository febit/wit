// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.core.ast.statments;

import java.util.LinkedList;
import java.util.List;
import webit.script.Template;
import webit.script.core.ast.Expression;
import webit.script.core.ast.Position;
import webit.script.core.ast.ResetableValueExpression;
import webit.script.exceptions.ParseException;

/**
 *
 * @author Zqq
 */
public class ImportStatmentPart extends Position {

    private Expression expr;
    private Expression paramsExpr;
    private List<String> exportNameList;
    private List<ResetableValueExpression> toResetableValueList;

    public ImportStatmentPart(Expression expr, int line, int column) {
        this(expr, null, line, column);
    }

    public ImportStatmentPart(Expression expr, Expression paramsExpr, int line, int column) {
        super(line, column);
        this.expr = expr;
        this.paramsExpr = paramsExpr;
        this.exportNameList = new LinkedList<String>();
        this.toResetableValueList = new LinkedList<ResetableValueExpression>();
    }

    public ImportStatmentPart append(String name, Expression to) {
        if (to instanceof ResetableValueExpression) {
            this.exportNameList.add(name);
            this.toResetableValueList.add((ResetableValueExpression) to);
            return this;
        } else {
            throw new ParseException("Need a resetable expression.", to);
        }
    }

    public ImportStatment pop(Template template) {

        final int len = exportNameList.size();
        return len == 0
                ? new ImportStatment(expr, paramsExpr, null, null, template, line, column)
                : new ImportStatment(expr, paramsExpr,
                        exportNameList.toArray(new String[len]),
                        toResetableValueList.toArray(new ResetableValueExpression[len]),
                        template,line, column);
    }
}
