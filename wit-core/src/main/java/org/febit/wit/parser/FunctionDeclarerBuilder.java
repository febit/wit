/*
 * Copyright 2013-present febit.org (support@febit.org)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.febit.wit.parser;

import org.febit.wit.Presets;
import org.febit.wit.exception.ParseException;
import org.febit.wit.runtime.ast.Expression;
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

        varLayout.shiftFrame();
        argsIndexStart = varLayout.assignVar(Presets.ARGUMENTS, position);
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
        var indexers = varLayout.buildScopedIndexers();
        int heapSize = varLayout.heapSize();
        varLayout.unshiftFrame();

        var batches = Ast.batch(list, ctrl -> {
            if (!ctrl.state().isReturn()) {
                throw new ParseException("flow control leaks from function body: " + ctrl.state(), ctrl.position());
            }
        });

        var argDefaults = new Object[this.args.size()];
        for (int i = 0; i < argDefaults.length; i++) {
            argDefaults[i] = this.args.get(i).defaultValue;
        }

        return new FunctionDeclarer(argDefaults,
                heapSize,
                indexers,
                batches,
                argsIndexStart,
                position);
    }
}
