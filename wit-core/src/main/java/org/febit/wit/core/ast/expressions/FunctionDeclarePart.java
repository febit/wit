// Copyright (c) 2013-2016, febit.org. All Rights Reserved.
package org.febit.wit.core.ast.expressions;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.febit.wit.core.LoopInfo;
import org.febit.wit.core.VariantIndexer;
import org.febit.wit.core.VariantManager;
import org.febit.wit.core.ast.Expression;
import org.febit.wit.core.ast.Statement;
import org.febit.wit.core.ast.operators.Assign;
import org.febit.wit.core.ast.statements.Return;
import org.febit.wit.exceptions.ParseException;
import org.febit.wit.util.StatementUtil;
import org.febit.wit.util.StringUtil;

/**
 *
 * @author zqq90
 */
public class FunctionDeclarePart {

    public static class ArgumentInfo {

        public final String name;
        public final Object defaultValue;

        public ArgumentInfo(String name, Object defaultValue) {
            this.name = name;
            this.defaultValue = defaultValue;
        }
    }

    protected final int line;
    protected final int column;
    private final int assignToIndex;
    private final int assignVariantStart;
    private final VariantManager varmgr;
    private final List<ArgumentInfo> args;

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
        assignVariantStart = varmgr.assignVariant("arguments", line, column);
    }

    public FunctionDeclarePart appendArgs(List<ArgumentInfo> infos) {
        if (infos != null) {
            for (ArgumentInfo name : infos) {
                appendArg(name);
            }
        }
        return this;
    }

    public FunctionDeclarePart appendArg(String name) {
        return appendArg(name, null);
    }

    public FunctionDeclarePart appendArg(String name, Object defaultValue) {
        return appendArg(new ArgumentInfo(name, defaultValue));
    }

    public FunctionDeclarePart appendArg(ArgumentInfo info) {
        if (varmgr.assignVariant(info.name, line, column) != (assignVariantStart + (this.args.size() + 1))) {
            throw new ParseException("Failed to assign vars!");
        }
        this.args.add(info);
        return this;
    }

    public String getArg(int index) {
        return args.get(index).name;
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
        
        Object[] argDefaults = new Object[this.args.size()];
        for (int i = 0; i < argDefaults.length; i++) {
            argDefaults[i] = this.args.get(i).defaultValue;
        }

        return new FunctionDeclare(argDefaults,
                varSize,
                indexers,
                invertedStatements,
                assignVariantStart,
                hasReturnLoops,
                line, column);
    }
}
