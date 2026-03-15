package org.febit.wit.parser;

import lombok.Builder;
import lombok.Singular;
import lombok.experimental.UtilityClass;
import org.febit.wit.Script;
import org.febit.wit.exception.ParseException;
import org.febit.wit.runtime.ALU;
import org.febit.wit.runtime.ast.AssignableExpression;
import org.febit.wit.runtime.ast.Expression;
import org.febit.wit.runtime.ast.FlowControl;
import org.febit.wit.runtime.ast.FlowControls;
import org.febit.wit.runtime.ast.IBlock;
import org.febit.wit.runtime.ast.Position;
import org.febit.wit.runtime.ast.Statement;
import org.febit.wit.runtime.ast.StatementUtils;
import org.febit.wit.runtime.ast.expr.Assign;
import org.febit.wit.runtime.ast.expr.BreakpointExpr;
import org.febit.wit.runtime.ast.expr.DirectValue;
import org.febit.wit.runtime.ast.expr.DynamicNativeMethodCaller;
import org.febit.wit.runtime.ast.expr.FixedPropertyAccess;
import org.febit.wit.runtime.ast.expr.FunctionCaller;
import org.febit.wit.runtime.ast.expr.GroupAssign;
import org.febit.wit.runtime.ast.expr.HeapValue;
import org.febit.wit.runtime.ast.expr.IfExpr;
import org.febit.wit.runtime.ast.expr.NewArray;
import org.febit.wit.runtime.ast.expr.NewMap;
import org.febit.wit.runtime.ast.expr.PropertyAccess;
import org.febit.wit.runtime.ast.expr.SuppliedValue;
import org.febit.wit.runtime.ast.expr.VariableHeapFrameValue;
import org.febit.wit.runtime.ast.expr.VariableHeapValue;
import org.febit.wit.runtime.ast.extra.Import;
import org.febit.wit.runtime.ast.oper.And;
import org.febit.wit.runtime.ast.oper.ConstableBiOperator;
import org.febit.wit.runtime.ast.oper.ConstableUnaryOperator;
import org.febit.wit.runtime.ast.oper.DecreaseAndGet;
import org.febit.wit.runtime.ast.oper.GetAndDecrease;
import org.febit.wit.runtime.ast.oper.GetAndIncrease;
import org.febit.wit.runtime.ast.oper.IncreaseAndGet;
import org.febit.wit.runtime.ast.oper.IntStep;
import org.febit.wit.runtime.ast.oper.Or;
import org.febit.wit.runtime.ast.oper.SelfCalcAndAssign;
import org.febit.wit.runtime.ast.statement.Block;
import org.febit.wit.runtime.ast.statement.BlockNonFlow;
import org.febit.wit.runtime.ast.statement.BreakpointStatement;
import org.febit.wit.runtime.ast.statement.DoWhile;
import org.febit.wit.runtime.ast.statement.DoWhileNonFlow;
import org.febit.wit.runtime.ast.statement.Echo;
import org.febit.wit.runtime.ast.statement.If;
import org.febit.wit.runtime.ast.statement.IfElse;
import org.febit.wit.runtime.ast.statement.IfNot;
import org.febit.wit.runtime.ast.statement.Interpolation;
import org.febit.wit.runtime.ast.statement.NoopStatement;
import org.febit.wit.runtime.ast.statement.RenderRedirect;
import org.febit.wit.runtime.ast.statement.Return;
import org.febit.wit.runtime.ast.statement.StatementBatch;
import org.febit.wit.runtime.ast.statement.StatementList;
import org.febit.wit.runtime.ast.statement.TryCatchFinally;
import org.febit.wit.runtime.ast.statement.TryFinally;
import org.febit.wit.runtime.ast.statement.While;
import org.febit.wit.runtime.ast.statement.WhileNonFlow;
import org.febit.wit.runtime.ast.template.TemplateStringValue;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;

@UtilityClass
public class Ast {

    private static final Expression[] EMPTY_EXPRESSIONS = new Expression[0];

    public static IfExpr ifExpr(
            Expression condition,
            Expression left,
            Expression right,
            Position pos
    ) {
        return new IfExpr(condition, left, right, pos);
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

    public static Expression assign(AssignableExpression target, Expression value, Position pos) {
        return new Assign(target, value, pos);
    }

    public static Expression groupAssign(Expression[] targets, Expression value, Position pos) {
        var assignables = new AssignableExpression[targets.length];
        for (int i = 0; i < targets.length; i++) {
            assignables[i] = castToAssignable(targets[i]);
        }
        return new GroupAssign(assignables, value, pos);
    }

    public static PropertyAccess property(Expression target, Expression property, Position pos) {
        return new PropertyAccess(target, property, pos);
    }

    public static FixedPropertyAccess property(Expression target, String property, Position pos) {
        return new FixedPropertyAccess(target, property, pos);
    }

    public static Statement interpolation(Expression value) {
        return new Interpolation(value, value.position());
    }

    public static DirectValue directValue(@Nullable Object value, Position pos) {
        return new DirectValue(value, pos);
    }

    public static DirectValue directValue(Token token) {
        return directValue(token.value, token.pos);
    }

    public static Statement breakpointStatement(
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

    public static SuppliedValue emptyArray(Position pos) {
        return new SuppliedValue(() -> new Object[0], pos);
    }

    public static Expression[] emptyExpressions() {
        return EMPTY_EXPRESSIONS;
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
        return new TemplateStringValue(toExpressionArray(segments), pos);
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

        var scope = body.scope();
        var batches = body.body();

        var controls = new ArrayList<FlowControl>();
        FlowControls.collect(controls::add, body);

        if (controls.isEmpty()) {
            if (batches.size() != 1) {
                throw new IllegalStateException("Unexpected multiple batches without flow control");
            }
            var batch0 = batches.get(0);
            return switch (kind) {
                case WHILE -> new WhileNonFlow(scope, condition, batch0, pos);
                case DO_WHILE -> new DoWhileNonFlow(scope, condition, batch0, pos);
            };
        }

        if (label == null) {
            label = 0;
        }
        var bubbled = List.copyOf(controls.stream()
                .filter(FlowControls.loopBubbleFilter(label))
                .toList());
        return switch (kind) {
            case WHILE -> new While(label, scope, condition, batches, bubbled, pos);
            case DO_WHILE -> new DoWhile(label, scope, condition, batches, bubbled, pos);
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
            Position pos
    ) {
        Objects.requireNonNull(body, "tryBody is required");
        Objects.requireNonNull(pos, "position is required");

        body = StatementUtils.optimize(body);

        if (catchBody != null) {
            Objects.requireNonNull(exceptionVarIndex,
                    "exceptionVarIndex is required when catchBody is provided");
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
        return new TryCatchFinally(exceptionVarIndex, body, catchBody, finallyBody, pos);
    }

    @Builder(
            builderMethodName = "importBuilder",
            builderClassName = "ImportBuilder"
    )
    private static Statement import0(
            Script script,
            Position pos,
            Expression path,
            @Nullable Expression params,
            @Singular("exportVar")
            List<ImportVar> exportVars
    ) {
        Objects.requireNonNull(script, "script is required");
        Objects.requireNonNull(path, "path is required");
        Objects.requireNonNull(pos, "position is required");

        path = StatementUtils.optimize(path);

        if (params != null) {
            params = StatementUtils.optimize(params);
        }

        var refer = script.path();
        if (exportVars.isEmpty()) {
            return new Import(path, params, null, null, refer, pos);
        }

        var vars = exportVars.stream()
                .map(ImportVar::name)
                .toArray(String[]::new);

        var targets = exportVars.stream()
                .map(ImportVar::target)
                .map(StatementUtils::optimize)
                .map(AssignableExpression.class::cast)
                .toArray(AssignableExpression[]::new);

        return new Import(path, params, vars, targets, refer, pos);
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

    public static Expression value(VarAddress addr, Position pos) {
        return switch (addr.kind()) {
            case VAR -> new VariableHeapValue(addr.index(), pos);
            case FRAME_VAR -> new VariableHeapFrameValue(addr.frameOffset(), addr.index(), pos);
            case DIRECT -> new DirectValue(addr.value(), pos);
            case HEAP -> {
                var key = Objects.requireNonNull(addr.key());
                var heap = Objects.requireNonNull(addr.heap());
                yield new HeapValue(
                        heap, key, pos
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
            arr[i] = StatementUtils.optimize(arr[i]);
        }
        return arr;
    }

    public static Statement statementList(List<Statement> list, Position pos) {
        return new StatementList(List.copyOf(list), pos);
    }

    public static Expression functionCall(
            Expression func, Expression[] params, Position pos) {
        StatementUtils.optimize(params);
        func = StatementUtils.optimize(func);
        return new FunctionCaller(func, params, pos);
    }

    public static Expression dynamicNativeMethodCall(
            Expression self, String method, Expression[] params, Position pos) {
        StatementUtils.optimize(params);
        self = StatementUtils.optimize(self);
        return new DynamicNativeMethodCaller(method, self, params, pos);
    }

    public static Statement ifStatement(
            Expression ifExpr,
            @Nullable Statement thenBody,
            @Nullable Statement elseBody,
            Position pos
    ) {
        thenBody = StatementUtils.optimize(thenBody);
        elseBody = StatementUtils.optimize(elseBody);
        if (!(thenBody instanceof NoopStatement)) {
            if (elseBody instanceof NoopStatement) {
                return new If(ifExpr, thenBody, pos);
            }
            return new IfElse(ifExpr, thenBody, elseBody, pos);
        }
        if (!(elseBody instanceof NoopStatement)) {
            return new IfNot(ifExpr, elseBody, pos);
        }
        return NoopStatement.INSTANCE;
    }

    public static IBlock block(@Nullable List<Statement> list, int scope, Position pos) {
        var controls = new ArrayList<FlowControl>();
        var batches = batch(list, controls::add);
        if (controls.isEmpty()) {
            if (batches.size() != 1) {
                throw new IllegalStateException("Unexpected multiple batches without flow control");
            }
            return new BlockNonFlow(scope, batches.get(0), pos);
        }

        return new Block(scope, batches, List.copyOf(controls), pos);
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
     * Batch statements, collect flow controls.
     *
     * @return always not empty, if no statement, return a batch with empty statements.
     */
    public static List<StatementBatch> batch(@Nullable List<Statement> list, Consumer<FlowControl> controlsCollector) {
        if (list == null || list.isEmpty()) {
            return List.of(StatementBatch.empty());
        }
        var flag = new AtomicBoolean();
        var collecting = (Consumer<FlowControl>) (ctrl -> {
            flag.set(true);
            controlsCollector.accept(ctrl);
        });

        var batches = new ArrayList<StatementBatch>();
        var current = new ArrayList<Statement>();

        flatAndOptimize(list, stat -> {
            current.add(stat);
            FlowControls.collect(collecting, stat);
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
        throw new ParseException("expression is not assignable", expr.position());
    }

    public static ParseException unsupportedOperator(Position pos) {
        return new ParseException("Unsupported Operator", pos);
    }

    public static Expression selfAssign(Expression target, Expression delta, int tokenKind, Position pos) {
        var assignable = castToAssignable(target);
        var biFunc = binaryOperator(tokenKind);
        if (biFunc == null) {
            throw unsupportedOperator(pos);
        }
        var optimized = StatementUtils.optimize(
                new SelfCalcAndAssign(assignable, delta, biFunc, pos)
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
            case TokenKinds.DOTDOT -> new IntStep(left, right, token.pos);
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
