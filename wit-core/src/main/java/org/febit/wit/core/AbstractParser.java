// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.core;

import jakarta.annotation.Nullable;
import org.febit.wit.Engine;
import org.febit.wit.Template;
import org.febit.wit.core.VariantManager.VarAddress;
import org.febit.wit.core.text.TextStatementFactory;
import org.febit.wit.exceptions.ParseException;
import org.febit.wit.exceptions.UncheckedException;
import org.febit.wit.lang.Ast;
import org.febit.wit.lang.AstUtils;
import org.febit.wit.lang.LoopMeta;
import org.febit.wit.lang.FunctionDeclare;
import org.febit.wit.lang.Position;
import org.febit.wit.lang.TextPosition;
import org.febit.wit.lang.ast.AssignableExpression;
import org.febit.wit.lang.ast.Expression;
import org.febit.wit.lang.ast.Statement;
import org.febit.wit.lang.ast.TemplateAST;
import org.febit.wit.lang.ast.expr.BreakpointExpr;
import org.febit.wit.lang.ast.expr.ScopedContextVar;
import org.febit.wit.lang.ast.expr.ContextVar;
import org.febit.wit.lang.ast.expr.DirectValue;
import org.febit.wit.lang.ast.expr.DynamicNativeMethodExecute;
import org.febit.wit.lang.ast.expr.AssignableSupplierValue;
import org.febit.wit.lang.ast.expr.MapValue;
import org.febit.wit.lang.ast.expr.FunctionCallExpr;
import org.febit.wit.lang.ast.expr.JavaStaticFieldExpr;
import org.febit.wit.lang.ast.oper.And;
import org.febit.wit.lang.ast.oper.Assign;
import org.febit.wit.lang.ast.oper.ConstableBiOperator;
import org.febit.wit.lang.ast.oper.ConstableUnaryOperator;
import org.febit.wit.lang.ast.oper.GroupAssign;
import org.febit.wit.lang.ast.oper.IntStep;
import org.febit.wit.lang.ast.oper.Or;
import org.febit.wit.lang.ast.oper.SelfOperator;
import org.febit.wit.lang.ast.stat.Block;
import org.febit.wit.lang.ast.stat.BlockNoLoops;
import org.febit.wit.lang.ast.stat.BreakpointStatement;
import org.febit.wit.lang.ast.stat.IBlock;
import org.febit.wit.lang.ast.stat.If;
import org.febit.wit.lang.ast.stat.IfElse;
import org.febit.wit.lang.ast.stat.IfNot;
import org.febit.wit.lang.ast.stat.Interpolation;
import org.febit.wit.lang.ast.stat.NoopStatement;
import org.febit.wit.lang.ast.stat.StatementGroup;
import org.febit.wit.lang.ast.stat.TryPart;
import org.febit.wit.loaders.Resource;
import org.febit.wit.loaders.ResourceOffset;
import org.febit.wit.security.NativeSecurityManager;
import org.febit.wit.lang.ALU;
import org.febit.wit.util.ClassNameBand;
import org.febit.wit.util.ClassUtil;
import org.febit.wit.util.ExceptionUtil;
import org.febit.wit.util.Stack;
import org.febit.wit.util.StringUtil;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * @author zqq90
 */
@SuppressWarnings({
        "WeakerAccess"
})
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
    private final Map<String, Integer> labelIndexMap = new HashMap<>();
    private final AtomicInteger nextLabelIndex = new AtomicInteger();

    private TextStatementFactory textStatementFactory;
    private NativeSecurityManager nativeSecurityManager;
    private Engine engine;
    private NativeFactory nativeFactory;
    private boolean locateVarForce;

    protected final Stack<Symbol> symbolStack = new Stack<>(24);
    protected Template template;
    protected VariantManager varmgr;

    /**
     * Caching current resource version.
     */
    private long lastResourceVersion;

    /**
     * flag to stop parser
     */
    protected boolean goonParse;

    AbstractParser() {
    }

    private static short getAction(final short[] row, final int sym) {
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
    private static short getReduce(@Nullable final short[] row, int sym) {
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
                ClassUtil.getDefaultClassLoader().getResourceAsStream("org/febit/wit/core/Parser$" + name + ".data")
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
        switch (sym) {
            case Tokens.COLON: //":"
            case Tokens.SEMICOLON: //";"
            case Tokens.RBRACE: //"}"
            case Tokens.INTERPOLATION_END: //"}"
            case Tokens.RPAREN: //")"
            case Tokens.RBRACK: //"]"
            case Tokens.IDENTIFIER: //"IDENTIFIER"
            case Tokens.DIRECT_VALUE: //"DIRECT_VALUE"
                return true;
            default:
                return false;
        }
    }

    private static String symbolToString(final short sym) {
        if (sym >= 0 && sym < SYMBOL_STRS.length) {
            return SYMBOL_STRS[sym];
        }
        return "UNKNOWN";
    }

    public static TemplateAST parse(final Template template) throws ParseException {
        return new Parser().doParse(template);
    }

    abstract Object doAction(int actionId) throws ParseException;

    static ParseException unsupportedOperator(Position position) {
        return new ParseException("Unsupported Operator", position);
    }

    @SuppressWarnings({
            "squid:S135", // Loops should not contain more than a single "break" or "continue" statement
            "squid:S3776" // Cognitive Complexity of methods should not be too high
    })
    private Symbol process(final Lexer lexer) throws IOException {

        int act;
        Symbol pending;
        Symbol currentSymbol;
        final Stack<Symbol> stack = this.symbolStack;
        stack.clear();

        //Start Symbol
        currentSymbol = new Symbol(0, TextPosition.UNKNOWN, null);
        currentSymbol.state = 0;
        stack.push(currentSymbol);

        final boolean looseSemicolon = this.engine.isLooseSemicolon();

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
                        case Tokens.RETURN:
                        case Tokens.BREAK:
                        case Tokens.CONTINUE:
                            pendingPending = createLooseSemicolonSymbol(pending);
                            break;
                        default:
                            // Do nothing
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
                throw new ParseException(StringUtil.format("Syntax error at line {} column {}, Hints: {}",
                        lexer.getLine(), lexer.getColumn(), getSimpleHintMessage(currentSymbol)),
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
     * @param template Template
     * @return TemplateAST
     * @throws ParseException ParseException
     */
    protected TemplateAST doParse(
            final Template template
    ) throws ParseException {
        final Engine myEngine = template.getEngine();
        final Resource resource = template.getResource();
        this.textStatementFactory = myEngine.get(TextStatementFactory.class);
        this.nativeSecurityManager = myEngine.get(NativeSecurityManager.class);
        this.template = template;
        this.engine = myEngine;
        this.locateVarForce = !myEngine.isLooseVar();
        this.nativeFactory = myEngine.getNativeFactory();
        this.varmgr = new VariantManager(myEngine);
        this.labelIndexMap.put(null, 0);
        this.nextLabelIndex.set(1);
        Lexer lexer = null;
        try {
            // get resource version before open it, may less than actual value.
            this.lastResourceVersion = resource.version();
            //ISSUE: LexerProvider
            lexer = new Lexer(resource.openReader());
            lexer.setTrimCodeBlockBlankLine(myEngine.isTrimCodeBlockBlankLine());
            if (resource.isCodeFirst()) {
                lexer.codeFirst();
            }
            if (resource instanceof ResourceOffset) {
                lexer.setOffset((ResourceOffset) resource);
            } else {
                lexer.setOffset(0, 0);
            }
            this.textStatementFactory.startTemplateParser(template);
            return (TemplateAST) this.process(lexer).value;
        } catch (ParseException e) {
            throw e;
        } catch (Exception e) {
            throw new ParseException(e);
        } finally {
            this.textStatementFactory.finishTemplateParser(template);
            if (lexer != null) {
                try {
                    lexer.close();
                } catch (IOException ex) {
                    this.engine.getLogger().warn("Failed to close lexer.", ex);
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
            return ClassUtil.getClass(classFullName, arrayDept);
        } catch (ClassNotFoundException ex) {
            throw new ParseException("Class<?> not found:".concat(classFullName), ex);
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
        cls = ClassUtil.getPrimitiveClass(className);
        if (cls != null) {
            return className;
        }

        // 3. find as java.lang.*
        try {
            cls = ClassUtil.getClass("java.lang.".concat(className));
        } catch (Exception ex) {
            ExceptionUtil.ignore(ex);
        }
        if (cls != null) {
            return cls.getName();
        }

        // failed, just return
        return className;
    }

    void registerClass(ClassNameBand classNameBand, Position position) throws ParseException {
        final String className = classNameBand.getClassSimpleName();
        if (ClassUtil.getPrimitiveClass(className) != null) {
            throw new ParseException("Duplicate class simple name:" + classNameBand.getClassPureName(), position);
        }
        if (importedClasses.containsKey(className)) {
            throw new ParseException("Duplicate class register:" + classNameBand.getClassPureName(), position);
        }
        importedClasses.put(className, classNameBand.getClassPureName());
    }

    int getLabelIndex(String label) {
        return labelIndexMap.computeIfAbsent(label,
                l -> nextLabelIndex.getAndIncrement());
    }

    Class<?> toClass(ClassNameBand classNameBand, Position position) throws ParseException {
        String classFullName = resolveClassFullName(classNameBand.getClassPureName());
        try {
            return ClassUtil.getClass(classFullName, classNameBand.getArrayDepth());
        } catch (ClassNotFoundException ex) {
            throw new ParseException("Class<?> not found:".concat(classFullName), ex, position);
        }
    }

    Expression createAssign(AssignableExpression lexpr, Expression rexpr, Position position) {
        return new Assign(lexpr, rexpr, position);
    }

    Expression createGroupAssign(Expression[] lexprs, Expression rexpr, Position position) {
        AssignableExpression[] resetableExprs = new AssignableExpression[lexprs.length];
        for (int i = 0; i < lexprs.length; i++) {
            resetableExprs[i] = castToAssignableExpression(lexprs[i]);
        }
        return new GroupAssign(resetableExprs, rexpr, position);
    }

    Expression createBreakpointExpression(@Nullable Expression labelExpr, Expression expr, Position position) {
        final Object label = labelExpr == null ? null : AstUtils.calcConst(labelExpr);

        return new BreakpointExpr(label, expr, position);
    }

    Statement createBreakpointStatement(@Nullable Expression labelExpr, Statement statement, Position position) {
        final Object label = labelExpr == null ? null : AstUtils.calcConst(labelExpr);

        return new BreakpointStatement(label, statement, position);
    }

    Statement createTextStatement(@Nullable char[] text, Position position) {
        if (text == null || text.length == 0) {
            return NoopStatement.INSTANCE;
        }
        return this.textStatementFactory.getTextStatement(template, text, position);
    }

    ContextVar declareVarAndCreateContextValue(String name, Position position) {
        return new ContextVar(varmgr.assignVariant(name, position), position);
    }

    ContextVar[] declareVarAndCreateContextValues(List<String> names, Position position) {
        ContextVar[] contextVars = new ContextVar[names.size()];
        for (int i = 0; i < names.size(); i++) {
            contextVars[i] = declareVarAndCreateContextValue(names.get(i), position);
        }
        return contextVars;
    }

    MapValue createMapValue(@Nullable List<Expression[]> propertyDefList, Position position) {
        if (propertyDefList == null || propertyDefList.isEmpty()) {
            return new MapValue(AstUtils.emptyExpressions(), AstUtils.emptyExpressions(), position);
        }
        int size = propertyDefList.size();
        Expression[] keys = new Expression[size];
        Expression[] values = new Expression[size];
        for (int i = 0; i < propertyDefList.size(); i++) {
            Expression[] def = propertyDefList.get(i);
            // assert def.length == 2
            keys[i] = def[0];
            values[i] = def[1];
        }
        return new MapValue(keys, values, position);
    }

    DirectValue toDirectValue(Symbol sym) {
        return Ast.directValue(sym.pos, sym.value);
    }

    AssignableSupplierValue createSupplierVarExpr(String name, Position position) {
        var mgr = this.engine.getGlobalManager();
        return new AssignableSupplierValue(() -> mgr.getGlobal(name), (v) -> mgr.setGlobal(name, v), position);
    }

    Expression createContextValue(VarAddress addr, Position position) {
        switch (addr.type) {
            case VarAddress.GLOBAL:
                return createSupplierVarExpr(addr.constValue.toString(), position);
            case VarAddress.CONST:
                return new DirectValue(addr.constValue, position);
            case VarAddress.SCOPE:
                return new ScopedContextVar(addr.scopeOffset, addr.index, position);
            default:
                //VarAddress.CONTEXT
                return new ContextVar(addr.index, position);
        }
    }

    Expression createContextValueAtUpstair(int upstair, String name, Position position) {
        return createContextValue(varmgr.locateAtUpstair(name, upstair, position), position);
    }

    Expression createContextValue(int upstair, String name, Position position) {
        return createContextValue(varmgr.locate(name, upstair, this.locateVarForce, position), position);
    }

    void assignConst(String name, Expression expr, Position position) {
        varmgr.assignConst(name, AstUtils.calcConst(expr), position);
    }

    Statement createInterpolation(final Expression expr) {
        return new Interpolation(expr);
    }

    Expression createNativeStaticValue(ClassNameBand classNameBand, Position position) {
        if (classNameBand.size() <= 1) {
            throw new ParseException("native static need a field name.", position);
        }
        final String fieldName = classNameBand.pop();
        final Class<?> clazz = toClass(classNameBand, position);
        final String path = clazz.getName() + '.' + fieldName;
        if (!this.nativeSecurityManager.access(path)) {
            throw new ParseException("Inaccessible native path: ".concat(path), position);
        }
        final Field field;
        try {
            field = clazz.getField(fieldName);
        } catch (NoSuchFieldException ex) {
            throw new ParseException("No such field: ".concat(path), ex, position);
        }
        if (ClassUtil.isStatic(field)) {
            ClassUtil.setAccessible(field);
            if (ClassUtil.isFinal(field)) {
                try {
                    return new DirectValue(field.get(null), position);
                } catch (IllegalArgumentException | IllegalAccessException ex) {
                    throw new ParseException("Failed to get static field value: ".concat(path), ex, position);
                }
            } else {
                return new JavaStaticFieldExpr(field, position);
            }
        } else {
            throw new ParseException("No a static field: ".concat(path), position);
        }
    }

    Expression createNativeNewArrayDeclareExpression(Class<?> componentType, Position position) {
        return new DirectValue(this.nativeFactory.getNativeNewArrayMethodDeclare(componentType, position, true),
                position);
    }

    Expression createNativeMethodDeclareExpression(Class<?> clazz, String methodName,
                                                   @Nullable List<Class> list, Position position) {
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

    Expression createNativeConstructorDeclareExpression(Class<?> clazz, List<Class> list, Position position) {
        return new DirectValue(this.nativeFactory.getNativeConstructorDeclare(clazz,
                list == null ? new Class[0] : list.toArray(new Class[0]),
                position, true), position);
    }

    Statement declareVar(String ident, Position position) {
        //XXX: Should Check var used before init
        varmgr.assignVariant(ident, position);
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
        return new StatementGroup(AstUtils.toStatementArray(list), position);
    }

    Expression createMethodExecute(Expression funcExpr, Expression[] paramExprs, Position position) {
        AstUtils.optimize(paramExprs);
        funcExpr = AstUtils.optimize(funcExpr);
        return new FunctionCallExpr(funcExpr, paramExprs, position);
    }

    Expression createDynamicNativeMethodExecute(Expression thisExpr, String func,
                                                Expression[] paramExprs, Position position) {
        AstUtils.optimize(paramExprs);
        thisExpr = AstUtils.optimize(thisExpr);
        return new DynamicNativeMethodExecute(thisExpr, func, paramExprs, position);
    }

    TemplateAST createTemplateAST(List<Statement> list) {
        Statement[] statements = AstUtils.toStatementArray(list);
        List<LoopMeta> loops = AstUtils.collectPossibleLoops(statements);
        if (!loops.isEmpty()) {
            throw new ParseException("loop overflow: " + StringUtil.join(loops, ','));
        }
        return new TemplateAST(varmgr.getIndexers(), statements, varmgr.getVarCount(), this.lastResourceVersion);
    }

    IBlock createIBlock(@Nullable List<Statement> list, int varIndexer, Position position) {
        var statements = AstUtils.toStatementArray(list);
        var loops = AstUtils.collectPossibleLoops(statements);
        return loops.isEmpty()
                ? new BlockNoLoops(varIndexer, statements, position)
                : new Block(varIndexer, statements, loops.toArray(new LoopMeta[0]), position);
    }

    AssignableExpression castToAssignableExpression(Expression expr) {
        if (expr instanceof AssignableExpression) {
            return (AssignableExpression) expr;
        }
        throw new ParseException("expression is not assignable", expr.getPosition());
    }

    TryPart createTryPart(List<Statement> list, int varIndexer, Position position) {
        return new TryPart(createIBlock(list, varIndexer, position), position);
    }

    Expression createSelfOperator(Expression lexpr, int sym, Expression rightExpr, Position position) {
        AssignableExpression leftExpr = castToAssignableExpression(lexpr);
        var biFunc = getBiFunctionForBiOperator(sym);
        if (biFunc == null) {
            throw unsupportedOperator(position);
        }
        return AstUtils.optimize(new SelfOperator(leftExpr, rightExpr, biFunc, position));
    }

    @Nullable
    BiFunction<Object, Object, Object> getBiFunctionForBiOperator(int op) {
        switch (op) {
            case OP_PLUSEQ:
            case Tokens.PLUS:
                return ALU::plus;
            case OP_MINUSEQ:
            case Tokens.MINUS:
                return ALU::minus;
            case OP_MULTEQ:
            case Tokens.MULT:
                return ALU::mult;
            case OP_DIVEQ:
            case Tokens.DIV:
                return ALU::div;
            case OP_MODEQ:
            case Tokens.MOD:
                return ALU::mod;
            case OP_LSHIFTEQ:
            case Tokens.LSHIFT:
                return ALU::lshift;
            case OP_RSHIFTEQ:
            case Tokens.RSHIFT:
                return ALU::rshift;
            case OP_URSHIFTEQ:
            case Tokens.URSHIFT:
                return ALU::urshift;
            case Tokens.LT:
                return ALU::less;
            case Tokens.GT:
                return ALU::greater;
            case Tokens.LTEQ:
                return ALU::lessEqual;
            case Tokens.GTEQ:
                return ALU::greaterEqual;
            case Tokens.EQEQ:
                return ALU::isEqual;
            case Tokens.NOTEQ:
                return ALU::notEqual;
            case OP_ANDEQ:
            case Tokens.AND:
                return ALU::bitAnd;
            case OP_XOREQ:
            case Tokens.XOR:
                return ALU::bitXor;
            case OP_OREQ:
            case Tokens.OR:
                return ALU::bitOr;
            default:
                return null;
        }
    }

    Expression createOperator(Expression expr, Symbol sym) {
        Function<Object, Object> func;
        switch ((Integer) sym.value) {
            case Tokens.COMP:
                func = ALU::bitNot;
                break;
            case Tokens.MINUS:
                func = ALU::negative;
                break;
            case Tokens.NOT:
                func = ALU::not;
                break;
            default:
                throw unsupportedOperator(sym.pos);
        }
        return AstUtils.optimize(new ConstableUnaryOperator(expr, func, sym.pos));
    }

    Expression createBiOperator(Expression leftExpr, Symbol sym, Expression rightExpr) {
        Expression op;
        switch ((Integer) sym.value) {
            case Tokens.ANDAND:
                op = new And(leftExpr, rightExpr, sym.pos);
                break;
            case Tokens.OROR:
                op = new Or(leftExpr, rightExpr, sym.pos);
                break;
            case Tokens.DOTDOT:
                op = new IntStep(leftExpr, rightExpr, sym.pos);
                break;
            default:
                var biFunc = getBiFunctionForBiOperator((Integer) sym.value);
                if (biFunc == null) {
                    throw unsupportedOperator(sym.pos);
                }
                op = new ConstableBiOperator(leftExpr, rightExpr, biFunc, sym.pos);
        }
        return AstUtils.optimize(op);
    }

}
