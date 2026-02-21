package org.febit.wit.parser;

import lombok.Builder;
import lombok.Singular;
import lombok.experimental.UtilityClass;
import org.febit.wit.Script;
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
import org.febit.wit.runtime.ast.expr.ContextLayerVar;
import org.febit.wit.runtime.ast.expr.ContextVar;
import org.febit.wit.runtime.ast.expr.DirectValue;
import org.febit.wit.runtime.ast.expr.DynamicNativeMethodCallExpr;
import org.febit.wit.runtime.ast.expr.FunctionCallExpr;
import org.febit.wit.runtime.ast.expr.HeapValue;
import org.febit.wit.runtime.ast.expr.NewArrayExpr;
import org.febit.wit.runtime.ast.expr.NewMapExpr;
import org.febit.wit.runtime.ast.expr.SuppliedValue;
import org.febit.wit.runtime.ast.extra.Import;
import org.febit.wit.runtime.ast.oper.And;
import org.febit.wit.runtime.ast.oper.Assign;
import org.febit.wit.runtime.ast.oper.ConstableBiOperator;
import org.febit.wit.runtime.ast.oper.ConstableUnaryOperator;
import org.febit.wit.runtime.ast.oper.DecreaseAndGet;
import org.febit.wit.runtime.ast.oper.FixedPropertyOperator;
import org.febit.wit.runtime.ast.oper.GetAndDecrease;
import org.febit.wit.runtime.ast.oper.GetAndIncrease;
import org.febit.wit.runtime.ast.oper.GroupAssign;
import org.febit.wit.runtime.ast.oper.IfOperator;
import org.febit.wit.runtime.ast.oper.IncreaseAndGet;
import org.febit.wit.runtime.ast.oper.IntStep;
import org.febit.wit.runtime.ast.oper.Or;
import org.febit.wit.runtime.ast.oper.PropertyOperator;
import org.febit.wit.runtime.ast.oper.SelfOperator;
import org.febit.wit.runtime.ast.stat.Block;
import org.febit.wit.runtime.ast.stat.BlockWithoutLoops;
import org.febit.wit.runtime.ast.stat.BreakpointStatement;
import org.febit.wit.runtime.ast.stat.DoWhile;
import org.febit.wit.runtime.ast.stat.DoWhileNoLoops;
import org.febit.wit.runtime.ast.stat.Echo;
import org.febit.wit.runtime.ast.stat.If;
import org.febit.wit.runtime.ast.stat.IfElse;
import org.febit.wit.runtime.ast.stat.IfNot;
import org.febit.wit.runtime.ast.stat.Interpolation;
import org.febit.wit.runtime.ast.stat.NoopStatement;
import org.febit.wit.runtime.ast.stat.RenderRedirect;
import org.febit.wit.runtime.ast.stat.Return;
import org.febit.wit.runtime.ast.stat.StatementGroup;
import org.febit.wit.runtime.ast.stat.TryCatchFinally;
import org.febit.wit.runtime.ast.stat.TryFinally;
import org.febit.wit.runtime.ast.stat.While;
import org.febit.wit.runtime.ast.stat.WhileNoLoops;
import org.febit.wit.runtime.ast.template.TemplateStringValue;
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

    public static IfOperator ifOperator(Expression ifExpr, Expression leftValueExpr, Expression rightValueExpr, Position position) {
        return new IfOperator(ifExpr, leftValueExpr, rightValueExpr, position);
    }

    public static Echo echo(Expression src, Position position) {
        return new Echo(src, position);
    }

    public static Return returnWith(@Nullable Expression src, Position position) {
        return new Return(src, position);
    }

    public static Return returnUndefined(Position position) {
        return new Return(null, position);
    }

    public static RenderRedirect renderRedirect(Statement body, AssignableExpression sink, Position position) {
        return new RenderRedirect(body, sink, position);
    }

    public static IncreaseAndGet increaseAndGet(AssignableExpression assignable, Position position) {
        return new IncreaseAndGet(assignable, position);
    }

    public static DecreaseAndGet decreaseAndGet(AssignableExpression assignable, Position position) {
        return new DecreaseAndGet(assignable, position);
    }

    public static GetAndIncrease getAndIncrease(AssignableExpression assignable, Position position) {
        return new GetAndIncrease(assignable, position);
    }

    public static GetAndDecrease getAndDecrease(AssignableExpression assignable, Position position) {
        return new GetAndDecrease(assignable, position);
    }

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

    public static PropertyOperator property(Expression leftExpr, Expression rightExpr, Position position) {
        return new PropertyOperator(leftExpr, rightExpr, position);
    }

    public static FixedPropertyOperator property(Expression leftExpr, String property, Position position) {
        return new FixedPropertyOperator(leftExpr, property, position);
    }

    public static Statement interpolation(Expression expr) {
        return new Interpolation(expr);
    }

    public static DirectValue directValue(@Nullable Object value, Position pos) {
        return new DirectValue(value, pos);
    }

    public static DirectValue directValue(Token sym) {
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
            Position position
    ) {
        Objects.requireNonNull(position, "position is required");
        return new TemplateStringValue(toExpressionArray(segments), position);
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
            Position position
    ) {
        Objects.requireNonNull(kind, "kind is required");
        Objects.requireNonNull(condition, "condition is required");
        Objects.requireNonNull(body, "body is required");
        Objects.requireNonNull(position, "position is required");

        var frame = body.frame();
        var statements = body.statements();

        if (!body.hasLoopFlags()) {
            return switch (kind) {
                case WHILE -> new WhileNoLoops(condition, frame, statements, position);
                case DO_WHILE -> new DoWhileNoLoops(condition, frame, statements, position);
            };
        }

        if (label == null) {
            label = 0;
        }
        var loops = AstUtils.collectLoopFlagsForWhile(List.of(body), null, label);
        return switch (kind) {
            case WHILE -> new While(condition, frame, statements, loops, label, position);
            case DO_WHILE -> new DoWhile(condition, frame, statements, loops, label, position);
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
            @Nullable Integer exceptionVarIndex,
            Position position
    ) {
        Objects.requireNonNull(body, "tryBody is required");
        Objects.requireNonNull(position, "position is required");

        body = AstUtils.optimize(body);

        if (catchBody != null) {
            Objects.requireNonNull(exceptionVarIndex,
                    "exceptionVarIndex is required when catchBody is provided");
            catchBody = AstUtils.optimize(catchBody);
        }

        if (finallyBody != null) {
            finallyBody = AstUtils.optimize(finallyBody);
        }

        if (catchBody == null) {
            if (finallyBody == null) {
                return body;
            }
            return new TryFinally(body, finallyBody, position);
        }
        return new TryCatchFinally(body, exceptionVarIndex, catchBody, finallyBody, position);
    }

    @Builder(
            builderMethodName = "importBuilder",
            builderClassName = "ImportBuilder"
    )
    private static Statement import0(
            Script script,
            Position position,
            Expression path,
            @Nullable Expression params,
            @Singular("exportVar")
            List<ImportVar> exportVars
    ) {
        Objects.requireNonNull(script, "script is required");
        Objects.requireNonNull(path, "path is required");
        Objects.requireNonNull(position, "position is required");

        path = AstUtils.optimize(path);

        if (params != null) {
            params = AstUtils.optimize(params);
        }

        var refer = script.path();
        if (exportVars.isEmpty()) {
            return new Import(path, params, null, null, refer, position);
        }

        var vars = exportVars.stream()
                .map(ImportVar::name)
                .toArray(String[]::new);

        var targets = exportVars.stream()
                .map(ImportVar::target)
                .map(AstUtils::optimize)
                .map(AssignableExpression.class::cast)
                .toArray(AssignableExpression[]::new);

        return new Import(path, params, vars, targets, refer, position);
    }

    public record ImportVar(String name, AssignableExpression target) {
    }

    public static class ImportBuilder {

        public ImportBuilder export(String name, Expression target) {
            return export(name, castToAssignable(target));
        }

        public ImportBuilder export(String name, AssignableExpression target) {
            return exportVar(new ImportVar(name, target));
        }
    }

    public static Expression readVar(VarAddress addr, Position position) {
        return switch (addr.kind()) {
            case CONST -> new DirectValue(addr.value(), position);
            case CONTEXT_LAYER -> new ContextLayerVar(addr.layerOffset(), addr.index(), position);
            case CONTEXT -> new ContextVar(addr.index(), position);
            case STATIC_VAR -> {
                var key = Objects.requireNonNull(addr.key());
                var heap = Objects.requireNonNull(addr.heap());
                yield new HeapValue(
                        heap, key, position
                );
            }
        };
    }

    public enum WhileKind {
        WHILE, DO_WHILE
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

    public static IBlock block(@Nullable List<Statement> list, int frame, Position position) {
        var statements = flatStatements(list);
        var loops = AstUtils.collectLoopFlags(statements);
        return loops.isEmpty()
                ? new BlockWithoutLoops(frame, statements, position)
                : new Block(frame, statements, loops.toArray(new LoopFlag[0]), position);
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

    public static Expression operator(Expression expr, Token sym) {
        if (!(sym.value instanceof Integer token)) {
            throw unsupportedOperator(sym.pos);
        }
        UnaryOperator<@Nullable Object> func = switch (token) {
            case TokenKinds.COMP -> ALU::bitNot;
            case TokenKinds.MINUS -> ALU::negative;
            case TokenKinds.NOT -> ALU::not;
            default -> throw unsupportedOperator(sym.pos);
        };
        var optimized = AstUtils.optimize(
                new ConstableUnaryOperator(expr, func, sym.pos)
        );
        Objects.requireNonNull(optimized);
        return optimized;
    }

    public static Expression binaryOperator(Expression left, Token sym, Expression right) {
        if (!(sym.value instanceof Integer token)) {
            throw unsupportedOperator(sym.pos);
        }
        var op = switch (token) {
            case TokenKinds.ANDAND -> new And(left, right, sym.pos);
            case TokenKinds.OROR -> new Or(left, right, sym.pos);
            case TokenKinds.DOTDOT -> new IntStep(left, right, sym.pos);
            default -> {
                var biFunc = binaryOperator(token);
                if (biFunc == null) {
                    throw unsupportedOperator(sym.pos);
                }
                yield new ConstableBiOperator(left, right, biFunc, sym.pos);
            }
        };
        var optimized = AstUtils.optimize(op);
        Objects.requireNonNull(optimized);
        return optimized;
    }

    @Nullable
    private static BinaryOperator<@Nullable Object> binaryOperator(int token) {
        return switch (token) {
            case TokenKinds.PLUS -> ALU::plus;
            case TokenKinds.MINUS -> ALU::minus;
            case TokenKinds.MULT -> ALU::multi;
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
