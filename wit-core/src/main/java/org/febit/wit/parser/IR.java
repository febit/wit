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
import lombok.experimental.UtilityClass;
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
import org.febit.wit.ir.expr.DynamicNativeCall;
import org.febit.wit.ir.expr.FunctionCall;
import org.febit.wit.ir.expr.LazyValue;
import org.febit.wit.ir.expr.NewArray;
import org.febit.wit.ir.expr.NewMap;
import org.febit.wit.ir.flow.Break;
import org.febit.wit.ir.flow.Continue;
import org.febit.wit.ir.flow.Jump;
import org.febit.wit.ir.flow.Return;
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
import org.febit.wit.ir.statement.Throw;
import org.febit.wit.ir.statement.TryCatchFinally;
import org.febit.wit.ir.statement.TryFinally;
import org.febit.wit.ir.support.ALU;
import org.febit.wit.ir.support.StatementList;
import org.febit.wit.ir.support.StatementUtils;
import org.febit.wit.parser.support.SwitchBuilder;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.BinaryOperator;
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

    public static Throw throwWith(Expression value, Position pos) {
        return new Throw(value, pos);
    }

    public static Break breakTo(int label, Position pos) {
        return new Break(label, pos);
    }

    public static Continue continueTo(int label, Position pos) {
        return new Continue(label, pos);
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
        return new NewArray(ExpressionArray.of(values), pos);
    }

    public static NewMap newMap(List<NewMap.NewMapEntry> entries, Position pos) {
        return new NewMap(entries, pos);
    }

    public static NewMap.NewMapEntry entryOfNewMap(Expression key, Expression value) {
        return new NewMap.NewMapEntry(key, value);
    }

    public static SwitchBuilder switchBuilder() {
        return new SwitchBuilder();
    }

    public enum WhileKind {
        WHILE, DO_WHILE
    }

    @Builder(
            builderMethodName = "whileBuilder",
            builderClassName = "WhileBuilder"
    )
    private static Statement while0(
            @lombok.NonNull WhileKind kind,
            @lombok.NonNull Expression condition,
            @lombok.NonNull IBlock body,
            @lombok.NonNull Position pos,
            @Nullable Integer label
    ) {
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
            @lombok.NonNull Statement body,
            @lombok.NonNull Position pos,
            @Nullable Statement catchBody,
            @Nullable Statement finallyBody,
            @Nullable Integer exceptionVarSlot
    ) {
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

    public static LoopBody loopBodyFromStatements(List<Statement> statements, int targetLabel) {
        var jumps = new ArrayList<Jump>();
        var batches = StatementBatch.batch(statements, jumps::add);
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

    public static StatementList statementList(List<Statement> list, Position pos) {
        return new StatementList(list, pos);
    }

    public static FunctionCall functionCall(Expression func, ExpressionArray params, Position pos) {
        return new FunctionCall(func, params, pos);
    }

    public static DynamicNativeCall dynamicNativeCall(
            Expression self, String method, ExpressionArray params, Position pos) {
        return new DynamicNativeCall(self, method, params, pos);
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
        var batches = StatementBatch.batch(list, jumps::add);
        if (jumps.isEmpty()) {
            if (batches.size() != 1) {
                throw new IllegalStateException("Unexpected multiple batches without jumps");
            }
            return new NoJumpBlock(scope, batches.get(0), pos);
        }
        return new Block(scope, batches, List.copyOf(jumps), pos);
    }

    public static AssignableExpression castToAssignable(Expression expr) {
        expr = StatementUtils.optimize(expr);
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
