package org.febit.wit.core;

import lombok.Builder;
import lombok.Singular;
import lombok.experimental.UtilityClass;
import org.febit.wit.exception.ParseException;
import org.febit.wit.runtime.ALU;
import org.febit.wit.runtime.ast.AssignableExpression;
import org.febit.wit.runtime.ast.AstUtils;
import org.febit.wit.runtime.ast.Expression;
import org.febit.wit.runtime.ast.IBlock;
import org.febit.wit.runtime.ast.LoopFlag;
import org.febit.wit.runtime.ast.Position;
import org.febit.wit.runtime.ast.Statement;
import org.febit.wit.runtime.ast.expr.BreakpointExpr;
import org.febit.wit.runtime.ast.expr.DirectValue;
import org.febit.wit.runtime.ast.expr.DynamicNativeMethodCallExpr;
import org.febit.wit.runtime.ast.expr.FunctionCallExpr;
import org.febit.wit.runtime.ast.expr.NewArrayExpr;
import org.febit.wit.runtime.ast.expr.NewMapExpr;
import org.febit.wit.runtime.ast.expr.SuppliedValue;
import org.febit.wit.runtime.ast.expr.TemplateStringValue;
import org.febit.wit.runtime.ast.oper.And;
import org.febit.wit.runtime.ast.oper.Assign;
import org.febit.wit.runtime.ast.oper.ConstableBiOperator;
import org.febit.wit.runtime.ast.oper.ConstableUnaryOperator;
import org.febit.wit.runtime.ast.oper.GroupAssign;
import org.febit.wit.runtime.ast.oper.IntStep;
import org.febit.wit.runtime.ast.oper.Or;
import org.febit.wit.runtime.ast.oper.SelfOperator;
import org.febit.wit.runtime.ast.stat.Block;
import org.febit.wit.runtime.ast.stat.BlockWithoutLoops;
import org.febit.wit.runtime.ast.stat.BreakpointStatement;
import org.febit.wit.runtime.ast.stat.If;
import org.febit.wit.runtime.ast.stat.IfElse;
import org.febit.wit.runtime.ast.stat.IfNot;
import org.febit.wit.runtime.ast.stat.Interpolation;
import org.febit.wit.runtime.ast.stat.NoopStatement;
import org.febit.wit.runtime.ast.stat.StatementGroup;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.BinaryOperator;
import java.util.function.UnaryOperator;

@UtilityClass
public class Ast {

    private static final Statement[] EMPTY_STATEMENTS = new Statement[0];
    private static final Expression[] EMPTY_EXPRESSIONS = new Expression[0];

    public static Expression assign(AssignableExpression lexpr, Expression rexpr, Position position) {
        return new Assign(lexpr, rexpr, position);
    }

    public static Expression groupAssign(Expression[] lexprs, Expression rexpr, Position position) {
        var assignables = new AssignableExpression[lexprs.length];
        for (int i = 0; i < lexprs.length; i++) {
            assignables[i] = castToAssignable(lexprs[i]);
        }
        return new GroupAssign(assignables, rexpr, position);
    }

    public static Statement interpolation(final Expression expr) {
        return new Interpolation(expr);
    }

    public static DirectValue directValue(@Nullable Object value, Position pos) {
        return new DirectValue(value, pos);
    }

    public static DirectValue directValue(Symbol sym) {
        return directValue(sym.value, sym.pos);
    }

    public static Statement breakpointStatement(
            @Nullable Expression labelExpr,
            @Nullable Statement statement,
            Position position
    ) {
        var label = labelExpr == null ? null : AstUtils.evalConst(labelExpr);
        return new BreakpointStatement(label, statement, position);
    }

    public static Expression breakpointExpr(@Nullable Expression labelExpr, Expression expr, Position position) {
        var label = labelExpr == null ? null : AstUtils.evalConst(labelExpr);
        return new BreakpointExpr(label, expr, position);
    }

    public static SuppliedValue emptyArray(Position pos) {
        return new SuppliedValue(() -> new Object[0], pos);
    }

    public static Expression[] emptyExpressions() {
        return EMPTY_EXPRESSIONS;
    }

    public static NewArrayExpr newArray(
            Position pos,
            @Singular List<Expression> exprs
    ) {
        return new NewArrayExpr(toExpressionArray(exprs), pos);
    }

    public static NewMapExpr newMap(
            @Nullable List<Expression[]> propertyDefList, Position position) {
        if (propertyDefList == null || propertyDefList.isEmpty()) {
            return new NewMapExpr(emptyExpressions(), emptyExpressions(), position);
        }
        int size = propertyDefList.size();
        var keys = new Expression[size];
        var values = new Expression[size];
        for (int i = 0; i < propertyDefList.size(); i++) {
            var def = propertyDefList.get(i);
            // assert def.length == 2
            keys[i] = def[0];
            values[i] = def[1];
        }
        return new NewMapExpr(keys, values, position);
    }

    @Builder(
            builderMethodName = "templateStringBuilder",
            builderClassName = "TemplateStringBuilder"
    )
    public static TemplateStringValue templateString(
            Position pos,
            @Singular List<Expression> exprs
    ) {
        return new TemplateStringValue(toExpressionArray(exprs), pos);
    }

    public static Expression[] toExpressionArray(@Nullable List<Expression> list) {
        if (list == null || list.isEmpty()) {
            return EMPTY_EXPRESSIONS;
        }
        var arr = list.toArray(new Expression[0]);
        for (int i = 0; i < arr.length; i++) {
            arr[i] = AstUtils.optimize(arr[i]);
        }
        return arr;
    }

    public static Statement[] flatStatements(@Nullable List<Statement> list) {
        if (list == null || list.isEmpty()) {
            return EMPTY_STATEMENTS;
        }
        List<Statement> temp = new ArrayList<>(list.size());
        for (var stat : list) {
            if (stat instanceof StatementGroup group) {
                temp.addAll(group.list());
                continue;
            }
            stat = AstUtils.optimize(stat);
            if (!(stat instanceof NoopStatement)) {
                temp.add(stat);
            }
        }
        return list.isEmpty()
                ? EMPTY_STATEMENTS
                : temp.toArray(new Statement[0]);
    }

    public static Statement statementGroup(List<Statement> list, Position position) {
        return new StatementGroup(flatStatements(list), position);
    }

    public static Expression functionCall(Expression funcExpr, Expression[] paramExprs, Position position) {
        AstUtils.optimize(paramExprs);
        funcExpr = AstUtils.optimize(funcExpr);
        return new FunctionCallExpr(funcExpr, paramExprs, position);
    }

    public static Expression dynamicNativeMethodCall(
            Expression thisExpr, String func, Expression[] paramExprs, Position position) {
        AstUtils.optimize(paramExprs);
        thisExpr = AstUtils.optimize(thisExpr);
        return new DynamicNativeMethodCallExpr(thisExpr, func, paramExprs, position);
    }

    public static Statement ifStatement(
            Expression ifExpr,
            @Nullable Statement thenBody,
            @Nullable Statement elseBody,
            Position position
    ) {
        thenBody = AstUtils.optimize(thenBody);
        elseBody = AstUtils.optimize(elseBody);
        if (!(thenBody instanceof NoopStatement)) {
            if (elseBody instanceof NoopStatement) {
                return new If(ifExpr, thenBody, position);
            }
            return new IfElse(ifExpr, thenBody, elseBody, position);
        }
        if (!(elseBody instanceof NoopStatement)) {
            return new IfNot(ifExpr, elseBody, position);
        }
        return NoopStatement.INSTANCE;
    }

    public static IBlock block(@Nullable List<Statement> list, int varIndexer, Position position) {
        var statements = flatStatements(list);
        var loops = AstUtils.collectLoopFlags(statements);
        return loops.isEmpty()
                ? new BlockWithoutLoops(varIndexer, statements, position)
                : new Block(varIndexer, statements, loops.toArray(new LoopFlag[0]), position);
    }

    public static AssignableExpression castToAssignable(Expression expr) {
        if (expr instanceof AssignableExpression assign) {
            return assign;
        }
        throw new ParseException("expression is not assignable", expr.position());
    }

    public static ParseException unsupportedOperator(Position position) {
        return new ParseException("Unsupported Operator", position);
    }

    public static Expression selfOperator(Expression lexpr, int sym, Expression rightExpr, Position position) {
        var leftExpr = castToAssignable(lexpr);
        var biFunc = binaryOperator(sym);
        if (biFunc == null) {
            throw unsupportedOperator(position);
        }
        var optimized = AstUtils.optimize(
                new SelfOperator(leftExpr, rightExpr, biFunc, position)
        );
        Objects.requireNonNull(optimized);
        return optimized;
    }

    public static Expression operator(Expression expr, Symbol sym) {
        if (!(sym.value instanceof Integer token)) {
            throw unsupportedOperator(sym.pos);
        }
        UnaryOperator<@Nullable Object> func = switch (token) {
            case Tokens.COMP -> ALU::bitNot;
            case Tokens.MINUS -> ALU::negative;
            case Tokens.NOT -> ALU::not;
            default -> throw unsupportedOperator(sym.pos);
        };
        var optimized = AstUtils.optimize(
                new ConstableUnaryOperator(expr, func, sym.pos)
        );
        Objects.requireNonNull(optimized);
        return optimized;
    }

    public static Expression biOperator(Expression leftExpr, Symbol sym, Expression rightExpr) {
        if (!(sym.value instanceof Integer token)) {
            throw unsupportedOperator(sym.pos);
        }
        var op = switch (token) {
            case Tokens.ANDAND -> new And(leftExpr, rightExpr, sym.pos);
            case Tokens.OROR -> new Or(leftExpr, rightExpr, sym.pos);
            case Tokens.DOTDOT -> new IntStep(leftExpr, rightExpr, sym.pos);
            default -> {
                var biFunc = binaryOperator(token);
                if (biFunc == null) {
                    throw unsupportedOperator(sym.pos);
                }
                yield new ConstableBiOperator(leftExpr, rightExpr, biFunc, sym.pos);
            }
        };
        var optimized = AstUtils.optimize(op);
        Objects.requireNonNull(optimized);
        return optimized;
    }

    @Nullable
    private static BinaryOperator<@Nullable Object> binaryOperator(int token) {
        return switch (token) {
            case Tokens.PLUS -> ALU::plus;
            case Tokens.MINUS -> ALU::minus;
            case Tokens.MULT -> ALU::multi;
            case Tokens.DIV -> ALU::div;
            case Tokens.MOD -> ALU::mod;
            case Tokens.LSHIFT -> ALU::lshift;
            case Tokens.RSHIFT -> ALU::rshift;
            case Tokens.URSHIFT -> ALU::urshift;
            case Tokens.LT -> ALU::less;
            case Tokens.GT -> ALU::greater;
            case Tokens.LTEQ -> ALU::lessEqual;
            case Tokens.GTEQ -> ALU::greaterEqual;
            case Tokens.EQEQ -> ALU::isEqual;
            case Tokens.NOTEQ -> ALU::isNotEqual;
            case Tokens.AND -> ALU::bitAnd;
            case Tokens.XOR -> ALU::bitXor;
            case Tokens.OR -> ALU::bitOr;
            default -> null;
        };
    }
}
