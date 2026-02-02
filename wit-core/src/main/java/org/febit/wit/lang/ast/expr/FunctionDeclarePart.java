// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.lang.ast.expr;

import org.febit.wit.core.VariantManager;
import org.febit.wit.exceptions.ParseException;
import org.febit.wit.lang.AstUtils;
import org.febit.wit.lang.LoopMeta;
import org.febit.wit.lang.Position;
import org.febit.wit.lang.ast.Expression;
import org.febit.wit.lang.ast.Statement;
import org.febit.wit.lang.ast.oper.Assign;
import org.febit.wit.lang.ast.stat.Return;
import org.febit.wit.util.StringUtils;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class FunctionDeclarePart {

    private final Position position;
    private final int assignToIndex;
    private final int assignVariantStart;
    private final VariantManager varmgr;
    private final List<ArgumentInfo> args;

    public FunctionDeclarePart(String assignTo, VariantManager varmgr, Position position) {
        this(varmgr.assignVariant(assignTo, position), varmgr, position);
    }

    public FunctionDeclarePart(VariantManager varmgr, Position position) {
        this(-1, varmgr, position);
    }

    protected FunctionDeclarePart(int assignToIndex, VariantManager varmgr, Position position) {
        this.position = position;
        this.varmgr = varmgr;
        this.assignToIndex = assignToIndex;
        this.args = new ArrayList<>();
        varmgr.pushScope();
        assignVariantStart = varmgr.assignVariant("arguments", position);
    }

    public record ArgumentInfo(
            String name,
            @Nullable Object defaultValue
    ) {
    }

    public FunctionDeclarePart appendArgs(@Nullable List<ArgumentInfo> infos) {
        if (infos != null) {
            infos.forEach(this::appendArg);
        }
        return this;
    }

    public FunctionDeclarePart appendArg(String name) {
        return appendArg(name, null);
    }

    public FunctionDeclarePart appendArg(String name, @Nullable Object defaultValue) {
        return appendArg(new ArgumentInfo(name, defaultValue));
    }

    public FunctionDeclarePart appendArg(ArgumentInfo info) {
        if (varmgr.assignVariant(info.name, position) != (assignVariantStart + (this.args.size() + 1))) {
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

    public FunctionDeclareExpr popFunctionDeclare(Expression expr) {
        return popFunctionDeclare(toStatementList(expr));
    }

    private static List<Statement> toStatementList(Expression expr) {
        List<Statement> list = new ArrayList<>(1);
        list.add(new Return(expr, expr.position()));
        return list;
    }

    public Expression pop(List<Statement> list) {
        final Expression expr = popFunctionDeclare(list);
        if (this.assignToIndex >= 0) {
            return new Assign(new ContextVar(this.assignToIndex, position), expr, position);
        }
        return expr;
    }

    public FunctionDeclareExpr popFunctionDeclare(List<Statement> list) {
        return popFunctionDeclare(AstUtils.toStatementArray(list));
    }

    protected FunctionDeclareExpr popFunctionDeclare(Statement[] statements) {
        var indexers = varmgr.getIndexers();
        int varSize = varmgr.getVarCount();
        varmgr.popScope();
        boolean hasReturnLoops = false;

        List<LoopMeta> overflowLoops = new ArrayList<>();
        for (var loop : AstUtils.collectPossibleLoops(statements)) {
            if (loop.kind().isReturn()) {
                hasReturnLoops = true;
            } else {
                overflowLoops.add(loop);
            }
        }
        if (!overflowLoops.isEmpty()) {
            throw new ParseException("Loops overflow in function body: "
                    + StringUtils.join(overflowLoops, ','));
        }

        @Nullable
        Object[] argDefaults = new Object[this.args.size()];
        for (int i = 0; i < argDefaults.length; i++) {
            argDefaults[i] = this.args.get(i).defaultValue;
        }

        return new FunctionDeclareExpr(argDefaults,
                varSize,
                indexers,
                statements,
                assignVariantStart,
                hasReturnLoops,
                position);
    }
}
