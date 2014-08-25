// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script.core;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import webit.script.Engine;
import webit.script.Template;
import webit.script.core.VariantManager.VarAddress;
import webit.script.core.ast.*;
import webit.script.core.ast.TemplateAST;
import webit.script.core.ast.expressions.*;
import webit.script.core.ast.operators.*;
import webit.script.core.ast.statements.BreakPointStatement;
import webit.script.core.ast.statements.Interpolation;
import webit.script.core.text.TextStatementFactory;
import webit.script.debug.BreakPointListener;
import webit.script.exceptions.ParseException;
import webit.script.loaders.Resource;
import webit.script.loaders.ResourceOffset;
import webit.script.util.ClassLoaderUtil;
import webit.script.util.ClassNameBand;
import webit.script.util.ClassUtil;
import webit.script.util.ExceptionUtil;
import webit.script.util.Stack;
import webit.script.util.StatementUtil;
import webit.script.util.StringUtil;

/**
 *
 * @author Zqq
 */
abstract class AbstractParser {

    private static final short[][] PRODUCTION_TABLE = loadFromDataFile("Production");
    private static final short[][] ACTION_TABLE = loadFromDataFile("Action");
    private static final short[][] REDUCE_TABLE = loadFromDataFile("Reduce");

    final Stack<Symbol> _stack;
    boolean goonParse = false;
    
    Engine engine;
    Template template;
    TextStatementFactory textStatementFactory;
    NativeFactory nativeFactory;
    boolean locateVarForce;
    NativeImportManager nativeImportMgr;
    VariantManager varmgr;
    Map<String, Integer> labelsIndexMap;
    int currentLabelIndex;
    BreakPointListener breakPointListener;

    AbstractParser() {
        this._stack = new Stack<Symbol>(24);
    }

    public TemplateAST parseTemplate(final Template template, BreakPointListener breakPointListener) throws ParseException {
        this.breakPointListener = breakPointListener;
        return parseTemplate(template);
    }
    
    /**
     *
     * @param template Template
     * @return TemplateAST
     * @throws ParseException
     */
    public TemplateAST parseTemplate(final Template template) throws ParseException {
        Lexer lexer = null;
        try {
            final Engine _engine;
            final TextStatementFactory _textStatementFactory;
            final Symbol astSymbol;

            //ISSUE: LexerProvider
            final Resource resource = template.resource;
            lexer = new Lexer(resource.openReader());

            if (resource instanceof ResourceOffset) {
                lexer.setOffset((ResourceOffset) resource);
            } else {
                lexer.setOffset(0, 0);
            }

            this.template = template;
            this.engine = _engine = template.engine;
            lexer.setTrimCodeBlockBlankLine(_engine.isTrimCodeBlockBlankLine());
            this.textStatementFactory = _textStatementFactory = _engine.getTextStatementFactory();
            this.locateVarForce = !_engine.isLooseVar();
            this.nativeImportMgr = new NativeImportManager();
            this.nativeFactory = _engine.getNativeFactory();
            this.varmgr = new VariantManager(_engine);

            //init labelsIndexMap
            (this.labelsIndexMap = new HashMap<String, Integer>())
                    .put(null, this.currentLabelIndex = 0);

            _textStatementFactory.startTemplateParser(template);
            astSymbol = this.parse(lexer);
            _textStatementFactory.finishTemplateParser(template);
            return (TemplateAST) astSymbol.value;
        } catch (Exception e) {
            throw ExceptionUtil.castToParseException(e);
        } finally {
            if (lexer != null) {
                try {
                    lexer.yyclose();
                } catch (IOException ignore) {
                }
            }
        }
    }

    abstract Object doAction(int act_num) throws ParseException;

    int getLabelIndex(String label) {
        Integer index = labelsIndexMap.get(label);
        if (index == null) {
            index = ++currentLabelIndex;
            labelsIndexMap.put(label, index);
        }
        return index;
    }
    
    Expression createBreakPointExpression(String label, Expression expr, int line, int column){
        if (breakPointListener == null) {
            return expr;
        }
        return new BreakPointExpression(breakPointListener, label, expr, line, column);
    }
    
    Statement createBreakPointStatement(String label, Statement statement, int line, int column){
        if (breakPointListener == null) {
            return statement;
        }
        return new BreakPointStatement(breakPointListener, label, statement, line, column);
    }

    Expression createContextValue(VarAddress addr, int line, int column) {
        switch (addr.type) {
            case VarAddress.ROOT:
                return new RootContextValue(addr.index, line, column);
            case VarAddress.GLOBAL:
                return new GlobalValue(this.engine.getGlobalManager(), addr.index, line, column);
            case VarAddress.CONST:
                return new DirectValue(addr.constValue, line, column);
            default:
                //VarAddress.CONTEXT
                if (addr.upstairs == 0) {
                    return new CurrentContextValue(addr.index, line, column);
                } else {
                    return new ContextValue(addr.upstairs, addr.index, line, column);
                }
        }
    }

    Expression createContextValueAtUpstair(int upstair, String name, int line, int column) {
        return createContextValue(varmgr.locateAtUpstair(name, upstair, line, column), line, column);
    }

    Expression createContextValue(int upstair, String name, int line, int column) {
        return createContextValue(varmgr.locate(name, upstair, this.locateVarForce, line, column), line, column);
    }

    void assignConst(String name, Expression value, int line, int column) {
        value = StatementUtil.optimize(value);
        if (value instanceof DirectValue) {
            varmgr.assignConst(name, ((DirectValue) value).value, line, column);
        } else {
            throw new ParseException("const should defined a direct value.", line, column);
        }
    }

    Expression createNativeStaticValue(ClassNameBand classNameBand, int line, int column) {
        if (classNameBand.size() < 2) {
            throw new ParseException("native static need a filed name.", line, column);
        }
        final String fieldName = classNameBand.pop();
        final Class clazz = nativeImportMgr.toClass(classNameBand, line, column);
        final String path = (StringUtil.concat(clazz.getName(), ".", fieldName));
        if (!this.engine.getNativeSecurityManager().access(path)) {
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
                } catch (Exception ex) {
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
        return new DirectValue(this.nativeFactory.createNativeNewArrayMethodDeclare(componentType, line, column), line, column);
    }

    Expression createNativeMethodDeclareExpression(Class clazz, String methodName, ClassNameList list, int line, int column) {
        return new DirectValue(this.nativeFactory.createNativeMethodDeclare(clazz, methodName, list.toArray(), line, column), line, column);
    }

    Expression createNativeConstructorDeclareExpression(Class clazz, ClassNameList list, int line, int column) {
        return new DirectValue(this.nativeFactory.createNativeConstructorDeclare(clazz, list.toArray(), line, column), line, column);
    }

    static ResetableValueExpression castToResetableValueExpression(Expression expr) {
        if (expr instanceof ResetableValueExpression) {
            return (ResetableValueExpression) expr;
        } else {
            throw new ParseException("Invalid expression to redirect out stream to, must be rewriteable", expr);
        }
    }

    static Expression createSelfOperator(Expression lexpr, int sym, Expression rightExpr, int line, int column) {
        ResetableValueExpression leftExpr = castToResetableValueExpression(lexpr);
        SelfOperator oper;
        switch (sym) {

            // (+ - * / %)=
            case Operators.PLUSEQ:
                oper = new SelfPlus(leftExpr, rightExpr, line, column);
                break;
            case Operators.MINUSEQ:
                oper = new SelfMinus(leftExpr, rightExpr, line, column);
                break;
            case Operators.MULTEQ:
                oper = new SelfMult(leftExpr, rightExpr, line, column);
                break;
            case Operators.DIVEQ:
                oper = new SelfDiv(leftExpr, rightExpr, line, column);
                break;
            case Operators.MODEQ:
                oper = new SelfMod(leftExpr, rightExpr, line, column);
                break;

            // (<< >> >>>)=
            case Operators.LSHIFTEQ:
                oper = new SelfLShift(leftExpr, rightExpr, line, column);
                break;
            case Operators.RSHIFTEQ:
                oper = new SelfRShift(leftExpr, rightExpr, line, column);
                break;
            case Operators.URSHIFTEQ:
                oper = new SelfURShift(leftExpr, rightExpr, line, column);
                break;

            // (& ^ |)=
            case Operators.ANDEQ:
                oper = new SelfBitAnd(leftExpr, rightExpr, line, column);
                break;
            case Operators.XOREQ:
                oper = new SelfBitXor(leftExpr, rightExpr, line, column);
                break;
            case Operators.OREQ:
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
            case Tokens.QUESTION_COLON: // ?:
                oper = new IfOrOperator(leftExpr, rightExpr, line, column);
                break;
            case Tokens.DOTDOT: // ..
                oper = new IntStep(leftExpr, rightExpr, line, column);
                break;
            default:
                throw new ParseException("Unsupported Operator", line, column);
        }
        return StatementUtil.optimize(oper);
    }

    private static short getAction(final short[] row, final int sym) {
        int first, last, probe, row_len;

        /* linear search if we are < 10 entries, otherwise binary search */
        if ((row_len = row.length) < 20) {
            for (probe = 0; probe < row_len; probe++) {
                if (row[probe++] == sym) {
                    return row[probe];
                }
            }
        } else {
            first = 0;
            last = ((row_len - 1) >> 1);

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
        int probe, len;
        if (row != null) {
            for (probe = 0, len = row.length; probe < len; probe++) {
                if (row[probe++] == sym) {
                    return row[probe];
                }
            }
        }
        //error
        return -1;
    }

    /**
     * This method provides the main parsing routine. It returns only when
     * finishParsing() has been called (typically because the parser has
     * accepted, or a fatal error has been reported). See the header
     * documentation for the class regarding how shift/reduce parsers operate
     * and how the various tables are used.
     */
    private Symbol parse(final Lexer myLexer) throws Exception {

        int act;
        Symbol currentToken;
        Symbol currentSymbol;
        final Stack<Symbol> stack = this._stack;
        stack.clear();

        //Start Symbol
        currentSymbol = new Symbol(0, null);
        currentSymbol.state = 0;
        stack.push(currentSymbol);

        final short[][] actionTable = ACTION_TABLE;
        final short[][] reduceTable = REDUCE_TABLE;
        final short[][] productionTable = PRODUCTION_TABLE;

        currentToken = myLexer.nextToken();

        /* continue until we are told to stop */
        goonParse = true;
        do {

            /* look up action out of the current state with the current input */
            act = getAction(actionTable[currentSymbol.state], currentToken.id);

            /* decode the action -- > 0 encodes shift */
            if (act > 0) {
                /* shift to the encoded state by pushing it on the _stack */
                currentToken.state = act - 1;
                stack.push(currentSymbol = currentToken);

                /* advance to the next Symbol */
                currentToken = myLexer.nextToken();
            } else if (act < 0) {
                /* if its less than zero, then it encodes a reduce action */
                act = (-act) - 1;
                final int symId, handleSize;
                final Object result = doAction(act);
                final short[] row;
                symId = (row = productionTable[act])[0];
                handleSize = row[1];
                if (handleSize == 0) {
                    currentSymbol = new Symbol(symId, result);
                } else {
                    currentSymbol = new Symbol(symId, result, stack.peek(handleSize - 1)); //position based on left
                    //pop the handle
                    stack.pops(handleSize);
                }

                /* look up the state to go to from the one popped back to */
                /* shift to that state */
                currentSymbol.state = getReduce(reduceTable[stack.peek().state], symId);
                stack.push(currentSymbol);

            } else {
                //act == 0
                throw new ParseException(StringUtil.concat("Syntax error before: ", Integer.toString(myLexer.getLine()), "(", Integer.toString(myLexer.getColumn()), ")", ". Hints: ", getSimpleHintMessage(currentSymbol)), myLexer.getLine(), myLexer.getColumn());
            }
        } while (goonParse);

        return stack.peek();
    }

    private static short[][] loadFromDataFile(String name) {
        ObjectInputStream in = null;
        try {
            in = new ObjectInputStream(ClassLoaderUtil.getDefaultClassLoader()
                    .getResourceAsStream(StringUtil.concat("webit/script/core/Parser$", name, ".data")));
            return (short[][]) in.readObject();
        } catch (IOException e) {
            throw new Error(e);
        } catch (ClassNotFoundException e) {
            throw new Error(e);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException ioex) {
                    // ignore
                }
            }
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

    private static final short[] HINTS_LEVEL_1 = new short[]{
        Tokens.COLON, //":"
        Tokens.SEMICOLON, //";"
        Tokens.RBRACE, //"}"
        Tokens.INTERPOLATION_END, //"}"
        Tokens.RPAREN, //")"
        Tokens.RBRACK, //"]"
        Tokens.IDENTIFIER, //"IDENTIFIER"
        Tokens.DIRECT_VALUE, //"DIRECT_VALUE"
    };

    private static boolean isHintLevelOne(short sym) {
        for (int i = 0, len = HINTS_LEVEL_1.length; i < len; i++) {
            if (HINTS_LEVEL_1[i] == sym) {
                return true;
            }
        }
        return false;
    }

    private static final String[] SYMBOL_STRS = new String[]{
        "EOF", //EOF = 0
        "ERROR", //ERROR = 1
        "var", //VAR = 2
        "if", //IF = 3
        "else", //ELSE = 4
        "for", //FOR = 5
        "this", //THIS = 6
        "super", //SUPER = 7
        "switch", //SWITCH = 8
        "case", //CASE = 9
        "default", //DEFAULT = 10
        "do", //DO = 11
        "while", //WHILE = 12
        "throw", //THROW = 13
        "try", //TRY = 14
        "catch", //CATCH = 15
        "finally", //FINALLY = 16
        "new", //NEW = 17
        "instanceof", //INSTANCEOF = 18
        "function", //FUNCTION = 19
        "echo", //ECHO = 20
        "static", //STATIC = 21
        "native", //NATIVE = 22
        "import", //IMPORT = 23
        "include", //INCLUDE = 24
        "@import", //NATIVE_IMPORT = 25
        "break", //BREAK = 26
        "continue", //CONTINUE = 27
        "return", //RETURN = 28
        "++", //PLUSPLUS = 29
        "--", //MINUSMINUS = 30
        "+", //PLUS = 31
        "-", //MINUS = 32
        "*", //MULT = 33
        "/", //DIV = 34
        "%", //MOD = 35
        "<<", //LSHIFT = 36
        ">>", //RSHIFT = 37
        ">>>", //URSHIFT = 38
        "<", //LT = 39
        ">", //GT = 40
        "<=", //LTEQ = 41
        ">=", //GTEQ = 42
        "==", //EQEQ = 43
        "!=", //NOTEQ = 44
        "&", //AND = 45
        "^", //XOR = 46
        "|", //OR = 47
        "~", //COMP = 48
        "&&", //ANDAND = 49
        "||", //OROR = 50
        "!", //NOT = 51
        "?", //QUESTION = 52
        "?:", //QUESTION_COLON = 53
        "*=", //SELFEQ = 54
        "-", //UMINUS = 55
        ".", //DOT = 56
        ":", //COLON = 57
        ",", //COMMA = 58
        ";", //SEMICOLON = 59
        "{", //LBRACE = 60
        "}", //RBRACE = 61
        "}", //INTERPOLATION_END = 62
        "(", //LPAREN = 63
        ")", //RPAREN = 64
        "[", //LBRACK = 65
        "]", //RBRACK = 66
        "=>", //EQGT = 67
        "@", //AT = 68
        "..", //DOTDOT = 69
        "=", //EQ = 70
        "IDENTIFIER", //IDENTIFIER = 71
        "TEXT", //TEXT_STATEMENT = 72
        "DIRECT_VALUE", //DIRECT_VALUE = 73
        "UNKNOWN"
    };

    static String symbolToString(final short sym) {
        if (sym >= 0 && sym < SYMBOL_STRS.length) {
            return SYMBOL_STRS[sym];
        }
        return "UNKNOWN";
    }
}
