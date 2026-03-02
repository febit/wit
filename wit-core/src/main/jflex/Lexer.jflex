// Copyright (c) 2013-2016, febit.org. All Rights Reserved.
package org.febit.wit.parser;

import org.jspecify.annotations.Nullable;
import org.febit.wit.exception.ParseException;
import org.febit.wit.runtime.ast.TextPosition;
import org.febit.wit.runtime.Source;
import org.febit.wit.util.LexerCharsBuffer;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;

@javax.annotation.processing.Generated("jflex")
%%
%class Lexer
%function _parseNextToken
%apiprivate
%type Token
%line
%column
%buffer 8192

%{
    //================ >> user code

    private static final int INTERPOLATION_START_LEN = 2;
    private static final int TEXT_BLOCK_END_LEN = 2;
    private static final Token SYM_NEW_LINE = new Token(-1, TextPosition.UNKNOWN, '\n');

    private boolean interpolationFlag = false;
    private boolean leftInterpolationFlag = true;
    private boolean templateStringFlag = false;
    private int templateStringBraceClosingCounter = 0;

    private final LexerCharsBuffer charsBuffer = new LexerCharsBuffer(256);
    private boolean trimCodeBlockBlankLine = false;
    private int stringLine = 0;
    private int stringColumn = 0;

    private int offsetLine = 0;
    private int offsetColumnOfFirstLine = 0;

    private final Deque<Token> pendingQueue = new ArrayDeque<>(8);

    private void addPendingSymbols(Token... syms) {
        this.pendingQueue.addAll(Arrays.asList(syms));
    }

    private Token _nextToken() throws java.io.IOException {
        // check pending queue first
        Deque<Token> pending = this.pendingQueue;
        Token next = pending.pollFirst();
        if (next != null) {
            return next;
        }
        // parse next when queue is empty
        next = _parseNextToken();
        if (next != null) {
            return next;
        }
        // try again
        return _nextToken();
    }

    public Token nextToken() throws java.io.IOException {
        Token next;

        // skip new-line
        do {
            next = _nextToken();
        } while (next == SYM_NEW_LINE);

        // EOF or SEMICOLON
        if (next.kind == TokenKinds.EOF
                || next.kind == TokenKinds.SEMICOLON) {
            return next;
        }

        // Others must check if next token is new-line or EOF
        Token nextAfter = _nextToken();
        // return back
        this.pendingQueue.addFirst(nextAfter);
        if (nextAfter == SYM_NEW_LINE || nextAfter.kind == TokenKinds.EOF) {
            next.isAtEdgeOfNewLine = true;
        }
        return next;
    }

    public void close() throws java.io.IOException {
        yyclose();
    }

    public void setTrimCodeBlockBlankLine(boolean flag) {
        trimCodeBlockBlankLine = flag;
    }

    public void setOffset(int offsetLine, int offsetColumnOfFirstLine) {
        this.offsetLine = offsetLine;
        this.offsetColumnOfFirstLine = 1 - offsetColumnOfFirstLine;
    }

    public void setOffset(Source source) {
        setOffset(source.getOffsetLine(), source.getOffsetColumnOfFirstLine());
    }

    public int getColumn() {
        return yycolumn + (yyline == offsetLine ? offsetColumnOfFirstLine : 1);
    }

    public int getLine() {
        return yyline - offsetLine + 1;
    }

    public TextPosition getPosition() {
        return TextPosition.of(getLine(), getColumn());
    }

    public char yychar() {
        return (char) yychar;
    }

    private char[] popChars() {
        char[] chars = charsBuffer.toCharArray();
        charsBuffer.reset();
        return chars;
    }

    private char[] popCharsWithoutLeadingLineEnding() {
        char[] chars = charsBuffer.toCharsWithoutLeadingLineEnding();
        charsBuffer.reset();
        return chars;
    }

    private String popAsString() {
        return new String(popChars());
    }

    private void resetCharsBuffer() {
        charsBuffer.reset();
        stringLine = yyline;
        stringColumn = yycolumn;
    }

    private void appendToString(char c) {
        charsBuffer.write(c);
    }

    private void appendToString(char c, int repeat) {
        if (repeat == 1) {
            charsBuffer.append(c);
        } else if (repeat == 2) {
            charsBuffer.append(c).append(c);
        } else if (repeat != 0) {
            final char[] chars = new char[repeat];
            while (repeat != 0) {
                chars[--repeat] = c;
            }
            charsBuffer.write(chars);
        }
    }

    private void appendToString(char c, char c2) {
        charsBuffer.append(c).append(c2);
    }

    private void pullToString() {
        charsBuffer.write(zzBuffer, zzStartRead, zzMarkedPos - zzStartRead);
    }

    void beginWith(Source.BeginWith with) {
        switch (with) {
            case SCRIPT -> yybegin(STATE_SCRIPT);
            case TEMPLATE -> yybegin(STATE_TEMPLATE);
        }
    }

    private Token token(int sym) {
        return token(sym, yyline + 1, yycolumn + 1, sym);
    }

    private Token token(int sym, @Nullable Object val) {
        return token(sym, yyline + 1, yycolumn + 1, val);
    }

    private Token token(int sym, int line, int column, @Nullable Object val) {
        return new Token(sym, TextPosition.of(line, column), val);
    }

    private Token popTemplateTextSymbol(boolean interpolationFlag) {
        this.interpolationFlag = interpolationFlag;
        yybegin(STATE_SCRIPT);
        final char[] chars;
        if (trimCodeBlockBlankLine) {
            if (!interpolationFlag) {
                charsBuffer.trimTrailingBlankLine();
            }
            chars = this.leftInterpolationFlag
                    ? popChars()
                    : popCharsWithoutLeadingLineEnding();
        } else {
            chars = popChars();
        }
        return token(TokenKinds.TEXT_STATEMENT, stringLine, stringColumn, chars);
    }

    public String yytext(int startOffset, int endOffset) {
        return new String(zzBuffer, zzStartRead + startOffset, zzMarkedPos - zzStartRead + endOffset);
    }

    public String yytext(int endOffset) {
        return new String(zzBuffer, zzStartRead, zzMarkedPos - zzStartRead + endOffset);
    }

    public char yyTextChar(int startOffset) {
        return zzBuffer[zzStartRead + startOffset];
    }

    public char yyTextChar() {
        return zzBuffer[zzStartRead];
    }

    private long yyDecLong(int startOffset, int endOffset) {
        return parseDecLong(zzBuffer, zzStartRead + startOffset, zzMarkedPos + endOffset);
    }

    private int yyDecInt(int startOffset, int endOffset) {
        long result = parseDecLong(zzBuffer, zzStartRead + startOffset, zzMarkedPos + endOffset);
        if (result > Integer.MAX_VALUE || result < Integer.MIN_VALUE) {
            throw new ParseException("Number overflow", getPosition());
        }
        return (int) result;
    }

    private long yyLong(int startOffset, int endOffset, int radix) {
        return parseLong(zzBuffer, zzStartRead + startOffset, zzMarkedPos + endOffset, radix);
    }

    private int yyInt(int startOffset, int endOffset, int radix) {
        return (int) parseLong(zzBuffer, zzStartRead + startOffset, zzMarkedPos + endOffset, radix);
    }

    private long yyBinLong(int startOffset, int endOffset) {
        return parseBinLong(zzBuffer, zzStartRead + startOffset, zzMarkedPos + endOffset);
    }

    private int yyBinInteger(int startOffset, int endOffset) {
        return (int) parseBinLong(zzBuffer, zzStartRead + startOffset, zzMarkedPos + endOffset);
    }

    private long parseBinLong(char[] buffer, int start, int end) {
        long result = 0;
        while (start < end) {
            result <<= 1;
            if (buffer[start++] == '1') {
                ++result;
            }
        }
        return result;
    }

    private long parseDecLong(final char[] buffer, int start, final int end) {
        long result = 0;
        while (start < end) {
            if (result > Long.MAX_VALUE / 10) {
                throw new ParseException("Number overflow", getPosition());
            }
            result *= 10;
            int digit = Character.digit(buffer[start++], 10);
            if (result > (Long.MAX_VALUE - digit)) {
                throw new ParseException("Number overflow", getPosition());
            }
            result += digit;
        }
        return result;
    }

    private long parseLong(char[] buffer, int start, int end, int radix) {
        long result = 0;
        while (start < end) {
            result = result * radix + Character.digit(buffer[start++], radix);
        }
        return result;
    }

    //================ << user code
%}


/* main character classes */
LineTerminator = \r|\n|\r\n
InputCharacter = [^\r\n]

Blanks = [ \t\f]
WhiteSpace = {LineTerminator} | {Blanks}

/* comments */
Comment = {TraditionalComment} | {LineComment} |
          {DocumentationComment}

TraditionalComment = "/*" [^*] ~"*/" | "/*" "*"+ "/"
LineComment = "//" {InputCharacter}*
DocumentationComment = "/*" "*"+ [^/*] ~"*/"

/* identifiers */
Identifier = [:jletter:][:jletterdigit:]*
/* Identifier = {Identifier0} | "for." {Identifier0} */

/* number literals */
IntegerMin = "-" {WhiteSpace}* "2147483648"
LongMin = "-" {WhiteSpace}* "9223372036854775808" [lL]
BinIntegerLiteral = 0 [bB] [01] {1,32}
BinLongLiteral = 0 [bB] [01] {1,64} [lL]

DecIntegerLiteral = 0 | [1-9][0-9]*
DecLongLiteral    = {DecIntegerLiteral} [lL]

HexDigit          = [0-9a-fA-F]
HexIntegerLiteral = 0 [xX] {HexDigit} {1,8}
HexLongLiteral    = 0 [xX] {HexDigit} {1,16} [lL]

OctDigit          = [0-7]
OctIntegerLiteral = 0 [0-3]? {OctDigit} {1,10}
OctLongLiteral    = 0 [0-1]? {OctDigit} {1,21} [lL]

/* floating point literals */
DoubleLiteralPart = ({DecIntegerLiteral}|{DecIntegerLiteral}? \. [0-9]+) ([eE] [+-]? [0-9]+)?
FloatLiteral  = {DoubleLiteralPart} [fF]
DoubleLiteral = {DoubleLiteralPart} [dD]

/* string and character literals */
StringCharacter = [^\"\\]
CharCharacter = [^\r\n\'\\]

/* Delimiter */

DelimiterStatementStart     = "<%"
DelimiterStatementEnd       = "%>"
DelimiterInterpolationStart   = "${"
/* DelimiterInterpolationEnd     = "}"*/

DelimiterStatementStartMatch   = [\\]* {DelimiterStatementStart}
DelimiterInterpolationStartMatch   = [\\]* {DelimiterInterpolationStart}

lambdaArgsClosing = ")" {WhiteSpace}* "->"

MethodReference = {Identifier} ("." {Identifier})* {WhiteSpace}* ("[" {WhiteSpace}* "]" {WhiteSpace}*)* {WhiteSpace}* "::" {WhiteSpace}* {Identifier}

%state STATE_SCRIPT, STATE_TEMPLATE, STATE_STRING, STATE_RAW_STRING, STATE_TEMPLATE_STRING, STATE_CHAR_LITERAL, STATE_EOF

%%

/* text block */
<STATE_TEMPLATE>{

  /* if to STATE_SCRIPT */
  {DelimiterStatementStartMatch}        { int length = yylength() - TEXT_BLOCK_END_LEN; appendToString('\\',length/2); if((length & 1) == 0){return popTemplateTextSymbol(false);} else {appendToString('<', '%');} }

  /* if to INTERPOLATION */
  {DelimiterInterpolationStartMatch}      { int length = yylength() - INTERPOLATION_START_LEN; appendToString('\\',length/2); if((length & 1) == 0){return popTemplateTextSymbol(true);} else {appendToString('$', '{');} }


  [^]                                  { pullToString(); }

  <<EOF>>                               { yybegin(STATE_EOF); return token(TokenKinds.TEXT_STATEMENT, stringLine, stringColumn, (!trimCodeBlockBlankLine || this.leftInterpolationFlag) ? popChars() : popCharsWithoutLeadingLineEnding());}
}


/* code block */
<STATE_SCRIPT> {

  /* keywords */
  "break"                        { return token(TokenKinds.BREAK); }
  "case"                         { return token(TokenKinds.CASE); }
  "continue"                     { return token(TokenKinds.CONTINUE); }
  "do"                           { return token(TokenKinds.DO); }
  "else"                         { return token(TokenKinds.ELSE); }
  "for"                          { return token(TokenKinds.FOR); }
  "default"                      { return token(TokenKinds.DEFAULT); }
  "instanceof"                   { return token(TokenKinds.INSTANCEOF); }
  "new"                          { return token(TokenKinds.NEW); }
  "if"                           { return token(TokenKinds.IF); }
  "super"                        { return token(TokenKinds.SUPER); }
  "switch"                       { return token(TokenKinds.SWITCH); }
  "while"                        { return token(TokenKinds.WHILE); }
  "var"                          { return token(TokenKinds.VAR); }
  /* "in"                           { return token(TokenKinds.IN); } */
  "function"                     { return token(TokenKinds.FUNCTION); }
  "return"                       { return token(TokenKinds.RETURN); }
  "this"                         { return token(TokenKinds.THIS); }

  "throw"                        { return token(TokenKinds.THROW); }
  "try"                          { return token(TokenKinds.TRY); }
  "catch"                        { return token(TokenKinds.CATCH); }
  "finally"                      { return token(TokenKinds.FINALLY); }

  "native"                       { return token(TokenKinds.NATIVE); }
  "static"                       { return token(TokenKinds.STATIC); }

  "import"                       { return token(TokenKinds.IMPORT); }
  "include"                      { return token(TokenKinds.INCLUDE); }

  "echo"                         { return token(TokenKinds.ECHO); }

  "@import"                      { return token(TokenKinds.NATIVE_IMPORT); }

  "const"                        { return token(TokenKinds.CONST); }

  /* boolean literals */
  "true"                         { return token(TokenKinds.DIRECT_VALUE, Boolean.TRUE); }
  "false"                        { return token(TokenKinds.DIRECT_VALUE, Boolean.FALSE); }

  /* null literal */
  "null"                         { return token(TokenKinds.DIRECT_VALUE, null); }

  /* separators */

  "[?"                           { return token(TokenKinds.LDEBUG); }
  "?]"                           { return token(TokenKinds.RDEBUG); }
  "[?]"                          { return token(TokenKinds.LRDEBUG); }

  "("                            { return token(TokenKinds.LPAREN); }
  ")"                            { return token(TokenKinds.RPAREN); }
  "{"                            { if(templateStringFlag){ templateStringBraceClosingCounter++; } return token(TokenKinds.LBRACE); }
  "}"                            { if(templateStringFlag && templateStringBraceClosingCounter == 0){yybegin(STATE_TEMPLATE_STRING);return token(TokenKinds.TEMPLATE_STRING_INTERPOLATION_END);}else if(interpolationFlag){yybegin(STATE_TEMPLATE);leftInterpolationFlag = true;return token(TokenKinds.INTERPOLATION_END);}else{ if(templateStringFlag){templateStringBraceClosingCounter--;} return token(TokenKinds.RBRACE);} }
  "["                            { return token(TokenKinds.LBRACK); }
  "]"                            { return token(TokenKinds.RBRACK); }
  ";"                            { return token(TokenKinds.SEMICOLON); }
  ","                            { return token(TokenKinds.COMMA); }
  "."                            { return token(TokenKinds.DOT); }
  ".."                           { return token(TokenKinds.DOTDOT); }

  /* operators */
  "="                            { return token(TokenKinds.EQ); }
  ">"                            { return token(TokenKinds.GT); }
  "<"                            { return token(TokenKinds.LT); }
  "!"                            { return token(TokenKinds.NOT); }
  "~"                            { return token(TokenKinds.COMP); }
  "?"                            { return token(TokenKinds.QUESTION); }
  "::"                           { return token(TokenKinds.COLONCOLON); }
  ":"                            { return token(TokenKinds.COLON); }
//  "?:"                           { return token(TokenKinds.QUESTION_COLON); }
  "=="                           { return token(TokenKinds.EQEQ); }
  "<="                           { return token(TokenKinds.LTEQ); }
  ">="                           { return token(TokenKinds.GTEQ); }
  "!="                           { return token(TokenKinds.NOTEQ); }
  "&&"                           { return token(TokenKinds.ANDAND); }
  "||"                           { return token(TokenKinds.OROR); }
  "++"                           { return token(TokenKinds.PLUSPLUS); }
  "--"                           { return token(TokenKinds.MINUSMINUS); }
  "+"                            { return token(TokenKinds.PLUS); }
  "-"                            { return token(TokenKinds.MINUS); }
  "*"                            { return token(TokenKinds.MULT); }
  "/"                            { return token(TokenKinds.DIV); }
  "&"                            { return token(TokenKinds.AND); }
  "|"                            { return token(TokenKinds.OR); }
  "^"                            { return token(TokenKinds.XOR); }
  "%"                            { return token(TokenKinds.MOD); }
  "<<"                           { return token(TokenKinds.LSHIFT); }
  ">>"                           { return token(TokenKinds.RSHIFT); }
  ">>>"                          { return token(TokenKinds.URSHIFT); }
  "+="                           { return token(TokenKinds.SELFEQ, TokenKinds.PLUS); }
  "-="                           { return token(TokenKinds.SELFEQ, TokenKinds.MINUS); }
  "*="                           { return token(TokenKinds.SELFEQ, TokenKinds.MULT); }
  "/="                           { return token(TokenKinds.SELFEQ, TokenKinds.DIV); }
  "&="                           { return token(TokenKinds.SELFEQ, TokenKinds.AND); }
  "|="                           { return token(TokenKinds.SELFEQ, TokenKinds.OR); }
  "^="                           { return token(TokenKinds.SELFEQ, TokenKinds.XOR); }
  "%="                           { return token(TokenKinds.SELFEQ, TokenKinds.MOD); }
  "<<="                          { return token(TokenKinds.SELFEQ, TokenKinds.LSHIFT); }
  ">>="                          { return token(TokenKinds.SELFEQ, TokenKinds.RSHIFT); }
  ">>>="                         { return token(TokenKinds.SELFEQ, TokenKinds.URSHIFT); }

  ".~"                           { return token(TokenKinds.DYNAMIC_DOT); }
  "=>"                           { return token(TokenKinds.EQGT); }
  "->"                           { return token(TokenKinds.MINUSGT); }
  {lambdaArgsClosing}            { return token(TokenKinds.RPAREN_MINUSGT); }


  /* string literal */
  \"                             { yybegin(STATE_STRING); resetCharsBuffer(); }

  "r\""                          { yybegin(STATE_RAW_STRING); resetCharsBuffer(); }

  /* character literal */
  \'                             { yybegin(STATE_CHAR_LITERAL); }

  /* script string literal */
  "`"                             { if(templateStringFlag){ throw new ParseException("Illegal character '`', not support nesting script string.", getPosition()); } yybegin(STATE_TEMPLATE_STRING); this.templateStringFlag = true; templateStringBraceClosingCounter = 0; return token(TokenKinds.TEMPLATE_STRING_START); }

  /* numeric literals */

  /* Note: This is matched together with the minus, because the number is too big to
     be represented by a positive integer/long. */
  {IntegerMin}                   { return token(TokenKinds.DIRECT_VALUE, Integer.MIN_VALUE); }
  {LongMin}                      { return token(TokenKinds.DIRECT_VALUE, Long.MIN_VALUE); }

  {BinIntegerLiteral}            { return token(TokenKinds.DIRECT_VALUE, yyBinInteger(2, 0)); }
  {BinLongLiteral}               { return token(TokenKinds.DIRECT_VALUE, yyBinLong(2, -1)); }

  {DecIntegerLiteral}            { return token(TokenKinds.DIRECT_VALUE, yyDecInt(0, 0)); }
  {DecLongLiteral}               { return token(TokenKinds.DIRECT_VALUE, yyDecLong(0, -1)); }

  {HexIntegerLiteral}            { return token(TokenKinds.DIRECT_VALUE, yyInt(2, 0, 16)); }
  {HexLongLiteral}               { return token(TokenKinds.DIRECT_VALUE, yyLong(2, -1, 16)); }

  {OctIntegerLiteral}            { return token(TokenKinds.DIRECT_VALUE, yyInt(1, 0, 8)); }
  {OctLongLiteral}               { return token(TokenKinds.DIRECT_VALUE, yyLong(1, -1, 8)); }

  {FloatLiteral}                 { return token(TokenKinds.DIRECT_VALUE, Float.valueOf(yytext(0, -1))); }
  {DoubleLiteralPart}            { return token(TokenKinds.DIRECT_VALUE, Double.valueOf(yytext())); }
  {DoubleLiteral}                { return token(TokenKinds.DIRECT_VALUE, Double.valueOf(yytext(0, -1))); }

  /* comments */
  {Comment}                      { /* ignore */ }

  /* %> etc .. */
  {DelimiterStatementEnd}        { leftInterpolationFlag = false; yybegin(STATE_TEMPLATE); }

  /* identifiers */
  {Identifier}                   { return token(TokenKinds.IDENTIFIER, yytext().intern()); }

  {MethodReference}              { return token(TokenKinds.METHOD_REFERENCE, yytext()); }

  {LineTerminator}               { return SYM_NEW_LINE; }
  {Blanks}                       { /* ignore */ }
}

<STATE_EOF>{
  <<EOF>>                          { return token(TokenKinds.EOF); }
}

<STATE_STRING> {
  \"                             { yybegin(STATE_SCRIPT); return token(TokenKinds.DIRECT_VALUE, stringLine, stringColumn, popAsString()); }

  {StringCharacter}+             { pullToString(); }

  /* escape sequences */
  "\\b"                          { appendToString('\b'); }
  "\\t"                          { appendToString('\t'); }
  "\\n"                          { appendToString('\n'); }
  "\\f"                          { appendToString('\f'); }
  "\\r"                          { appendToString('\r'); }
  "\\\""                         { appendToString('\"'); }
  "\\'"                          { appendToString('\''); }
  "\\/"                          { appendToString('/'); }
  "\\\\"                         { appendToString('\\'); }
  \\[0-3]?{OctDigit}?{OctDigit}  { char val = (char) Integer.parseInt(yytext(1,0),8); appendToString(val); }

  \\{LineTerminator}             { /* escape new line */ }

  /* error cases */
  \\.                            { throw new ParseException("Illegal escape sequence \""+yytext()+"\"", getPosition()); }
}

<STATE_RAW_STRING> {
  \"                             { yybegin(STATE_SCRIPT); return token(TokenKinds.DIRECT_VALUE, stringLine, stringColumn, popAsString()); }
  [^\"]+                         { pullToString(); }
}

<STATE_TEMPLATE_STRING> {
  "`"                             { yybegin(STATE_SCRIPT); this.templateStringFlag = false; return token(TokenKinds.TEMPLATE_STRING_END, stringLine, stringColumn, popAsString()); }

  "${"                             { yybegin(STATE_SCRIPT); return token(TokenKinds.TEMPLATE_STRING_INTERPOLATION_START, stringLine, stringColumn, popAsString()); }

  /* escape sequences */
  "\\b"                          { appendToString('\b'); }
  "\\t"                          { appendToString('\t'); }
  "\\n"                          { appendToString('\n'); }
  "\\f"                          { appendToString('\f'); }
  "\\r"                          { appendToString('\r'); }
  "\\/"                          { appendToString('/'); }
  "\\\\"                         { appendToString('\\'); }
  \\[0-3]?{OctDigit}?{OctDigit}  { char val = (char) Integer.parseInt(yytext(1,0),8); appendToString(val); }

  \\{LineTerminator}             { /* escape new line */ }
  "\\`"                          { appendToString('`'); }
  "\\${"                         { appendToString('$','{'); }
  .|\r|\n                       { appendToString(yyTextChar()); }

  /* error cases */
  \\.                            { throw new ParseException("Illegal escape sequence \""+yytext()+"\"", getPosition()); }
}

<STATE_CHAR_LITERAL> {
  {CharCharacter}\'            { yybegin(STATE_SCRIPT); return token(TokenKinds.DIRECT_VALUE, yyTextChar()); }

  /* escape sequences */
  "\\b"\'                        { yybegin(STATE_SCRIPT); return token(TokenKinds.DIRECT_VALUE, '\b');}
  "\\t"\'                        { yybegin(STATE_SCRIPT); return token(TokenKinds.DIRECT_VALUE, '\t');}
  "\\n"\'                        { yybegin(STATE_SCRIPT); return token(TokenKinds.DIRECT_VALUE, '\n');}
  "\\f"\'                        { yybegin(STATE_SCRIPT); return token(TokenKinds.DIRECT_VALUE, '\f');}
  "\\r"\'                        { yybegin(STATE_SCRIPT); return token(TokenKinds.DIRECT_VALUE, '\r');}
  "\\\""\'                       { yybegin(STATE_SCRIPT); return token(TokenKinds.DIRECT_VALUE, '\"');}
  "\\'"\'                        { yybegin(STATE_SCRIPT); return token(TokenKinds.DIRECT_VALUE, '\'');}
  "\\/"\'                        { yybegin(STATE_SCRIPT); return token(TokenKinds.DIRECT_VALUE, '/');}
  "\\\\"\'                       { yybegin(STATE_SCRIPT); return token(TokenKinds.DIRECT_VALUE, '\\');}
  \\[0-3]?{OctDigit}?{OctDigit}\' { yybegin(STATE_SCRIPT); return token(TokenKinds.DIRECT_VALUE, (char) yyInt(1, -1 ,8));}

  /* error cases */
  \\.                            { throw new ParseException("Illegal escape sequence \""+yytext()+"\"", getPosition()); }
  {LineTerminator}               { throw new ParseException("Unterminated character literal at end of line", getPosition()); }
}

/* error fallback */
[^]                              { throw new ParseException("Illegal character \""+yytext()+"\" at line "+yyline+", column "+yycolumn, getPosition()); }
<<EOF>>                          { return token(TokenKinds.EOF); }

