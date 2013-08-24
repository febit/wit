/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package webit.script.core.ast.statments;

import java.util.ArrayList;
import java.util.List;
import webit.script.core.ast.Expression;
import webit.script.core.ast.StatmentPart;
import webit.script.core.ast.expressions.ContextValue;

/**
 *
 * @author Zqq
 */
public class ImportStatmentPart extends StatmentPart {

    private Expression expr;
    private Expression paramsExpr;
    private List<String> exportNameList;
    private List<ContextValue> toContextValueList;

    public ImportStatmentPart(Expression expr, int line, int column) {
        super(line, column);
        this.expr = expr;
        exportNameList = new ArrayList<String>();
        toContextValueList = new ArrayList<ContextValue>();
    }

    public ImportStatmentPart setParamsExpr(Expression paramsExpr) {
        this.paramsExpr = paramsExpr;
        return this;
    }

    public ImportStatmentPart append(String name, ContextValue to) {
        exportNameList.add(name);
        toContextValueList.add(to);
        return this;
    }

    public ImportStatment pop() {
        if (exportNameList.isEmpty()) {
            return new ImportStatment(expr, paramsExpr, null, null, line, column);
        } else {
            int len = exportNameList.size();
            String[] exportNames = new String[len];
            ContextValue[] toContextValues = new ContextValue[len];
            exportNameList.toArray(exportNames);
            toContextValueList.toArray(toContextValues);

            return new ImportStatment(expr, paramsExpr, exportNames, toContextValues, line, column);
        }
    }
}
