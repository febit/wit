// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.parser;

import org.febit.wit.exception.ParseException;
import org.febit.wit.runtime.ast.AstUtils;
import org.febit.wit.runtime.ast.Expression;
import org.febit.wit.runtime.ast.LoopFlag;
import org.febit.wit.runtime.ast.Position;
import org.febit.wit.runtime.ast.Statement;
import org.febit.wit.runtime.ast.expr.ContextVar;
import org.febit.wit.runtime.ast.expr.FunctionDeclareExpr;
import org.febit.wit.runtime.ast.oper.Assign;
import org.febit.wit.runtime.ast.stat.Return;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class FunctionDeclareBuilder {

    private final Position position;
    private final int assignTarget;
    private final int argsIndexStart;
    private final VariantManager variants;

    private final List<ArgumentInfo> args = new ArrayList<>();

    public FunctionDeclareBuilder(String assignTarget, VariantManager variants, Position position) {
        this(variants, variants.assignVar(assignTarget, position), position);
    }

    public FunctionDeclareBuilder(VariantManager variants, Position position) {
        this(variants, -1, position);
    }

    public static FunctionDeclareBuilder create(VariantManager variants, Position position) {
        return new FunctionDeclareBuilder(variants, -1, position);
    }

    public static FunctionDeclareBuilder create(VariantManager variants, int assignTarget, Position position) {
        return new FunctionDeclareBuilder(variants, assignTarget, position);
    }

    private FunctionDeclareBuilder(VariantManager variants, int assignTarget, Position position) {
        this.variants = variants;
        this.position = position;
        this.assignTarget = assignTarget;

        variants.shiftPage();
        argsIndexStart = variants.assignVar("arguments", position);
    }

    public record ArgumentInfo(
            String name,
            @Nullable Object defaultValue
    ) {
    }

    public FunctionDeclareBuilder args(@Nullable List<ArgumentInfo> infos) {
        if (infos != null) {
            infos.forEach(this::arg);
        }
        return this;
    }

    public FunctionDeclareBuilder arg(String name) {
        return arg(name, null);
    }

    public FunctionDeclareBuilder arg(String name, @Nullable Object defaultValue) {
        return arg(new ArgumentInfo(name, defaultValue));
    }

    public FunctionDeclareBuilder arg(ArgumentInfo info) {
        if (variants.assignVar(info.name, position) != (argsIndexStart + (this.args.size() + 1))) {
            throw new ParseException("Failed to assign vars!");
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
            return new Assign(new ContextVar(this.assignTarget, position), expr, position);
        }
        return expr;
    }

    public FunctionDeclareExpr build(Expression lambda) {
        return build(lambdaBody(lambda));
    }

    public FunctionDeclareExpr build(List<Statement> list) {
        var statements = Ast.flatStatements(list);
        var indexers = variants.buildFrameIndexers();
        int frameSize = variants.frameSize();
        variants.unshiftPage();
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

        return new FunctionDeclareExpr(argDefaults,
                frameSize,
                indexers,
                statements,
                argsIndexStart,
                hasReturnLoops,
                position);
    }
}
