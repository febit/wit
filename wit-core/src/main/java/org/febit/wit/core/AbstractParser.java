// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.core;

import lombok.extern.slf4j.Slf4j;
import org.febit.wit.Engine;
import org.febit.wit.Feature;
import org.febit.wit.Script;
import org.febit.wit.exceptions.ParseException;
import org.febit.wit.exceptions.UncheckedException;
import org.febit.wit.runtime.ALU;
import org.febit.wit.runtime.Ast;
import org.febit.wit.runtime.AstUtils;
import org.febit.wit.runtime.FunctionDeclare;
import org.febit.wit.runtime.LoopFlag;
import org.febit.wit.runtime.Position;
import org.febit.wit.runtime.TextPosition;
import org.febit.wit.runtime.ast.AssignableExpression;
import org.febit.wit.runtime.ast.Expression;
import org.febit.wit.runtime.ast.IBlock;
import org.febit.wit.runtime.ast.ScriptAST;
import org.febit.wit.runtime.ast.Statement;
import org.febit.wit.runtime.ast.expr.AssignableSuppliedValue;
import org.febit.wit.runtime.ast.expr.BreakpointExpr;
import org.febit.wit.runtime.ast.expr.ContextUpstreamVar;
import org.febit.wit.runtime.ast.expr.ContextVar;
import org.febit.wit.runtime.ast.expr.DirectValue;
import org.febit.wit.runtime.ast.expr.FunctionCallExpr;
import org.febit.wit.runtime.ast.expr.JavaStaticFieldExpr;
import org.febit.wit.runtime.ast.expr.NewMapExpr;
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
import org.febit.wit.runtime.ast.stat.TryPart;
import org.febit.wit.runtime.extra.ast.DynamicNativeMethodCallExpr;
import org.febit.wit.util.ClassNameRope;
import org.febit.wit.util.ClassUtils;
import org.febit.wit.util.Stack;
import org.febit.wit.util.StringUtils;
import org.jspecify.annotations.Nullable;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BinaryOperator;
import java.util.function.UnaryOperator;

@Slf4j
abstract class AbstractParser {

    //Self Operators
    static final int OP_PLUSEQ = 0;
    static final int OP_MINUSEQ = 1;
    static final int OP_MULTEQ = 2;
    static final int OP_DIVEQ = 3;
    static final int OP_MODEQ = 4;
    static final int OP_LSHIFTEQ = 5;
    static final int OP_RSHIFTEQ = 6;
    static final int OP_URSHIFTEQ = 7;
    static final int OP_ANDEQ = 8;
    static final int OP_XOREQ = 9;
    static final int OP_OREQ = 10;

    /* Base Parser */
    private static final short[][] PRODUCTION_TABLE = loadData("Production");
    private static final short[][] ACTION_TABLE = loadData("Action");
    private static final short[][] REDUCE_TABLE = loadData("Reduce");
    private static final String[] SYMBOL_STRS = {
            "EOF", //EOF
            "ERROR", //ERROR
            "var", //VAR
            "if", //IF
            "else", //ELSE
            "for", //FOR
            "this", //THIS
            "super", //SUPER
            "switch", //SWITCH
            "case", //CASE
            "default", //DEFAULT
            "do", //DO
            "while", //WHILE
            "throw", //THROW
            "try", //TRY
            "catch", //CATCH
            "finally", //FINALLY
            "new", //NEW
            "instanceof", //INSTANCEOF
            "function", //FUNCTION
            "echo", //ECHO
            "static", //STATIC
            "native", //NATIVE
            "import", //IMPORT
            "include", //INCLUDE
            "@import", //NATIVE_IMPORT
            "break", //BREAK
            "continue", //CONTINUE
            "return", //RETURN
            "++", //PLUSPLUS
            "--", //MINUSMINUS
            "+", //PLUS
            "-", //MINUS
            "*", //MULT
            "/", //DIV
            "%", //MOD
            "<<", //LSHIFT
            ">>", //RSHIFT
            ">>>", //URSHIFT
            "<", //LT
            ">", //GT
            "<=", //LTEQ
            ">=", //GTEQ
            "==", //EQEQ
            "!=", //NOTEQ
            "&", //AND
            "^", //XOR
            "|", //OR
            "~", //COMP
            "&&", //ANDAND
            "||", //OROR
            "!", //NOT
            "?", //QUESTION
            "*=", //SELFEQ
            "-", //UMINUS
            ".", //DOT
            ":", //COLON
            "::", //COLONCOLON
            ",", //COMMA
            ";", //SEMICOLON
            "{", //LBRACE
            "}", //RBRACE
            "}", //INTERPOLATION_END
            "(", //LPAREN
            ")", //RPAREN
            "[", //LBRACK
            "]", //RBRACK
            "[?", //LDEBUG
            "?]", //RDEBUG
            "[?]", //LRDEBUG
            "=>", //EQGT
            ")->", //RPAREN_MINUSGT
            "->", //MINUSGT
            ".~", //DYNAMIC_DOT
            "..", //DOTDOT
            "=", //EQ
            "`", //TEMPLATE_STRING_START
            "}", //TEMPLATE_STRING_INTERPOLATION_END
            "${", //TEMPLATE_STRING_INTERPOLATION_START
            "`", //TEMPLATE_STRING_END
            "IDENTIFIER", //IDENTIFIER
            "::", //METHOD_REFERENCE
            "TEXT", //TEXT_STATEMENT
            "DIRECT_VALUE", // DIRECT_VALUE
            "const", //CONST
            "UNKNOWN"
    };

    private final Map<String, String> importedClasses = new HashMap<>();
    private final Map<@Nullable String, Integer> labelIndexMap = new HashMap<>();
    private final AtomicInteger nextLabelIndex = new AtomicInteger();

    private TextStatementFactory textStatementFactory;
    private Engine engine;
    private NativeFactory nativeFactory;

    protected final Stack<Symbol> symbolStack = new Stack<>(24);
    protected Script script;
    protected VariantManager variants;

    /**
     * Current source version.
     */
    private long lastSourceVersion;

    /**
     * flag to stop parser
     */
    protected boolean goonParse;

    AbstractParser() {
    }

    private static short getAction(short[] row, final int sym) {
        final int len = row.length;
        int probe;
        /* linear search if we are < 10 entries, otherwise binary search */
        if (len < 20) {
            for (probe = 0; probe < len; probe++) {
                if (row[probe++] == sym) {
                    return row[probe];
                }
            }
        } else {
            int first = 0;
            int last = (len - 1) >> 1;
            int probe2;
            while (first <= last) {
                probe = (first + last) >> 1;
                probe2 = probe << 1;
                if (sym == row[probe2]) {
                    return row[probe2 + 1];
                } else if (sym > row[probe2]) {
                    first = probe + 1;
                } else {
                    last = probe - 1;
                }
            }
        }
        //error
        return 0;
    }

    @SuppressWarnings({
            "squid:ForLoopCounterChangedCheck"
    })
    private static short getReduce(short @Nullable [] row, int sym) {
        if (row != null) {
            for (int probe = 0, len = row.length; probe < len; probe++) {
                if (row[probe++] == sym) {
                    return row[probe];
                }
            }
        }
        //error
        return -1;
    }

    private static short[][] loadData(String name) {
        try (ObjectInputStream in = new ObjectInputStream(
                ClassUtils.getDefaultClassLoader().getResourceAsStream("org/febit/wit/core/Parser$" + name + ".data")
        )) {
            return (short[][]) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new UncheckedException(e);
        }
    }

    private static String getSimpleHintMessage(Symbol symbol) {
        final short[] row = ACTION_TABLE[symbol.state];
        final int len = row.length;
        if (len == 0) {
            return "[no hints]";
        }
        final boolean higherLevel = len > 8;
        if (higherLevel && getAction(row, Tokens.SEMICOLON) != 0) {
            return "forget ';' ?";
        }
        final StringBuilder sb = new StringBuilder();
        boolean notFirst = false;
        short sym;
        for (int i = 0; i < len; i += 2) {
            sym = row[i];
            if (higherLevel && !isHintLevelOne(sym)) {
                continue;
            }
            if (notFirst) {
                sb.append(", ");
            } else {
                notFirst = true;
            }
            sb.append('\'')
                    .append(symbolToString(sym))
                    .append('\'');
        }
        return sb.toString();
    }

    private static boolean isHintLevelOne(short sym) {
        return switch (sym) {
            case Tokens.COLON, //":"
                 Tokens.SEMICOLON,  //";"
                 Tokens.RBRACE, //"}"
                 Tokens.INTERPOLATION_END,  //"}"
                 Tokens.RPAREN,  //")"
                 Tokens.RBRACK,  //"]"
                 Tokens.IDENTIFIER, //"IDENTIFIER"
                 Tokens.DIRECT_VALUE  //"DIRECT_VALUE"
                    -> true;
            default -> false;
        };
    }

    private static String symbolToString(short sym) {
        if (sym >= 0 && sym < SYMBOL_STRS.length) {
            return SYMBOL_STRS[sym];
        }
        return "UNKNOWN";
    }

    public static ScriptAST parse(Script script) throws ParseException {
        return new Parser().doParse(script);
    }

    @Nullable
    abstract Object doAction(int actionId) throws ParseException;

    static ParseException unsupportedOperator(Position position) {
        return new ParseException("Unsupported Operator", position);
    }

    @SuppressWarnings({
            "squid:S135", // Loops should not contain more than a single "break" or "continue" statement
            "java:S6541", // Methods should not perform too many tasks (aka Brain method)
            "squid:S3776" // Cognitive Complexity of methods should not be too high
    })
    private Symbol process(Lexer lexer) throws IOException {

        int act;
        Symbol pending;
        Symbol currentSymbol;
        final Stack<Symbol> stack = this.symbolStack;
        stack.clear();

        //Start Symbol
        currentSymbol = new Symbol(0, TextPosition.UNKNOWN, null);
        currentSymbol.state = 0;
        stack.push(currentSymbol);

        var looseSemicolon = this.engine.isEnabled(Feature.LOOSE_SEMICOLON);

        Symbol pendingPending = null;
        pending = lexer.nextToken();

        goonParse = true;
        do {

            // look up action out of the current state with the current input
            act = getAction(ACTION_TABLE[currentSymbol.state], pending.id);

            // decode the action -- > 0 encodes shift
            if (act > 0) {
                // shift to the encoded state by pushing it on the _stack
                pending.state = act - 1;
                stack.push(pending);
                currentSymbol = pending;
                // advance to the next Symbol

                // next token
                if (pendingPending != null) {
                    pending = pendingPending;
                    pendingPending = null;
                } else {
                    pending = lexer.nextToken();
                    if (looseSemicolon
                            && currentSymbol.isOnEdgeOfNewLine) {
                        switch (pending.id) {
                            case Tokens.LBRACK: // NOSONAR squid:S128 Switch cases should end with an unconditional "break" statement
                                if (currentSymbol.id == Tokens.COMMA
                                        || currentSymbol.id == Tokens.LBRACE) {
                                    break;
                                }
                            case Tokens.LBRACE:
                            case Tokens.LPAREN:
                            case Tokens.PLUSPLUS:
                            case Tokens.MINUSMINUS:
                                pendingPending = pending;
                                pending = createLooseSemicolonSymbol(pendingPending);
                                break;
                            default:
                                // Do nothing
                        }
                    }
                }
                if (looseSemicolon
                        && pendingPending == null
                        && pending.isOnEdgeOfNewLine) {
                    switch (pending.id) {
                        case Tokens.RETURN,
                             Tokens.BREAK,
                             Tokens.CONTINUE -> pendingPending = createLooseSemicolonSymbol(pending);
                        default -> {
                            // Do nothing
                        }
                    }
                }
                continue;
            }
            // assert act <=0
            if (act == 0
                    && looseSemicolon
                    && pending.id != Tokens.SEMICOLON
                    && (currentSymbol.isOnEdgeOfNewLine || pending.id == Tokens.RBRACE)) {
                act = getAction(ACTION_TABLE[currentSymbol.state], Tokens.SEMICOLON);
                if (act != 0) {
                    pendingPending = pending;
                    pending = createLooseSemicolonSymbol(pendingPending);
                    if (act > 0) {
                        // go back to do
                        continue;
                    }
                }
            }
            if (act == 0) {
                throw new ParseException("Syntax error at line " + lexer.getLine()
                        + " column " + lexer.getColumn()
                        + ", Hints: " + getSimpleHintMessage(currentSymbol),
                        TextPosition.of(lexer.getLine(), lexer.getColumn())
                );
            }
            boolean isLastSymbolOnEdgeOfNewLine = currentSymbol.isOnEdgeOfNewLine;
            // if its less than zero, then it encodes a reduce action
            act = (-act) - 1;
            final Object result = doAction(act);
            final short[] row = PRODUCTION_TABLE[act];
            final int symId = row[0];
            final int handleSize = row[1];
            if (handleSize == 0) {
                currentSymbol = new Symbol(symId, TextPosition.UNKNOWN, result);
            } else {
                //position based on left
                currentSymbol = new Symbol(symId, stack.peek(handleSize - 1).pos, result);
                //pop the handle
                stack.pops(handleSize);
            }

            // look up the state to go to from the one popped back to shift to that state
            currentSymbol.state = getReduce(REDUCE_TABLE[stack.peek().state], symId);
            currentSymbol.isOnEdgeOfNewLine = isLastSymbolOnEdgeOfNewLine;
            stack.push(currentSymbol);
        } while (goonParse);

        return stack.peek();
    }

    private Symbol createLooseSemicolonSymbol(Symbol referSymbol) {
        return new Symbol(Tokens.SEMICOLON, referSymbol.pos, null);
    }

    /**
     * @param script Script
     * @return ScriptAST
     * @throws ParseException ParseException
     */
    protected ScriptAST doParse(
            Script script
    ) throws ParseException {
        var myEngine = script.engine();
        var source = script.source();
        this.script = script;
        this.engine = myEngine;

        this.textStatementFactory = myEngine.textStatementFactory();
        this.nativeFactory = myEngine.nativeFactory();

        this.variants = new VariantManager(myEngine);
        this.labelIndexMap.put(null, 0);
        this.nextLabelIndex.set(1);
        Lexer lexer = null;
        try {
            // get source version before open it, may less than actual value.
            this.lastSourceVersion = source.version();
            //ISSUE: LexerProvider
            lexer = new Lexer(source.openReader());
            lexer.setTrimCodeBlockBlankLine(myEngine.isEnabled(Feature.TRIM_CODE_BLOCK_BLANK_LINE));
            if (source.codeFirst()) {
                lexer.codeFirst();
            }
            lexer.setOffset(source);
            this.textStatementFactory.onParserStarted(script);

            var ast = this.process(lexer).value;
            Objects.requireNonNull(ast, "Parser result is null.");
            return (ScriptAST) ast;
        } catch (ParseException e) {
            throw e;
        } catch (Exception e) {
            throw new ParseException(e);
        } finally {
            this.textStatementFactory.onParserCompleted(script);
            if (lexer != null) {
                try {
                    lexer.close();
                } catch (IOException ex) {
                    log.warn("Failed to close lexer.", ex);
                }
            }
        }
    }

    Class<?> toClass(String className) {
        int arrayDept = 0;
        int flag = className.indexOf('[');
        if (flag >= 0) {
            // figure out array dept
            for (char c : className.substring(flag).toCharArray()) {
                if (c == '[') {
                    arrayDept++;
                }
            }
            className = className.substring(0, flag).trim();
        }
        String classFullName = resolveClassFullName(className);
        try {
            return ClassUtils.loadByName(classFullName, arrayDept);
        } catch (ClassNotFoundException ex) {
            throw new ParseException("Class<?> not found:" + classFullName, ex);
        }
    }

    String resolveClassFullName(String className) {

        // 0. full name
        if (className.indexOf('.') >= 0) {
            return className;
        }

        //1. find from @imports
        String fullName = importedClasses.get(className);
        if (fullName != null) {
            return fullName;
        }
        Class<?> cls;

        // 2. find as primitive type
        cls = ClassUtils.findPrimitiveClass(className);
        if (cls != null) {
            return className;
        }

        // 3. find as java.lang.*
        try {
            cls = ClassUtils.loadByName("java.lang.".concat(className));
        } catch (Exception ignore) {
            // Ignore
        }
        if (cls != null) {
            return cls.getName();
        }

        // failed, just return
        return className;
    }

    void registerClass(ClassNameRope rope, Position position) throws ParseException {
        var simpleName = rope.simpleName();
        var componentName = rope.componentName();
        if (simpleName == null || componentName == null) {
            // Ignore empty class name
            return;
        }
        if (ClassUtils.findPrimitiveClass(simpleName) != null) {
            throw new ParseException("Cannot import primitive type:" + simpleName, position);
        }
        var existing = importedClasses.get(simpleName);
        if (existing != null) {
            if (existing.equals(componentName)) {
                return;
            }
            throw new ParseException("Ambiguous import for class name: " + simpleName
                    + ", exists: " + existing + ", new: " + componentName, position);
        }
        importedClasses.put(simpleName, componentName);
    }

    int getLabelIndex(String label) {
        return labelIndexMap.computeIfAbsent(label,
                l -> nextLabelIndex.getAndIncrement());
    }

    Class<?> toClass(ClassNameRope rope, Position position) throws ParseException {
        var compName = rope.componentName();
        if (compName == null) {
            throw new ParseException("Empty class name.", position);
        }
        var classFullName = resolveClassFullName(compName);
        try {
            return ClassUtils.loadByName(classFullName, rope.arrayDepth());
        } catch (ClassNotFoundException ex) {
            throw new ParseException("Class<?> not found:" + classFullName, ex, position);
        }
    }

    Expression createAssign(AssignableExpression lexpr, Expression rexpr, Position position) {
        return new Assign(lexpr, rexpr, position);
    }

    Expression createGroupAssign(Expression[] lexprs, Expression rexpr, Position position) {
        var assignables = new AssignableExpression[lexprs.length];
        for (int i = 0; i < lexprs.length; i++) {
            assignables[i] = castToAssignableExpression(lexprs[i]);
        }
        return new GroupAssign(assignables, rexpr, position);
    }

    Expression createBreakpointExpression(@Nullable Expression labelExpr, Expression expr, Position position) {
        var label = labelExpr == null ? null : AstUtils.evalConst(labelExpr);
        return new BreakpointExpr(label, expr, position);
    }

    Statement createBreakpointStatement(@Nullable Expression labelExpr, @Nullable Statement statement, Position position) {
        var label = labelExpr == null ? null : AstUtils.evalConst(labelExpr);
        return new BreakpointStatement(label, statement, position);
    }

    Statement createTextStatement(char @Nullable [] text, Position position) {
        if (text == null || text.length == 0) {
            return NoopStatement.INSTANCE;
        }
        return this.textStatementFactory.create(script, text, position);
    }

    ContextVar declareVarAndCreateContextValue(String name, Position position) {
        return new ContextVar(variants.assignVar(name, position), position);
    }

    ContextVar[] declareVarAndCreateContextValues(List<String> names, Position position) {
        var contextVars = new ContextVar[names.size()];
        for (int i = 0; i < names.size(); i++) {
            contextVars[i] = declareVarAndCreateContextValue(names.get(i), position);
        }
        return contextVars;
    }

    NewMapExpr createMapValue(@Nullable List<Expression[]> propertyDefList, Position position) {
        if (propertyDefList == null || propertyDefList.isEmpty()) {
            return new NewMapExpr(AstUtils.emptyExpressions(), AstUtils.emptyExpressions(), position);
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

    DirectValue toDirectValue(Symbol sym) {
        return Ast.directValue(sym.pos, sym.value);
    }

    Expression createContextValue(VarAddress addr, Position position) {
        return switch (addr.kind()) {
            case CONST -> new DirectValue(addr.constValue(), position);
            case UPSTREAM -> new ContextUpstreamVar(addr.pageOffset(), addr.index(), position);
            case CONTEXT -> new ContextVar(addr.index(), position);
            case STATIC_VAR -> {
                if (!(addr.constValue() instanceof String name)) {
                    throw new ParseException("Name of static variable must be a string.", position);
                }
                yield AssignableSuppliedValue.ofStatic(
                        this.engine.staticHeaps().variant(), name, position
                );
            }
        };
    }

    Expression createContextValue(int frameOffset, String name, Position position) {
        var addr = variants.locate(name, frameOffset, !engine.isEnabled(Feature.LOOSE_VAR), position);
        return createContextValue(addr, position);
    }

    void assignConst(String name, Expression expr, Position position) {
        variants.assignConst(name, AstUtils.evalConst(expr), position);
    }

    Statement createInterpolation(final Expression expr) {
        return new Interpolation(expr);
    }

    Expression createNativeStaticValue(ClassNameRope rope, Position position) {
        if (rope.size() <= 1) {
            throw new ParseException("native static need a field name.", position);
        }
        var fieldName = rope.pop();
        var clazz = toClass(rope, position);
        var path = clazz.getName() + '.' + fieldName;
        if (!this.nativeFactory.security().allowed(path)) {
            throw new ParseException("Inaccessible native path: " + path, position);
        }
        final Field field;
        try {
            field = clazz.getField(fieldName);
        } catch (NoSuchFieldException ex) {
            throw new ParseException("No such field: " + path, ex, position);
        }
        if (ClassUtils.isStatic(field)) {
            ClassUtils.setAccessible(field);
            if (ClassUtils.isFinal(field)) {
                try {
                    return new DirectValue(field.get(null), position);
                } catch (IllegalArgumentException | IllegalAccessException ex) {
                    throw new ParseException("Failed to get static field value: " + path, ex, position);
                }
            } else {
                return new JavaStaticFieldExpr(field, position);
            }
        } else {
            throw new ParseException("No a static field: " + path, position);
        }
    }

    Expression createNativeNewArrayDeclareExpression(Class<?> componentType, Position position) {
        return new DirectValue(this.nativeFactory.getNativeNewArrayMethodDeclare(componentType, position, true),
                position);
    }

    Expression createNativeMethodDeclareExpression(
            Class<?> clazz, String methodName, @Nullable List<Class<?>> list, Position position) {
        return new DirectValue(this.nativeFactory.getNativeMethodDeclare(clazz, methodName,
                list == null ? new Class[0] : list.toArray(new Class[0]),
                position, true), position);
    }

    Expression createMethodReference(String ref, Position position) {
        int split = ref.indexOf("::");
        String className = ref.substring(0, split).trim();
        String method = ref.substring(split + 2).trim();
        FunctionDeclare functionDeclare;
        Class<?> cls = toClass(className);
        if ("new".equals(method)) {
            if (cls.isArray()) {
                functionDeclare = this.nativeFactory.getNativeNewArrayMethodDeclare(cls.getComponentType(),
                        position, true);
            } else {
                functionDeclare = this.nativeFactory.getNativeConstructorDeclare(cls, position, true);
            }
        } else {
            functionDeclare = this.nativeFactory.getNativeMethodDeclare(cls, method, position, true);
        }
        return new DirectValue(functionDeclare, position);
    }

    Expression createNativeConstructorDeclareExpression(
            Class<?> clazz, @Nullable List<Class<?>> list, Position position) {
        return new DirectValue(this.nativeFactory.getNativeConstructorDeclare(clazz,
                list == null ? new Class[0] : list.toArray(new Class[0]),
                position, true), position);
    }

    Statement declareVar(String ident, Position position) {
        //XXX: Should Check var used before init
        variants.assignVar(ident, position);
        return NoopStatement.INSTANCE;
    }

    Statement createIfStatement(Expression ifExpr, Statement thenStatement,
                                Statement elseStatement, Position position) {
        thenStatement = AstUtils.optimize(thenStatement);
        elseStatement = AstUtils.optimize(elseStatement);
        if (!(thenStatement instanceof NoopStatement)) {
            if (elseStatement instanceof NoopStatement) {
                return new If(ifExpr, thenStatement, position);
            } else {
                return new IfElse(ifExpr, thenStatement, elseStatement, position);
            }
        } else if (!(elseStatement instanceof NoopStatement)) {
            return new IfNot(ifExpr, elseStatement, position);
        } else {
            return NoopStatement.INSTANCE;
        }
    }

    Statement createStatementGroup(List<Statement> list, Position position) {
        return new StatementGroup(AstUtils.flatStatements(list), position);
    }

    Expression createMethodExecute(Expression funcExpr, Expression[] paramExprs, Position position) {
        AstUtils.optimize(paramExprs);
        funcExpr = AstUtils.optimize(funcExpr);
        return new FunctionCallExpr(funcExpr, paramExprs, position);
    }

    Expression createDynamicNativeMethodExecute(
            Expression thisExpr, String func, Expression[] paramExprs, Position position) {
        AstUtils.optimize(paramExprs);
        thisExpr = AstUtils.optimize(thisExpr);
        return new DynamicNativeMethodCallExpr(thisExpr, func, paramExprs, position);
    }

    ScriptAST createScriptAST(List<Statement> list) {
        var statements = AstUtils.flatStatements(list);
        var loops = AstUtils.collectLoopFlags(statements);
        if (!loops.isEmpty()) {
            throw new ParseException("loop overflow: " + StringUtils.join(loops, ','));
        }
        return new ScriptAST(variants.constructIndexers(), statements, variants.varCounter(), this.lastSourceVersion);
    }

    IBlock createBlock(@Nullable List<Statement> list, int varIndexer, Position position) {
        var statements = AstUtils.flatStatements(list);
        var loops = AstUtils.collectLoopFlags(statements);
        return loops.isEmpty()
                ? new BlockWithoutLoops(varIndexer, statements, position)
                : new Block(varIndexer, statements, loops.toArray(new LoopFlag[0]), position);
    }

    AssignableExpression castToAssignableExpression(Expression expr) {
        if (expr instanceof AssignableExpression assign) {
            return assign;
        }
        throw new ParseException("expression is not assignable", expr.position());
    }

    TryPart createTryPart(List<Statement> list, int varIndexer, Position position) {
        return new TryPart(createBlock(list, varIndexer, position), position);
    }

    Expression createSelfOperator(Expression lexpr, int sym, Expression rightExpr, Position position) {
        var leftExpr = castToAssignableExpression(lexpr);
        var biFunc = getBinaryOperator(sym);
        if (biFunc == null) {
            throw unsupportedOperator(position);
        }
        var optimized = AstUtils.optimize(
                new SelfOperator(leftExpr, rightExpr, biFunc, position)
        );
        Objects.requireNonNull(optimized);
        return optimized;
    }

    @Nullable
    BinaryOperator<@Nullable Object> getBinaryOperator(int op) {
        return switch (op) {
            case OP_PLUSEQ, Tokens.PLUS -> ALU::plus;
            case OP_MINUSEQ, Tokens.MINUS -> ALU::minus;
            case OP_MULTEQ, Tokens.MULT -> ALU::multi;
            case OP_DIVEQ, Tokens.DIV -> ALU::div;
            case OP_MODEQ, Tokens.MOD -> ALU::mod;
            case OP_LSHIFTEQ, Tokens.LSHIFT -> ALU::lshift;
            case OP_RSHIFTEQ, Tokens.RSHIFT -> ALU::rshift;
            case OP_URSHIFTEQ, Tokens.URSHIFT -> ALU::urshift;
            case Tokens.LT -> ALU::less;
            case Tokens.GT -> ALU::greater;
            case Tokens.LTEQ -> ALU::lessEqual;
            case Tokens.GTEQ -> ALU::greaterEqual;
            case Tokens.EQEQ -> ALU::isEqual;
            case Tokens.NOTEQ -> ALU::isNotEqual;
            case OP_ANDEQ, Tokens.AND -> ALU::bitAnd;
            case OP_XOREQ, Tokens.XOR -> ALU::bitXor;
            case OP_OREQ, Tokens.OR -> ALU::bitOr;
            default -> null;
        };
    }

    Expression createOperator(Expression expr, Symbol sym) {
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

    Expression createBiOperator(Expression leftExpr, Symbol sym, Expression rightExpr) {
        if (!(sym.value instanceof Integer token)) {
            throw unsupportedOperator(sym.pos);
        }
        var op = switch (token) {
            case Tokens.ANDAND -> new And(leftExpr, rightExpr, sym.pos);
            case Tokens.OROR -> new Or(leftExpr, rightExpr, sym.pos);
            case Tokens.DOTDOT -> new IntStep(leftExpr, rightExpr, sym.pos);
            default -> {
                var biFunc = getBinaryOperator(token);
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

}
