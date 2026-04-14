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

import lombok.Builder;
import lombok.Singular;
import lombok.experimental.UtilityClass;
import org.febit.wit.Script;
import org.febit.wit.exception.ScriptParseException;
import org.febit.wit.ir.AssignableExpression;
import org.febit.wit.ir.Expression;
import org.febit.wit.ir.ExpressionArray;
import org.febit.wit.ir.IBlock;
import org.febit.wit.ir.Position;
import org.febit.wit.ir.Statement;
import org.febit.wit.ir.StatementBatch;
import org.febit.wit.ir.expr.BreakpointExpr;
import org.febit.wit.ir.expr.ConstantValue;
import org.febit.wit.ir.expr.DynamicNativeInvocation;
import org.febit.wit.ir.expr.FunctionCall;
import org.febit.wit.ir.expr.HeapValue;
import org.febit.wit.ir.expr.LazyValue;
import org.febit.wit.ir.expr.NewArray;
import org.febit.wit.ir.expr.NewMap;
import org.febit.wit.ir.expr.TemplateStringValue;
import org.febit.wit.ir.expr.VariableHeapFrameValue;
import org.febit.wit.ir.expr.VariableHeapValue;
import org.febit.wit.ir.flow.Jump;
import org.febit.wit.ir.flow.Return;
import org.febit.wit.ir.include.AssignMappedIncludeHandler;
import org.febit.wit.ir.include.Include;
import org.febit.wit.ir.include.IncludeHandler;
import org.febit.wit.ir.include.IncludeHandlers;
import org.febit.wit.ir.loop.DoWhile;
import org.febit.wit.ir.loop.JumpAwareLoopBody;
import org.febit.wit.ir.loop.LoopBody;
import org.febit.wit.ir.loop.NoJumpLoopBody;
import org.febit.wit.ir.loop.While;
import org.febit.wit.ir.oper.And;
import org.febit.wit.ir.oper.Assign;
import org.febit.wit.ir.oper.CompoundAssign;
import org.febit.wit.ir.oper.ConstableBiOperator;
import org.febit.wit.ir.oper.ConstableUnaryOperator;
import org.febit.wit.ir.oper.DecreaseAndGet;
import org.febit.wit.ir.oper.DestructuringAssign;
import org.febit.wit.ir.oper.FixedPropertyAccess;
import org.febit.wit.ir.oper.GetAndDecrease;
import org.febit.wit.ir.oper.GetAndIncrease;
import org.febit.wit.ir.oper.IncreaseAndGet;
import org.febit.wit.ir.oper.IntRange;
import org.febit.wit.ir.oper.Or;
import org.febit.wit.ir.oper.PropertyAccess;
import org.febit.wit.ir.oper.Ternary;
import org.febit.wit.ir.statement.Block;
import org.febit.wit.ir.statement.BreakpointStatement;
import org.febit.wit.ir.statement.Echo;
import org.febit.wit.ir.statement.If;
import org.febit.wit.ir.statement.IfElse;
import org.febit.wit.ir.statement.IfNot;
import org.febit.wit.ir.statement.NoJumpBlock;
import org.febit.wit.ir.statement.NoopStatement;
import org.febit.wit.ir.statement.RenderRedirect;
import org.febit.wit.ir.statement.TryCatchFinally;
import org.febit.wit.ir.statement.TryFinally;
import org.febit.wit.ir.support.ALU;
import org.febit.wit.ir.support.Jumps;
import org.febit.wit.ir.support.StatementList;
import org.febit.wit.ir.support.StatementUtils;
import org.febit.wit.ir.template.Interpolation;
import org.febit.wit.parser.support.SwitchBuilder;
import org.febit.wit.parser.support.VarAddress;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;

@UtilityClass
public class IR {

    public static Ternary ternary(
            Expression condition,
            Expression left,
            Expression right,
            Position pos
    ) {
        return new Ternary(condition, left, right, pos);
    }

    public static Echo echo(Expression value, Position pos) {
        return new Echo(value, pos);
    }

    public static Return returnWith(@Nullable Expression value, Position pos) {
        return new Return(value, pos);
    }

    public static RenderRedirect renderRedirect(Statement body, AssignableExpression target, Position pos) {
        return new RenderRedirect(target, body, pos);
    }

    public static IncreaseAndGet increaseAndGet(AssignableExpression target, Position pos) {
        return new IncreaseAndGet(target, pos);
    }

    public static DecreaseAndGet decreaseAndGet(AssignableExpression target, Position pos) {
        return new DecreaseAndGet(target, pos);
    }

    public static GetAndIncrease getAndIncrease(AssignableExpression target, Position pos) {
        return new GetAndIncrease(target, pos);
    }

    public static GetAndDecrease getAndDecrease(AssignableExpression target, Position pos) {
        return new GetAndDecrease(target, pos);
    }

    public static Assign assign(AssignableExpression target, Expression value, Position pos) {
        return new Assign(target, value, pos);
    }

    public static DestructuringAssign destructuringAssign(ExpressionArray targets, Expression value, Position pos) {
        var targetList = targets.asList();
        var size = targetList.size();
        var assignables = new AssignableExpression[size];
        for (int i = 0; i < size; i++) {
            assignables[i] = castToAssignable(targetList.get(i));
        }
        return new DestructuringAssign(assignables, value, pos);
    }

    public static PropertyAccess property(Expression target, Expression property, Position pos) {
        return new PropertyAccess(target, property, pos);
    }

    public static FixedPropertyAccess property(Expression target, String property, Position pos) {
        return new FixedPropertyAccess(target, property, pos);
    }

    public static Interpolation interpolation(Expression value) {
        return new Interpolation(value, value.position());
    }

    public static ConstantValue constant(@Nullable Object value, Position pos) {
        return new ConstantValue(value, pos);
    }

    public static ConstantValue constant(Token token) {
        return constant(token.value, token.pos);
    }

    public static BreakpointStatement breakpointStatement(
            @Nullable Expression mark,
            @Nullable Statement supervised,
            Position pos
    ) {
        var labelObj = mark == null ? null : StatementUtils.evalAsConst(mark);
        return new BreakpointStatement(labelObj, supervised, pos);
    }

    public static Expression breakpointExpr(@Nullable Expression mark, Expression supervised, Position pos) {
        var markObj = mark == null ? null : StatementUtils.evalAsConst(mark);
        return new BreakpointExpr(markObj, supervised, pos);
    }

    public static LazyValue emptyArray(Position pos) {
        return new LazyValue(() -> new Object[0], pos);
    }

    public static NewArray newArray(
            List<Expression> values,
            Position pos
    ) {
        return new NewArray(toExpressionArray(values), pos);
    }

    public static NewMap newMap(List<NewMap.NewMapEntry> entries, Position pos) {
        return new NewMap(List.copyOf(entries), pos);
    }

    public static NewMap.NewMapEntry entryOfNewMap(Expression key, Expression value) {
        key = StatementUtils.optimize(key);
        value = StatementUtils.optimize(value);
        return new NewMap.NewMapEntry(key, value);
    }

    public static SwitchBuilder switchBuilder() {
        return new SwitchBuilder();
    }

    @Builder(
            builderMethodName = "templateStringBuilder",
            builderClassName = "TemplateStringBuilder"
    )
    public static TemplateStringValue templateString(
            @Singular
            List<Expression> segments,
            Position pos
    ) {
        Objects.requireNonNull(pos, "position is required");
        return new TemplateStringValue(toExpressionArray(segments).asList(), pos);
    }

    public enum WhileKind {
        WHILE, DO_WHILE
    }

    @Builder(
            builderMethodName = "whileBuilder",
            builderClassName = "WhileBuilder"
    )
    private static Statement while0(
            WhileKind kind,
            Expression condition,
            IBlock body,
            @Nullable Integer label,
            Position pos
    ) {
        Objects.requireNonNull(kind, "kind is required");
        Objects.requireNonNull(condition, "condition is required");
        Objects.requireNonNull(body, "body is required");
        Objects.requireNonNull(pos, "position is required");

        var loopBody = loopBodyFromBatches(
                body.body(),
                label != null ? label : 0
        );
        var scope = body.scope();
        return switch (kind) {
            case WHILE -> new While(scope, condition, loopBody, pos);
            case DO_WHILE -> new DoWhile(scope, condition, loopBody, pos);
        };
    }

    @Builder(
            builderMethodName = "tryCatchBuilder",
            builderClassName = "TryCatchBuilder"
    )
    private static Statement tryCatch(
            Statement body,
            @Nullable Statement catchBody,
            @Nullable Statement finallyBody,
            @Nullable Integer exceptionVarSlot,
            Position pos
    ) {
        Objects.requireNonNull(body, "tryBody is required");
        Objects.requireNonNull(pos, "position is required");

        body = StatementUtils.optimize(body);

        if (catchBody != null) {
            Objects.requireNonNull(exceptionVarSlot,
                    "exceptionVarSlot is required when catchBody is provided");
            catchBody = StatementUtils.optimize(catchBody);
        }

        if (finallyBody != null) {
            finallyBody = StatementUtils.optimize(finallyBody);
        }

        if (catchBody == null) {
            if (finallyBody == null) {
                return body;
            }
            return new TryFinally(body, finallyBody, pos);
        }
        return new TryCatchFinally(exceptionVarSlot, body, catchBody, finallyBody, pos);
    }

    public static class IncludeBuilder {

        public IncludeBuilder export(String name, Expression target) {
            var optimized = castToAssignable(StatementUtils.optimize(target));
            return exportVar(new AssignMappedIncludeHandler.Entry(name, optimized));
        }
    }

    @Builder(
            builderMethodName = "includeBuilder",
            builderClassName = "IncludeBuilder"
    )
    private static Include include(
            Script script,
            Position pos,
            Expression path,
            @Nullable Boolean withoutExport,
            @Nullable Expression params,
            @Singular("exportVar")
            List<AssignMappedIncludeHandler.Entry> exportMappings
    ) {
        Objects.requireNonNull(script, "script is required");
        Objects.requireNonNull(path, "path is required");
        Objects.requireNonNull(pos, "position is required");

        path = StatementUtils.optimize(path);

        if (params != null) {
            params = StatementUtils.optimize(params);
        }

        var refer = script.path();

        IncludeHandler handler;
        if (withoutExport != null && withoutExport) {
            handler = IncludeHandlers::noop;
        } else if (exportMappings.isEmpty()) {
            // If empty, means export & import all.
            handler = IncludeHandlers::importAll;
        } else {
            handler = new AssignMappedIncludeHandler(exportMappings);
        }
        return new Include(refer, path, handler, params, pos);
    }

    public static LoopBody loopBodyFromStatements(List<Statement> statements, int targetLabel) {
        var jumps = new ArrayList<Jump>();
        var batches = batch(statements, jumps::add);
        return loopBody0(batches, jumps, targetLabel);
    }

    public static LoopBody loopBodyFromBatches(List<StatementBatch> batches, int targetLabel) {
        var jumps = new ArrayList<Jump>();
        batches.forEach(batch -> batch.collectJumps(jumps::add));
        return loopBody0(batches, jumps, targetLabel);
    }

    private static LoopBody loopBody0(List<StatementBatch> batches, List<Jump> jumps, int targetLabel) {
        if (jumps.isEmpty()) {
            if (batches.size() != 1) {
                throw new IllegalStateException("Unexpected multiple batches without jumps");
            }
            var batch0 = batches.get(0);
            return new NoJumpLoopBody(batch0);
        }

        var bubbled = List.copyOf(jumps.stream()
                .filter(f -> !f.matchesLabel(targetLabel)
                        || !f.state().isBreakOrContinue())
                .toList());
        return new JumpAwareLoopBody(targetLabel, batches, bubbled);
    }

    public static Expression value(VarAddress addr, Position pos) {
        return switch (addr.kind()) {
            case VAR -> new VariableHeapValue(addr.slot(), pos);
            case FRAME_VAR -> new VariableHeapFrameValue(addr.frameOffset(), addr.slot(), pos);
            case CONSTANT -> new ConstantValue(addr.value(), pos);
            case HEAP -> {
                var key = Objects.requireNonNull(addr.key());
                var heap = Objects.requireNonNull(addr.heap());
                yield new HeapValue(
                        heap, key, pos
                );
            }
        };
    }

    public static ExpressionArray toExpressionArray(@Nullable List<Expression> list) {
        if (list == null || list.isEmpty()) {
            return ExpressionArray.ofEmpty();
        }
        var arr = list.toArray(new Expression[0]);
        for (int i = 0; i < arr.length; i++) {
            arr[i] = StatementUtils.optimize(arr[i]);
        }
        return ExpressionArray.of(arr);
    }

    public static StatementList statementList(List<Statement> list, Position pos) {
        return new StatementList(List.copyOf(list), pos);
    }

    public static FunctionCall functionCall(
            Expression func, ExpressionArray params, Position pos) {
        func = StatementUtils.optimize(func);
        return new FunctionCall(func, params, pos);
    }

    public static DynamicNativeInvocation dynamicNativeInvocationCall(
            Expression self, String method, ExpressionArray params, Position pos) {
        self = StatementUtils.optimize(self);
        return new DynamicNativeInvocation(self, method, params, pos);
    }

    public static Statement ifStatement(
            Expression condition,
            @Nullable Statement thenBody,
            @Nullable Statement elseBody,
            Position pos
    ) {
        thenBody = StatementUtils.optimize(thenBody);
        elseBody = StatementUtils.optimize(elseBody);
        if (!(thenBody instanceof NoopStatement)) {
            if (elseBody instanceof NoopStatement) {
                return new If(condition, thenBody, pos);
            }
            return new IfElse(condition, thenBody, elseBody, pos);
        }
        if (!(elseBody instanceof NoopStatement)) {
            return new IfNot(condition, elseBody, pos);
        }
        return NoopStatement.INSTANCE;
    }

    public static IBlock block(@Nullable List<Statement> list, int scope, Position pos) {
        var jumps = new ArrayList<Jump>();
        var batches = batch(list, jumps::add);
        if (jumps.isEmpty()) {
            if (batches.size() != 1) {
                throw new IllegalStateException("Unexpected multiple batches without jumps");
            }
            return new NoJumpBlock(scope, batches.get(0), pos);
        }

        return new Block(scope, batches, List.copyOf(jumps), pos);
    }

    public static void flatAndOptimize(@Nullable List<Statement> statements, Consumer<Statement> collector) {
        if (statements == null || statements.isEmpty()) {
            return;
        }
        for (var stat : statements) {
            if (stat instanceof StatementList list) {
                flatAndOptimize(list.statements(), collector);
                continue;
            }
            stat = StatementUtils.optimize(stat);
            if (stat instanceof NoopStatement) {
                continue;
            }
            collector.accept(stat);
        }
    }

    /**
     * Batch statements, collect flow control jumps.
     *
     * @return always not empty, if no statement, return a batch with empty statements.
     */
    public static List<StatementBatch> batch(@Nullable List<Statement> list, Consumer<Jump> jumpConsumer) {
        if (list == null || list.isEmpty()) {
            return List.of(StatementBatch.empty());
        }
        var flag = new AtomicBoolean();
        var collecting = (Consumer<Jump>) (jump -> {
            flag.set(true);
            jumpConsumer.accept(jump);
        });

        var batches = new ArrayList<StatementBatch>();
        var current = new ArrayList<Statement>();

        flatAndOptimize(list, stat -> {
            current.add(stat);
            Jumps.collect(collecting, stat);
            if (flag.get()) {
                batches.add(StatementBatch.of(current));
                current.clear();
                flag.set(false);
            }
        });

        if (!current.isEmpty()) {
            batches.add(StatementBatch.of(current));
        }
        if (batches.isEmpty()) {
            return List.of(StatementBatch.empty());
        }
        return List.copyOf(batches);
    }

    public static AssignableExpression castToAssignable(Expression expr) {
        if (expr instanceof AssignableExpression assign) {
            return assign;
        }
        throw new ScriptParseException("expression is not assignable", expr.position());
    }

    public static ScriptParseException unsupportedOperator(Position pos) {
        return new ScriptParseException("Unsupported Operator", pos);
    }

    public static Expression selfAssign(Expression target, Expression delta, int tokenKind, Position pos) {
        var assignable = castToAssignable(target);
        var biFunc = binaryOperator(tokenKind);
        if (biFunc == null) {
            throw unsupportedOperator(pos);
        }
        var optimized = StatementUtils.optimize(
                new CompoundAssign(assignable, delta, biFunc, pos)
        );
        Objects.requireNonNull(optimized);
        return optimized;
    }

    public static Expression operator(Expression target, Token token) {
        if (!(token.value instanceof Integer kind)) {
            throw unsupportedOperator(token.pos);
        }
        UnaryOperator<@Nullable Object> func = switch (kind) {
            case TokenKinds.COMP -> ALU::bitNot;
            case TokenKinds.MINUS -> ALU::negative;
            case TokenKinds.NOT -> ALU::not;
            default -> throw unsupportedOperator(token.pos);
        };
        var optimized = StatementUtils.optimize(
                new ConstableUnaryOperator(target, func, token.pos)
        );
        Objects.requireNonNull(optimized);
        return optimized;
    }

    public static Expression binaryOperator(Expression left, Expression right, Token token) {
        if (!(token.value instanceof Integer kind)) {
            throw unsupportedOperator(token.pos);
        }
        var op = switch (kind) {
            case TokenKinds.ANDAND -> new And(left, right, token.pos);
            case TokenKinds.OROR -> new Or(left, right, token.pos);
            case TokenKinds.DOTDOT -> new IntRange(left, right, token.pos);
            default -> {
                var biFunc = binaryOperator(kind);
                if (biFunc == null) {
                    throw unsupportedOperator(token.pos);
                }
                yield new ConstableBiOperator(left, right, biFunc, token.pos);
            }
        };
        var optimized = StatementUtils.optimize(op);
        Objects.requireNonNull(optimized);
        return optimized;
    }

    @Nullable
    private static BinaryOperator<@Nullable Object> binaryOperator(int token) {
        return switch (token) {
            case TokenKinds.PLUS -> ALU::plus;
            case TokenKinds.MINUS -> ALU::minus;
            case TokenKinds.MULTI -> ALU::multi;
            case TokenKinds.DIV -> ALU::div;
            case TokenKinds.MOD -> ALU::mod;
            case TokenKinds.LSHIFT -> ALU::lshift;
            case TokenKinds.RSHIFT -> ALU::rshift;
            case TokenKinds.URSHIFT -> ALU::urshift;
            case TokenKinds.LT -> ALU::less;
            case TokenKinds.GT -> ALU::greater;
            case TokenKinds.LTEQ -> ALU::lessEqual;
            case TokenKinds.GTEQ -> ALU::greaterEqual;
            case TokenKinds.EQEQ -> ALU::isEqual;
            case TokenKinds.NOTEQ -> ALU::isNotEqual;
            case TokenKinds.AND -> ALU::bitAnd;
            case TokenKinds.XOR -> ALU::bitXor;
            case TokenKinds.OR -> ALU::bitOr;
            default -> null;
        };
    }
}
