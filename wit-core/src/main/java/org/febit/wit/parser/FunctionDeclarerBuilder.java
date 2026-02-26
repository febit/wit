// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.parser;

import org.febit.wit.exception.ParseException;
import org.febit.wit.runtime.ast.AstUtils;
import org.febit.wit.runtime.ast.Expression;
import org.febit.wit.runtime.ast.LoopFlag;
import org.febit.wit.runtime.ast.Position;
import org.febit.wit.runtime.ast.Statement;
import org.febit.wit.runtime.ast.expr.Assign;
import org.febit.wit.runtime.ast.expr.FunctionDeclarer;
import org.febit.wit.runtime.ast.expr.VariableHeapValue;
import org.febit.wit.runtime.ast.statement.Return;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class FunctionDeclarerBuilder {

    private final Position position;
    private final int assignTarget;
    private final int argsIndexStart;
    private final VarLayout varLayout;

    private final List<ArgumentInfo> args = new ArrayList<>();

    private FunctionDeclarerBuilder(VarLayout varLayout, int assignTarget, Position position) {
        this.varLayout = varLayout;
        this.position = position;
        this.assignTarget = assignTarget;

        varLayout.shiftLayer();
        argsIndexStart = varLayout.assignVar("arguments", position);
    }

    public static FunctionDeclarerBuilder create(VarLayout varLayout, Position position) {
        return new FunctionDeclarerBuilder(varLayout, -1, position);
    }

    public static FunctionDeclarerBuilder create(VarLayout varLayout, int assignTarget, Position position) {
        return new FunctionDeclarerBuilder(varLayout, assignTarget, position);
    }

    public record ArgumentInfo(
            String name,
            @Nullable Object defaultValue
    ) {
    }

    public FunctionDeclarerBuilder args(@Nullable List<ArgumentInfo> infos) {
        if (infos != null) {
            infos.forEach(this::arg);
        }
        return this;
    }

    public FunctionDeclarerBuilder arg(String name) {
        return arg(name, null);
    }

    public FunctionDeclarerBuilder arg(String name, @Nullable Object defaultValue) {
        return arg(new ArgumentInfo(name, defaultValue));
    }

    public FunctionDeclarerBuilder arg(ArgumentInfo info) {
        if (varLayout.assignVar(info.name, position) != (argsIndexStart + (this.args.size() + 1))) {
            throw new ParseException("Cannot assign argument variable: " + info.name);
        }
        this.args.add(info);
        return this;
    }

    public String getArg(int index) {
        return args.get(index).name;
    }

    private static List<Statement> lambdaBody(Expression lambda) {
        return List.of(
                new Return(lambda, lambda.position())
        );
    }

    public Expression buildAndAssign(Expression lambda) {
        return buildAndAssign(lambdaBody(lambda));
    }

    public Expression buildAndAssign(List<Statement> list) {
        var expr = build(list);
        if (this.assignTarget >= 0) {
            return new Assign(new VariableHeapValue(this.assignTarget, position), expr, position);
        }
        return expr;
    }

    public FunctionDeclarer build(Expression lambda) {
        return build(lambdaBody(lambda));
    }

    public FunctionDeclarer build(List<Statement> list) {
        var statements = Ast.flatStatements(list);
        var indexers = varLayout.buildFrameIndexers();
        int frameSize = varLayout.frameSize();
        varLayout.unshiftLayer();
        boolean hasReturnLoops = false;

        List<LoopFlag> overflowLoops = new ArrayList<>();
        for (var loop : AstUtils.collectLoopFlags(statements)) {
            if (loop.kind().isReturn()) {
                hasReturnLoops = true;
            } else {
                overflowLoops.add(loop);
            }
        }
        if (!overflowLoops.isEmpty()) {
            throw new ParseException("Loops overflow in function body: " + overflowLoops);
        }

        var argDefaults = new Object[this.args.size()];
        for (int i = 0; i < argDefaults.length; i++) {
            argDefaults[i] = this.args.get(i).defaultValue;
        }

        return new FunctionDeclarer(argDefaults,
                frameSize,
                indexers,
                statements,
                argsIndexStart,
                hasReturnLoops,
                position);
    }
}
