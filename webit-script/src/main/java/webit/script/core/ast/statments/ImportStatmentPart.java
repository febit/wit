// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.core.ast.statments;

import java.util.LinkedList;
import java.util.List;
import webit.script.core.ast.Expression;
import webit.script.core.ast.Position;
import webit.script.core.ast.expressions.ContextValue;

/**
 *
 * @author Zqq
 */
public class ImportStatmentPart extends Position {

    private Expression expr;
    private Expression paramsExpr;
    private List<String> exportNameList;
    private List<ContextValue> toContextValueList;

    public ImportStatmentPart(Expression expr, int line, int column) {
        super(line, column);
        this.expr = expr;
        this.exportNameList = new LinkedList<String>();
        this.toContextValueList = new LinkedList<ContextValue>();
    }

    public ImportStatmentPart setParamsExpr(Expression paramsExpr) {
        this.paramsExpr = paramsExpr;
        return this;
    }

    public ImportStatmentPart append(String name, ContextValue to) {
        this.exportNameList.add(name);
        this.toContextValueList.add(to);
        return this;
    }

    public ImportStatment pop() {

        final int len = exportNameList.size();
        return len == 0
                ? new ImportStatment(expr, paramsExpr, null, null, line, column)
                : new ImportStatment(expr, paramsExpr,
                exportNameList.toArray(new String[len]),
                toContextValueList.toArray(new ContextValue[len]),
                line, column);
    }
}
