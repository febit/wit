// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.parser;

import lombok.extern.slf4j.Slf4j;
import org.febit.wit.Feature;
import org.febit.wit.Script;
import org.febit.wit.exception.ParseException;
import org.febit.wit.exception.UncheckedException;
import org.febit.wit.runtime.ast.AstUtils;
import org.febit.wit.runtime.ast.Expression;
import org.febit.wit.runtime.ast.Position;
import org.febit.wit.runtime.ast.ScriptAST;
import org.febit.wit.runtime.ast.Statement;
import org.febit.wit.runtime.ast.TextPosition;
import org.febit.wit.runtime.ast.expr.AssignableSuppliedValue;
import org.febit.wit.runtime.ast.expr.ContextUpstreamVar;
import org.febit.wit.runtime.ast.expr.ContextVar;
import org.febit.wit.runtime.ast.expr.DirectValue;
import org.febit.wit.runtime.ast.expr.JavaStaticFieldExpr;
import org.febit.wit.runtime.ast.stat.NoopStatement;
import org.febit.wit.runtime.function.FunctionDeclare;
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

@Slf4j
abstract class AbstractParser {

    /* Base Parser */
    private static final short[][] PRODUCTION_TABLE = loadData("Production");
    private static final short[][] ACTION_TABLE = loadData("Action");
    private static final short[][] REDUCE_TABLE = loadData("Reduce");
    private static final String[] SYMBOL_NAMES = {
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

    private int features;
    private TextStatementFactory textStatementFactory;
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
                ClassUtils.getDefaultClassLoader().getResourceAsStream("org/febit/wit/parser/Parser$" + name + ".data")
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
        if (sym >= 0 && sym < SYMBOL_NAMES.length) {
            return SYMBOL_NAMES[sym];
        }
        return "UNKNOWN";
    }

    public static ScriptAST parse(Script script) throws ParseException {
        return new Parser().doParse(script);
    }

    @Nullable
    abstract Object doAction(int actionId) throws ParseException;

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

        var looseSemicolon = Feature.LOOSE_SEMICOLON.isEnabled(this.features);

        Symbol pendingPending = null;
        pending = lexer.nextToken();

        goonParse = true;
        do {

            // look up action out of the current state with the current input
            act = getAction(ACTION_TABLE[currentSymbol.state], pending.kind);

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
                        switch (pending.kind) {
                            case Tokens.LBRACK: // NOSONAR squid:S128 Switch cases should end with an unconditional "break" statement
                                if (currentSymbol.kind == Tokens.COMMA
                                        || currentSymbol.kind == Tokens.LBRACE) {
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
                    switch (pending.kind) {
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
                    && pending.kind != Tokens.SEMICOLON
                    && (currentSymbol.isOnEdgeOfNewLine || pending.kind == Tokens.RBRACE)) {
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

    private Symbol createLooseSemicolonSymbol(Symbol refer) {
        return new Symbol(Tokens.SEMICOLON, refer.pos, null);
    }

    /**
     * @param script Script
     * @return ScriptAST
     * @throws ParseException ParseException
     */
    protected ScriptAST doParse(
            Script script
    ) throws ParseException {
        var engine = script.engine();
        var source = script.source();

        this.script = script;
        this.features = engine.features();
        this.textStatementFactory = engine.textStatementFactory();
        this.nativeFactory = engine.nativeFactory();

        this.variants = new VariantManager(engine);
        this.labelIndexMap.put(null, 0);
        this.nextLabelIndex.set(1);
        Lexer lexer = null;
        try {
            // get source version before open it, may less than actual value.
            this.lastSourceVersion = source.version();
            //ISSUE: LexerProvider
            lexer = new Lexer(source.openReader());
            lexer.setTrimCodeBlockBlankLine(engine.isEnabled(Feature.TRIM_CODE_BLOCK_BLANK_LINE));
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
                        this.variants.staticHeaps().variant(), name, position
                );
            }
        };
    }

    Expression createContextValue(int frameOffset, String name, Position position) {
        var addr = variants.locate(name, frameOffset, !Feature.LOOSE_VAR.isEnabled(this.features), position);
        return createContextValue(addr, position);
    }

    void assignConst(String name, Expression expr, Position position) {
        variants.assignConst(name, AstUtils.evalConst(expr), position);
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

    ScriptAST createScriptAST(List<Statement> list) {
        var statements = Ast.flatStatements(list);
        var loops = AstUtils.collectLoopFlags(statements);
        if (!loops.isEmpty()) {
            throw new ParseException("loop overflow: " + StringUtils.join(loops, ','));
        }
        return new ScriptAST(variants.constructIndexers(), statements, variants.varCounter(), this.lastSourceVersion);
    }

    TryPart createTryPart(List<Statement> list, int varIndexer, Position position) {
        return new TryPart(Ast.block(list, varIndexer, position), position);
    }

}
