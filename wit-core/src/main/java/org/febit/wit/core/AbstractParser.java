// Copyright (c) 2013-2016, febit.org. All Rights Reserved.
package org.febit.wit.core;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.febit.wit.Engine;
import org.febit.wit.Template;
import org.febit.wit.core.VariantManager.VarAddress;
import org.febit.wit.core.ast.*;
import org.febit.wit.core.ast.expressions.*;
import org.febit.wit.core.ast.operators.*;
import org.febit.wit.core.ast.statements.*;
import org.febit.wit.core.text.TextStatementFactory;
import org.febit.wit.debug.BreakPointListener;
import org.febit.wit.exceptions.ParseException;
import org.febit.wit.lang.MethodDeclare;
import org.febit.wit.loaders.Resource;
import org.febit.wit.loaders.ResourceOffset;
import org.febit.wit.util.ClassNameBand;
import org.febit.wit.util.ClassUtil;
import org.febit.wit.util.Stack;
import org.febit.wit.util.StatementUtil;
import org.febit.wit.util.StringUtil;

/**
 *
 * @author zqq90
 */
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

    final Stack<Symbol> symbolStack = new Stack<>(24);

    private final Map<String, String> importedClasses;
    private final Map<String, Integer> labelsIndexMap;

    private TextStatementFactory textStatementFactory;
    private BreakPointListener breakPointListener;
    private Engine engine;
    private NativeFactory nativeFactory;
    private boolean locateVarForce;
    private int currentLabelIndex;

    Template template;
    VariantManager varmgr;

    boolean goonParse;

    AbstractParser() {
        this.importedClasses = new HashMap<>();
        this.labelsIndexMap = new HashMap<>();
    }

    public TemplateAST parse(final Template template, BreakPointListener breakPointListener) throws ParseException {
        this.breakPointListener = breakPointListener;
        return parse(template);
    }

    abstract Object doAction(int actionId) throws ParseException;

    Symbol parse(final Lexer lexer) throws Exception {

        int act;
        Symbol pending;
        Symbol currentSymbol;
        final Stack<Symbol> stack = this.symbolStack;
        stack.clear();

        //Start Symbol
        currentSymbol = new Symbol(0, -1, -1, null);
        currentSymbol.state = 0;
        stack.push(currentSymbol);

        final short[][] actionTable = ACTION_TABLE;
        final short[][] reduceTable = REDUCE_TABLE;
        final short[][] productionTable = PRODUCTION_TABLE;
        final boolean looseSemicolon = this.engine.isLooseSemicolon();
        int looseSemicolonCounter = 0;

        Symbol pendingPending = null;
        pending = lexer.nextToken();

        goonParse = true;
        do {

            // look up action out of the current state with the current input
            act = getAction(actionTable[currentSymbol.state], pending.id);

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
                            case Tokens.LBRACE: // {
                            case Tokens.LBRACK: // [
                            case Tokens.LPAREN: // (
                            case Tokens.PLUSPLUS: // ++
                            case Tokens.MINUSMINUS: // --
                                pendingPending = pending;
                                pending = createLooseSemicolonSymbol(pendingPending);
                                looseSemicolonCounter++;
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
                        default:
                            // Do nothing
                    }
                }
                continue;
            }
            // assert act <=0
            if (act == 0
                    && looseSemicolon
                    && pending.id != Tokens.SEMICOLON) {
                if (currentSymbol.isOnEdgeOfNewLine
                        || pending.id == Tokens.RBRACE) {
                    act = getAction(actionTable[currentSymbol.state], Tokens.SEMICOLON);
                    if (act != 0) {
                        pendingPending = pending;
                        pending = createLooseSemicolonSymbol(pendingPending);
                        looseSemicolonCounter++;
                        if (act > 0) {
                            // go back to do  
                            continue;
                        }
                    }
                }
            }
            if (act == 0) {
                throw new ParseException(StringUtil.format("Syntax error at line {} column {}, Hints: {}", lexer.getLine(), lexer.getColumn(), getSimpleHintMessage(currentSymbol)), lexer.getLine(), lexer.getColumn());
            }
            boolean isLastSymbolOnEdgeOfNewLine = currentSymbol.isOnEdgeOfNewLine;
            // if its less than zero, then it encodes a reduce action
            act = (-act) - 1;
            final int symId, handleSize;
            final Object result = doAction(act);
            final short[] row = productionTable[act];
            symId = row[0];
            handleSize = row[1];
            if (handleSize == 0) {
                currentSymbol = new Symbol(symId, -1, -1, result);
            } else {
                //position based on left
                currentSymbol = new Symbol(symId, result, stack.peek(handleSize - 1));
                //pop the handle
                stack.pops(handleSize);
            }

            // look up the state to go to from the one popped back to shift to that state 
            currentSymbol.state = getReduce(reduceTable[stack.peek().state], symId);
            currentSymbol.isOnEdgeOfNewLine = isLastSymbolOnEdgeOfNewLine;
            stack.push(currentSymbol);
        } while (goonParse);

        return stack.peek();
    }

    private Symbol createLooseSemicolonSymbol(Symbol referSymbol) {
        return new Symbol(Tokens.SEMICOLON, referSymbol.line, referSymbol.column, null);
    }

    /**
     *
     * @param template Template
     * @return TemplateAST
     * @throws ParseException
     */
    public TemplateAST parse(final Template template) throws ParseException {
        Lexer lexer = null;
        final Engine myEngine = template.engine;
        final Resource resource = template.resource;
        final TextStatementFactory textStatFactory = myEngine.getTextStatementFactory();
        this.template = template;
        this.engine = myEngine;
        this.textStatementFactory = textStatFactory;
        this.locateVarForce = !myEngine.isLooseVar();
        this.nativeFactory = myEngine.getNativeFactory();
        this.varmgr = new VariantManager(myEngine);
        this.currentLabelIndex = 0;
        this.labelsIndexMap.put(null, 0);
        try {
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
            textStatFactory.startTemplateParser(template);
            return (TemplateAST) this.parse(lexer).value;
        } catch (ParseException e) {
            throw e;
        } catch (Exception e) {
            throw new ParseException(e);
        } finally {
            textStatFactory.finishTemplateParser(template);
            if (lexer != null) {
                try {
                    lexer.close();
                } catch (IOException ignore) {
                    // ignore
                }
            }
        }
    }

    boolean registClass(ClassNameBand classNameBand, int line, int column) throws ParseException {
        final String className = classNameBand.getClassSimpleName();
        if (ClassUtil.getPrimitiveClass(className) != null) {
            throw new ParseException("Duplicate class simple name:".concat(classNameBand.getClassPureName()), line, column);
        }
        if (importedClasses.containsKey(className)) {
            throw new ParseException("Duplicate class register:".concat(classNameBand.getClassPureName()), line, column);
        }
        importedClasses.put(className, classNameBand.getClassPureName());
        return true;
    }

    Class toClass(String className) {
        String classFullName = resolveClassFullName(className);
        return ClassUtil.getClass(classFullName);
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
        Class cls;

        // 2. find as primitive type
        cls = ClassUtil.getPrimitiveClass(className);
        if (cls != null) {
            return className;
        }

        // 3. find as java.lang.*
        try {
            cls = ClassUtil.getClass("java.lang.".concat(className));
        } catch (Exception ex) {
            // ignore
        }
        if (cls != null) {
            return cls.getName();
        }

        // failed, just return
        return className;
    }

    Class toClass(ClassNameBand classNameBand, int line, int column) throws ParseException {
        String classFullName = resolveClassFullName(classNameBand.getClassPureName());
        try {
            return ClassUtil.getClass(classFullName, classNameBand.getArrayDepth());
        } catch (ClassNotFoundException ex) {
            throw new ParseException("Class not found:".concat(classFullName), line, column);
        }
    }

    int getLabelIndex(String label) {
        Integer index = labelsIndexMap.get(label);
        if (index == null) {
            index = ++currentLabelIndex;
            labelsIndexMap.put(label, index);
        }
        return index;
    }

    Expression createAssign(ResetableValueExpression lexpr, Expression rexpr, int line, int column) {
        return new Assign(lexpr, rexpr, line, column);
    }

    Expression createGroupAssign(Expression[] lexpres, Expression rexpr, int line, int column) {

        ResetableValueExpression[] resetableExprs = new ResetableValueExpression[lexpres.length];
        for (int i = 0; i < lexpres.length; i++) {
            resetableExprs[i] = castToResetableValueExpression(lexpres[i]);
        }
        return new GroupAssign(resetableExprs, rexpr, line, column);
    }

    Expression createBreakPointExpression(Expression labelExpr, Expression expr, int line, int column) {
        final Object label = labelExpr == null ? null : StatementUtil.calcConst(labelExpr, true);
        if (breakPointListener == null) {
            return expr;
        }
        return new BreakPointExpression(breakPointListener, label, expr, line, column);
    }

    Statement createBreakPointStatement(Expression labelExpr, Statement statement, int line, int column) {
        final Object label = labelExpr == null ? null : StatementUtil.calcConst(labelExpr, true);
        if (breakPointListener == null) {
            return statement;
        }
        return new BreakPointStatement(breakPointListener, label, statement, line, column);
    }

    Statement createTextStatement(char[] text, int line, int column) {
        if (text == null || text.length == 0) {
            return NoneStatement.INSTANCE;
        }
        return this.textStatementFactory.getTextStatement(template, text, line, column);
    }

    ContextValue declearVarAndCreateContextValue(String name, int line, int column) {
        return new ContextValue(varmgr.assignVariant(name, line, column), line, column);
    }

    ContextValue[] declearVarAndCreateContextValues(List<String> names, int line, int column) {
        ContextValue[] contextValues = new ContextValue[names.size()];
        for (int i = 0; i < names.size(); i++) {
            contextValues[i] = declearVarAndCreateContextValue(names.get(i), line, column);
        }
        return contextValues;
    }

    Expression createContextValue(VarAddress addr, int line, int column) {
        switch (addr.type) {
            case VarAddress.GLOBAL:
                return new GlobalValue(this.engine.getGlobalManager(), addr.index, line, column);
            case VarAddress.CONST:
                return new DirectValue(addr.constValue, line, column);
            case VarAddress.SCOPE:
                return new ContextScopeValue(addr.scopeOffset, addr.index, line, column);
            default:
                //VarAddress.CONTEXT
                return new ContextValue(addr.index, line, column);
        }
    }

    Expression createContextValueAtUpstair(int upstair, String name, int line, int column) {
        return createContextValue(varmgr.locateAtUpstair(name, upstair, line, column), line, column);
    }

    Expression createContextValue(int upstair, String name, int line, int column) {
        return createContextValue(varmgr.locate(name, upstair, this.locateVarForce, line, column), line, column);
    }

    void assignConst(String name, Expression expr, int line, int column) {
        varmgr.assignConst(name, StatementUtil.calcConst(expr, true), line, column);
    }

    Expression createNativeStaticValue(ClassNameBand classNameBand, int line, int column) {
        if (classNameBand.size() < 2) {
            throw new ParseException("native static need a field name.", line, column);
        }
        final String fieldName = classNameBand.pop();
        final Class clazz = toClass(classNameBand, line, column);
        final String path = StringUtil.concat(clazz.getName(), ".", fieldName);
        if (!this.engine.checkNativeAccess(path)) {
            throw new ParseException("Not accessable of native path: ".concat(path), line, column);
        }
        final Field field;
        try {
            field = clazz.getField(fieldName);
        } catch (NoSuchFieldException ex) {
            throw new ParseException("No such field: ".concat(path), line, column);
        }
        if (ClassUtil.isStatic(field)) {
            ClassUtil.setAccessible(field);
            if (ClassUtil.isFinal(field)) {
                try {
                    return new DirectValue(field.get(null), line, column);
                } catch (IllegalArgumentException | IllegalAccessException ex) {
                    throw new ParseException("Failed to get static field value: ".concat(path), ex, line, column);
                }
            } else {
                return new NativeStaticValue(field, line, column);
            }
        } else {
            throw new ParseException("No a static field: ".concat(path), line, column);
        }
    }

    Statement createInterpolation(final Expression expr) {
        return new Interpolation(expr);
    }

    Expression createNativeNewArrayDeclareExpression(Class componentType, int line, int column) {
        return new DirectValue(this.nativeFactory.getNativeNewArrayMethodDeclare(componentType, line, column, true), line, column);
    }

    Expression createNativeMethodDeclareExpression(Class clazz, String methodName, List<Class> list, int line, int column) {
        return new DirectValue(this.nativeFactory.getNativeMethodDeclare(clazz, methodName, list.toArray(new Class[list.size()]), line, column, true), line, column);
    }

    Expression createMethodReference(String ref, int line, int column) {
        int split = ref.indexOf("::");
        String cls = ref.substring(0, split).trim();
        String method = ref.substring(split + 2).trim();
        MethodDeclare methodDeclare;
        if (method.equals("new")) {
            methodDeclare = this.nativeFactory.getNativeConstructorDeclare(toClass(cls), line, column, true);
        } else {
            methodDeclare = this.nativeFactory.getNativeMethodDeclare(toClass(cls), method, line, column, true);
        }
        return new DirectValue(methodDeclare, line, column);
    }

    Expression createNativeConstructorDeclareExpression(Class clazz, List<Class> list, int line, int column) {
        return new DirectValue(this.nativeFactory.getNativeConstructorDeclare(clazz, list.toArray(new Class[list.size()]), line, column, true), line, column);
    }

    Statement declearVar(String ident, int line, int column) {
        //XXX: Should Check var used before init
        varmgr.assignVariant(ident, line, column);
        return NoneStatement.INSTANCE;
    }

    Statement createIfStatement(Expression ifExpr, Statement thenStatement, Statement elseStatement, int line, int column) {
        thenStatement = StatementUtil.optimize(thenStatement);
        elseStatement = StatementUtil.optimize(elseStatement);
        if (thenStatement != null) {
            if (elseStatement != null) {
                return new IfElse(ifExpr, thenStatement, elseStatement, line, column);
            } else {
                return new If(ifExpr, thenStatement, line, column);
            }
        } else if (elseStatement != null) {
            return new IfNot(ifExpr, elseStatement, line, column);
        } else {
            return NoneStatement.INSTANCE;
        }
    }

    static Statement createStatementGroup(List<Statement> list, int line, int column) {
        return new StatementGroup(StatementUtil.toStatementArray(list), line, column);
    }

    static Expression createMethodExecute(Expression funcExpr, Expression[] paramExprs, int line, int column) {
        StatementUtil.optimize(paramExprs);
        funcExpr = StatementUtil.optimize(funcExpr);
        return new MethodExecute(funcExpr, paramExprs, line, column);
    }

    static Expression createDynamicNativeMethodExecute(Expression thisExpr, String func, Expression[] paramExprs, int line, int column) {
        StatementUtil.optimize(paramExprs);
        thisExpr = StatementUtil.optimize(thisExpr);
        return new DynamicNativeMethodExecute(thisExpr, func, paramExprs, line, column);
    }

    TemplateAST createTemplateAST(List<Statement> list) {
        Statement[] statements = StatementUtil.toStatementInvertArray(list);
        List<LoopInfo> loopInfos = StatementUtil.collectPossibleLoopsInfo(statements);
        if (loopInfos != null) {
            throw new ParseException("loop overflow: ".concat(StringUtil.join(loopInfos, ',')));
        }
        return new TemplateAST(varmgr.getIndexers(), statements, varmgr.getVarCount());
    }

    static IBlock createIBlock(List<Statement> list, int varIndexer, int line, int column) {
        Statement[] statements = StatementUtil.toStatementInvertArray(list);
        List<LoopInfo> loopInfoList = StatementUtil.collectPossibleLoopsInfo(statements);
        return loopInfoList != null
                ? new Block(varIndexer, statements, loopInfoList.toArray(new LoopInfo[loopInfoList.size()]), line, column)
                : new BlockNoLoops(varIndexer, statements, line, column);
    }

    static TryPart createTryPart(List<Statement> list, int varIndexer, int line, int column) {
        return new TryPart(createIBlock(list, varIndexer, line, column), line, column);
    }

    static ResetableValueExpression castToResetableValueExpression(Expression expr) {
        if (expr instanceof ResetableValueExpression) {
            return (ResetableValueExpression) expr;
        }
        throw new ParseException("expression is not assignable", expr);
    }

    static Expression createSelfOperator(Expression lexpr, int sym, Expression rightExpr, int line, int column) {
        ResetableValueExpression leftExpr = castToResetableValueExpression(lexpr);
        SelfOperator oper;
        switch (sym) {

            // (+ - * / %)=
            case OP_PLUSEQ:
                oper = new SelfPlus(leftExpr, rightExpr, line, column);
                break;
            case OP_MINUSEQ:
                oper = new SelfMinus(leftExpr, rightExpr, line, column);
                break;
            case OP_MULTEQ:
                oper = new SelfMult(leftExpr, rightExpr, line, column);
                break;
            case OP_DIVEQ:
                oper = new SelfDiv(leftExpr, rightExpr, line, column);
                break;
            case OP_MODEQ:
                oper = new SelfMod(leftExpr, rightExpr, line, column);
                break;

            // (<< >> >>>)=
            case OP_LSHIFTEQ:
                oper = new SelfLShift(leftExpr, rightExpr, line, column);
                break;
            case OP_RSHIFTEQ:
                oper = new SelfRShift(leftExpr, rightExpr, line, column);
                break;
            case OP_URSHIFTEQ:
                oper = new SelfURShift(leftExpr, rightExpr, line, column);
                break;

            // (& ^ |)=
            case OP_ANDEQ:
                oper = new SelfBitAnd(leftExpr, rightExpr, line, column);
                break;
            case OP_XOREQ:
                oper = new SelfBitXor(leftExpr, rightExpr, line, column);
                break;
            case OP_OREQ:
                oper = new SelfBitOr(leftExpr, rightExpr, line, column);
                break;

            default:
                throw new ParseException("Unsupported Operator", line, column);
        }

        return StatementUtil.optimize(oper);
    }

    static Expression createBinaryOperator(Expression leftExpr, int sym, Expression rightExpr, int line, int column) {

        BinaryOperator oper;
        switch (sym) {
            case Tokens.ANDAND: // &&
                oper = new And(leftExpr, rightExpr, line, column);
                break;
            case Tokens.AND: // &
                oper = new BitAnd(leftExpr, rightExpr, line, column);
                break;
            case Tokens.OR: // |
                oper = new BitOr(leftExpr, rightExpr, line, column);
                break;
            case Tokens.XOR: // ^
                oper = new BitXor(leftExpr, rightExpr, line, column);
                break;
            case Tokens.DIV: // /
                oper = new Div(leftExpr, rightExpr, line, column);
                break;
            case Tokens.EQEQ: // ==
                oper = new Equal(leftExpr, rightExpr, line, column);
                break;
            case Tokens.GTEQ: // >=
                oper = new GreaterEqual(leftExpr, rightExpr, line, column);
                break;
            case Tokens.GT: // >
                oper = new Greater(leftExpr, rightExpr, line, column);
                break;
            case Tokens.LSHIFT: // <<
                oper = new LShift(leftExpr, rightExpr, line, column);
                break;
            case Tokens.LTEQ: // <=
                oper = new LessEqual(leftExpr, rightExpr, line, column);
                break;
            case Tokens.LT: // <
                oper = new Less(leftExpr, rightExpr, line, column);
                break;
            case Tokens.MINUS: // -
                oper = new Minus(leftExpr, rightExpr, line, column);
                break;
            case Tokens.MOD: // %
                oper = new Mod(leftExpr, rightExpr, line, column);
                break;
            case Tokens.MULT: // *
                oper = new Mult(leftExpr, rightExpr, line, column);
                break;
            case Tokens.NOTEQ: // !=
                oper = new NotEqual(leftExpr, rightExpr, line, column);
                break;
            case Tokens.OROR: // ||
                oper = new Or(leftExpr, rightExpr, line, column);
                break;
            case Tokens.PLUS: // +
                oper = new Plus(leftExpr, rightExpr, line, column);
                break;
            case Tokens.RSHIFT: // >>
                oper = new RShift(leftExpr, rightExpr, line, column);
                break;
            case Tokens.URSHIFT: // >>>
                oper = new URShift(leftExpr, rightExpr, line, column);
                break;
            case Tokens.DOTDOT: // ..
                oper = new IntStep(leftExpr, rightExpr, line, column);
                break;
            default:
                throw new ParseException("Unsupported Operator", line, column);
        }
        return StatementUtil.optimize(oper);
    }

    /* Base Parser */
    private static final short[][] PRODUCTION_TABLE = loadData("Production");
    private static final short[][] ACTION_TABLE = loadData("Action");
    private static final short[][] REDUCE_TABLE = loadData("Reduce");
    private static final String[] SYMBOL_STRS = new String[]{
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
        "->", //MINUSGT
        ".~", //DYNAMIC_DOT
        "..", //DOTDOT
        "=", //EQ
        "IDENTIFIER", //IDENTIFIER
        "TEXT", //TEXT_STATEMENT
        "DIRECT_VALUE", // DIRECT_VALUE
        "const", //CONST
        "UNKNOWN"
    };

    private static short getAction(final short[] row, final int sym) {
        final int len;
        int probe;
        /* linear search if we are < 10 entries, otherwise binary search */
        if ((len = row.length) < 20) {
            for (probe = 0; probe < len; probe++) {
                if (row[probe++] == sym) {
                    return row[probe];
                }
            }
        } else {
            int first, last;
            first = 0;
            last = (len - 1) >> 1;

            int probe_2;
            while (first <= last) {
                probe = (first + last) >> 1;
                probe_2 = probe << 1;
                if (sym == row[probe_2]) {
                    return row[probe_2 + 1];
                } else if (sym > row[probe_2]) {
                    first = probe + 1;
                } else {
                    last = probe - 1;
                }
            }
        }
        //error
        return 0;
    }

    private static short getReduce(final short[] row, int sym) {
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
                ClassUtil.getDefaultClassLoader().getResourceAsStream(
                        StringUtil.concat("org/febit/wit/core/Parser$", name, ".data"))
        )) {
            return (short[][]) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new Error(e);
        }
    }

    private static String getSimpleHintMessage(Symbol symbol) {
        final short[] row = ACTION_TABLE[symbol.state];
        final int len = row.length;
        if (len == 0) {
            return "[no hints]";
        }
        final boolean highterLevel = len > 8;
        if (highterLevel && getAction(row, Tokens.SEMICOLON) != 0) {
            return "forget ';' ?";
        }
        final StringBuilder sb = new StringBuilder();
        boolean notFirst = false;
        short sym;
        for (int i = 0; i < len; i += 2) {
            sym = row[i];
            if (highterLevel && !isHintLevelOne(sym)) {
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
}
