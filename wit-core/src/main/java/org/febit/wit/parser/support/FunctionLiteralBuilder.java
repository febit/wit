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
package org.febit.wit.parser.support;

import org.febit.wit.exception.ScriptParseException;
import org.febit.wit.ir.Expression;
import org.febit.wit.ir.Position;
import org.febit.wit.ir.Statement;
import org.febit.wit.ir.expr.FunctionLiteral;
import org.febit.wit.ir.expr.VariableHeapValue;
import org.febit.wit.ir.flow.Return;
import org.febit.wit.ir.oper.Assign;
import org.febit.wit.parser.IR;
import org.febit.wit.parser.ReservedNames;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class FunctionLiteralBuilder {

    private final VarLayout varLayout;
    private final int assignTarget;
    private final int argsBeginSlot;
    private final Position position;

    private final List<Argument> args = new ArrayList<>();

    private FunctionLiteralBuilder(VarLayout varLayout, int assignTarget, Position position) {
        this.varLayout = varLayout;
        this.position = position;
        this.assignTarget = assignTarget;

        varLayout.shiftFrame();
        argsBeginSlot = varLayout.assignVar(ReservedNames.ARGUMENTS, position);
    }

    public static Argument ofArgument(String name, @Nullable Object defaultValue) {
        return new Argument(name, defaultValue);
    }

    public static FunctionLiteralBuilder create(VarLayout varLayout, Position position) {
        return new FunctionLiteralBuilder(varLayout, -1, position);
    }

    public static FunctionLiteralBuilder create(VarLayout varLayout, int assignTarget, Position position) {
        return new FunctionLiteralBuilder(varLayout, assignTarget, position);
    }

    public FunctionLiteralBuilder args(@Nullable List<Argument> infos) {
        if (infos != null) {
            infos.forEach(this::arg);
        }
        return this;
    }

    public FunctionLiteralBuilder arg(String name) {
        return arg(name, null);
    }

    public FunctionLiteralBuilder arg(String name, @Nullable Object defaultValue) {
        return arg(new Argument(name, defaultValue));
    }

    public FunctionLiteralBuilder arg(Argument info) {
        if (varLayout.assignVar(info.name, position) != (argsBeginSlot + (this.args.size() + 1))) {
            throw new ScriptParseException("Cannot assign argument variable: " + info.name);
        }
        this.args.add(info);
        return this;
    }

    public String argAt(int i) {
        return args.get(i).name;
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

    public FunctionLiteral build(Expression lambda) {
        return build(lambdaBody(lambda));
    }

    public FunctionLiteral build(List<Statement> list) {
        var scopeTables = varLayout.buildScopeTables();
        int heapSize = varLayout.slotSize();
        varLayout.unshiftFrame();

        var batches = IR.batch(list, jump -> {
            if (!jump.state().isReturn()) {
                throw new ScriptParseException("Unhandled control flow in function literal: "
                        + jump.state(), jump.position());
            }
        });

        var argDefaults = new Object[this.args.size()];
        for (int i = 0; i < argDefaults.length; i++) {
            argDefaults[i] = this.args.get(i).defaultValue;
        }

        return new FunctionLiteral(
                heapSize,
                scopeTables,
                batches,
                argDefaults,
                argsBeginSlot,
                position
        );
    }

    public record Argument(
            String name,
            @Nullable Object defaultValue
    ) {
    }
}
