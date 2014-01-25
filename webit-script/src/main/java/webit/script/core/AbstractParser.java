// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script.core;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.HashMap;
import java.util.Map;
import webit.script.Engine;
import webit.script.Template;
import webit.script.core.ast.TemplateAST;
import webit.script.core.ast.statements.InterpolationFactory;
import webit.script.core.text.TextStatementFactory;
import webit.script.exceptions.ParseException;
import webit.script.loggers.Logger;
import webit.script.util.ClassLoaderUtil;
import webit.script.util.ExceptionUtil;
import webit.script.util.StringUtil;
import webit.script.util.collection.ArrayStack;
import webit.script.util.collection.Stack;

/**
 *
 * @author Zqq
 */
abstract class AbstractParser {

    private final static int stackInitialCapacity = 24;
    private final static int START_STATE = 0;

    AbstractParser() {
        this._stack = new ArrayStack<Symbol>(stackInitialCapacity);
    }
    /**
     * The parse _stack itself.
     */
    final Stack<Symbol> _stack;
    /**
     * Internal flag to indicate when parser should quit.
     */
    boolean goonParse = false;

    //
    Engine engine;
    Template template;
    TextStatementFactory textStatementFactory;
    InterpolationFactory interpolationFactory;
    NativeFactory nativeFactory;
    Logger logger;
    boolean locateVarForce;
    NativeImportManager nativeImportMgr;
    VariantManager varmgr;
    Map<String, Integer> labelsIndexMap;
    int currentLabelIndex;

    /**
     *
     * @param template Template
     * @return TemplateAST
     * @throws ParseException
     */
    public TemplateAST parseTemplate(final Template template) throws ParseException {
        Lexer lexer = null;
        try {
            lexer = new Lexer(template.resource.openReader());
            this.template = template;
            final Engine _engine;
            this.engine = _engine = template.engine;
            lexer.setTrimCodeBlockBlankLine(_engine.isTrimCodeBlockBlankLine());
            this.logger = _engine.getLogger();
            TextStatementFactory _textStatementFactory;
            this.textStatementFactory = _textStatementFactory = _engine.getTextStatementFactory();
            this.locateVarForce = !_engine.isLooseVar();
            this.interpolationFactory = new InterpolationFactory(_engine.getFilter());
            //
            this.nativeImportMgr = new NativeImportManager();
            this.nativeFactory = _engine.getNativeFactory();
            this.varmgr = new VariantManager(_engine);
            this.labelsIndexMap = new HashMap<String, Integer>();
            this.labelsIndexMap.put(null, 0);
            this.currentLabelIndex = 0;
            //
            _textStatementFactory.startTemplateParser(template);
            Symbol sym = this.parse(lexer);
            _textStatementFactory.finishTemplateParser(template);
            return (TemplateAST) sym.value;
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

    /**
     * Perform a bit of user supplied action code. Actions are indexed by an
     * internal action number assigned at parser generation time.
     *
     * @param act_num the internal index of the action to be performed.
     * @return Object
     * @throws java.lang.Exception
     */
    abstract Object doAction(int act_num) throws ParseException;

    /**
     *
     * @param row actionTable[state]
     * @param id the Symbol index of the action being accessed.
     */
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
        /* default: error == 0 */
        return 0;
    }

    /**
     *
     * @param row reduceTable[state]
     * @param id the Symbol index of the entry being accessed.
     */
    private static short getReduce(final short[] row, int sym) {
        int probe, len;
        if (row != null) {
            for (probe = 0, len = row.length; probe < len; probe++) {
                if (row[probe++] == sym) {
                    return row[probe];
                }
            }
        }
        //TODO: Error */
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
        final Stack<Symbol> stack;
        (stack = this._stack).clear();
        {
            Symbol START;
            (START = new Symbol(0, null)).state = START_STATE;
            stack.push(currentSymbol = START);
        }

        final short[][] actionTable = Parser.ACTION_TABLE;
        final short[][] reduceTable = Parser.REDUCE_TABLE;
        final short[][] productionTable = Parser.PRODUCTION_TABLE;

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
                //reduceAction()
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

            } else {//act == 0
                throw new ParseException(StringUtil.concat("Syntax error before: ", Integer.toString(myLexer.getLine()), "(", Integer.toString(myLexer.getColumn()), ")", ". Hints: ", getSimpleHintMessage(currentSymbol)), myLexer.getLine(), myLexer.getColumn());
            }
        } while (goonParse);

        return stack.peek();//lhs_sym;
    }

    static short[][] loadFromDataFile(String name) {
        ObjectInputStream in = null;
        try {
            return (short[][]) (in = new ObjectInputStream(ClassLoaderUtil
                    .getDefaultClassLoader()
                    .getResourceAsStream(StringUtil.concat("webit/script/core/Parser$", name, ".data"))))
                    .readObject();
        } catch (IOException e) {
            throw new Error(e);
        } catch (ClassNotFoundException e) {
            throw new Error(e);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                }
            }
        }
    }

    private static String getSimpleHintMessage(Symbol symbol) {
        final short[] row = Parser.ACTION_TABLE[symbol.state];
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

    private final static short[] HINTS_LEVEL_1 = new short[]{
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

    private final static String[] SYMBOL_STRS = new String[]{
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
