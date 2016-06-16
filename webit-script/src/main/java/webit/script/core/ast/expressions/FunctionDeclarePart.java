// Copyright (c) 2013-2015, Webit Team. All Rights Reserved.
package webit.script.core.ast.expressions;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import webit.script.core.LoopInfo;
import webit.script.core.Parser;
import webit.script.core.VariantIndexer;
import webit.script.core.VariantManager;
import webit.script.core.ast.Expression;
import webit.script.core.ast.Statement;
import webit.script.core.ast.operators.Assign;
import webit.script.core.ast.statements.Return;
import webit.script.exceptions.ParseException;
import webit.script.util.StatementUtil;
import webit.script.util.StringUtil;

/**
 *
 * @author zqq90
 */
public class FunctionDeclarePart {

    protected final int line;
    protected final int column;
    private int argsCount = 0;
    private final int assignToIndex;
    private final int start;
    private final VariantManager varmgr;
    private final List<String> args;

    public FunctionDeclarePart(String assignTo, VariantManager varmgr, int line, int column) {
        this(varmgr.assignVariant(assignTo, line, column), varmgr, line, column);
    }

    public FunctionDeclarePart(VariantManager varmgr, int line, int column) {
        this(-1, varmgr, line, column);
    }

    protected FunctionDeclarePart(int assignToIndex, VariantManager varmgr, int line, int column) {
        this.line = line;
        this.column = column;
        this.varmgr = varmgr;
        this.assignToIndex = assignToIndex;
        this.args = new ArrayList<>();
        varmgr.pushScope();
        start = varmgr.assignVariant("arguments", line, column);
    }

    public FunctionDeclarePart appendArgs(List<String> names) {
        if (names != null) {
            for (String name : names) {
                appendArg(name);
            }
        }
        return this;
    }

    public FunctionDeclarePart appendArg(String name) {
        if (varmgr.assignVariant(name, line, column) != (start + (++this.argsCount))) {
            throw new ParseException("Failed to assign vars!");
        }
        this.args.add(name);
        return this;
    }

    public String getArg(int index) {
        return args.get(index);
    }

    public Expression pop(Expression expr) {
        return pop(toStatementList(expr));
    }

    public FunctionDeclare popFunctionDeclare(Expression expr) {
        return popFunctionDeclare(toStatementList(expr));
    }

    private static List<Statement> toStatementList(Expression expr) {
        List<Statement> list = new ArrayList<>(1);
        list.add(new Return(expr, expr.line, expr.column));
        return list;
    }

    public Expression pop(List<Statement> list) {
        final Expression expr = popFunctionDeclare(list);
        if (this.assignToIndex >= 0) {
            return new Assign(new ContextValue(this.assignToIndex, line, column), expr, line, column);
        }
        return expr;
    }

    public FunctionDeclare popFunctionDeclare(List<Statement> list) {
        return popFunctionDeclare(StatementUtil.toStatementInvertArray(list));
    }

    protected FunctionDeclare popFunctionDeclare(Statement[] invertedStatements) {

        VariantIndexer[] indexers = varmgr.getIndexers();
        int varSize = varmgr.getVarCount();
        varmgr.popScope();
        boolean hasReturnLoops = false;
        List<LoopInfo> loopInfos = StatementUtil.collectPossibleLoopsInfo(invertedStatements);
        if (loopInfos != null) {
            for (Iterator<LoopInfo> it = loopInfos.iterator(); it.hasNext();) {
                LoopInfo loopInfo = it.next();
                if (loopInfo.type == LoopInfo.RETURN) {
                    hasReturnLoops = true;
                    it.remove();
                }
            }
            if (!loopInfos.isEmpty()) {
                throw new ParseException("Loops overflow in function body: ".concat(StringUtil.join(loopInfos, ',')));
            }
        }

        return new FunctionDeclare(argsCount,
                varSize,
                indexers,
                invertedStatements,
                start,
                hasReturnLoops,
                line, column);
    }
}
