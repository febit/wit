// Copyright (c) 2013-2016, febit.org. All Rights Reserved.
package org.febit.wit.core;

import org.febit.wit.exceptions.ParseException;
import org.febit.wit.loaders.ResourceOffset;
import org.febit.wit.util.LexerCharArrayWriter;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;

%%
%class Lexer
%function _parseNextToken
%apiprivate
%type Symbol
%line
%column
%buffer 8192

%{
    //================ >> user code

    private static final int INTERPOLATION_START_LEN = 2;
    private static final int TEXT_BLOCK_END_LEN = 2;
    private static final Symbol SYM_NEW_LINE = new Symbol(-1, -1, -1, '\n');

    private boolean interpolationFlag = false;
    private boolean leftInterpolationFlag = true;
    private boolean templateStringFlag = false;
    private int templateStringBraceClosingCounter = 0;

    private final LexerCharArrayWriter stringBuffer = new LexerCharArrayWriter(256);
    private boolean trimCodeBlockBlankLine = false;
    private int stringLine = 0;
    private int stringColumn = 0;
    
    private int offsetLine = 0;
    private int offsetColumnOfFirstLine = 0;

    private final Deque<Symbol> pendingQueue = new ArrayDeque<>(8);

    private void addPendingSymbols(Symbol... syms) {
        this.pendingQueue.addAll(Arrays.asList(syms));
    }

    private Symbol _nextToken() throws java.io.IOException {
        // check pending queue first
        Deque<Symbol> pending = this.pendingQueue;
        Symbol next = pending.pollFirst();
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

    public Symbol nextToken() throws java.io.IOException {
        Symbol next;
        
        // skip new-line 
        do {
            next = _nextToken();
        } while (next == SYM_NEW_LINE);

        // EOF or SEMICOLON
        if (next.id == Tokens.EOF
                || next.id == Tokens.SEMICOLON) {
            return next;
        }
        
        // Others must check if next token is new-line or EOF
        Symbol nextAfter = _nextToken();
        // return back
        this.pendingQueue.addFirst(nextAfter);
        if (nextAfter == SYM_NEW_LINE || nextAfter.id == Tokens.EOF) {
            next.isOnEdgeOfNewLine = true;
        }
        return next;
    }

    public void close() throws java.io.IOException {
        yyclose();
    }
    
    public void setTrimCodeBlockBlankLine(boolean flag){
        trimCodeBlockBlankLine = flag;
    }
    
    public void setOffset(int offsetLine, int offsetColumnOfFirstLine){
        this.offsetLine = offsetLine;
        this.offsetColumnOfFirstLine = 1 - offsetColumnOfFirstLine;
    }
    
    public void setOffset(ResourceOffset offset){
        setOffset(offset.getOffsetLine(), offset.getOffsetColumnOfFirstLine());
    }
    
    public int getColumn(){
        return yycolumn + (yyline == offsetLine ? offsetColumnOfFirstLine : 1);
    }
    
    public int getLine(){
        return yyline - offsetLine + 1;
    }

    public char yychar(){
        return (char)yychar;
    }

    private char[] popAsCharArray() {
        char[] chars = stringBuffer.toCharArray();
        stringBuffer.reset();
        return chars;
    }

    private char[] popAsCharArrayOmitStartingLineSeparator() {
        char[] chars = stringBuffer.toArrayOmitStartingLineSeparator();
        stringBuffer.reset();
        return chars;
    }

    private String popAsString() {
        return new String(popAsCharArray());
    }

    private void resetString() {
        stringBuffer.reset();
        stringLine = yyline;
        stringColumn = yycolumn;
    }

    private void appendToString(char c) {
        stringBuffer.write(c);
    }

    private void appendToString(char c, int repeat) {
        if (repeat == 1) {
            stringBuffer.append(c);
        } else if (repeat == 2) {
            stringBuffer.append(c).append(c);
        } else if(repeat != 0){
            final char[] chars = new char[repeat];
            while (repeat != 0) {
                chars[--repeat] = c;
            }
            stringBuffer.write(chars);
        }
    }

    private void appendToString(char c, char c2) {
        stringBuffer.append(c).append(c2);
    }

    private void pullToString() {
        stringBuffer.write(zzBuffer, zzStartRead, zzMarkedPos - zzStartRead);
    }

    void codeFirst() {
        yybegin(YYSTATEMENT);
    }

    private Symbol symbol(int sym) {
        return new Symbol(sym, yyline + 1, yycolumn + 1, sym);
    }

    private Symbol symbol(int sym, Object val) {
        return new Symbol(sym, yyline + 1, yycolumn + 1, val);
    }
    
    private Symbol symbol(int sym, int line, int column, Object val) {
        return new Symbol(sym, line, column, val);
    }

    private Symbol popTextStatementSymbol(boolean interpolationFlag){
        this.interpolationFlag = interpolationFlag;
        yybegin(YYSTATEMENT);
        final char[] chars;
        if(trimCodeBlockBlankLine){
            if(!interpolationFlag){
                stringBuffer.trimRightAfterLastLineSeparator();
            }
            chars = this.leftInterpolationFlag
                    ? popAsCharArray()
                    : popAsCharArrayOmitStartingLineSeparator();
        }else{
            chars = popAsCharArray();
        }
        return symbol(Tokens.TEXT_STATEMENT, stringLine, stringColumn, chars);
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

    private long yyDecLong(int startOffset, int endOffset){
        return parseDecLong(zzBuffer, zzStartRead + startOffset, zzMarkedPos + endOffset);
    }

    private int yyDecInt(int startOffset, int endOffset){
        long result = parseDecLong(zzBuffer, zzStartRead + startOffset, zzMarkedPos + endOffset);
        if (result > Integer.MAX_VALUE || result < Integer.MIN_VALUE) {
            throw new ParseException("Number overflow", getLine(), getColumn());
        }
        return (int) result;
    }

    private long yyLong(int startOffset, int endOffset, int radix){
        return parseLong(zzBuffer, zzStartRead + startOffset, zzMarkedPos + endOffset, radix);
    }

    private int yyInt(int startOffset, int endOffset, int radix){
        return (int) parseLong(zzBuffer, zzStartRead + startOffset, zzMarkedPos + endOffset, radix);
    }

    private long yyBinLong(int startOffset, int endOffset){
        return parseBinLong(zzBuffer, zzStartRead + startOffset, zzMarkedPos + endOffset);
    }

    private int yyBinInteger(int startOffset, int endOffset){
        return (int) parseBinLong(zzBuffer, zzStartRead + startOffset, zzMarkedPos + endOffset);
    }

    private long parseBinLong(char[] buffer, int start, int end) {
        long result = 0;
        while (start < end) {
            result <<= 1;
            if(buffer[start++] == '1'){
                ++ result;
            }
        }
        return result;
    }

    private long parseDecLong(final char[] buffer, int start, final int end) {
        long result = 0;
        while (start < end) {
            if (result > Long.MAX_VALUE / 10) {
                throw new ParseException("Number overflow", getLine(), getColumn());
            }
            result *= 10;
            int digit = Character.digit(buffer[start++],10);
            if (result > (Long.MAX_VALUE - digit)) {
                throw new ParseException("Number overflow", getLine(), getColumn());
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

%state  YYSTATEMENT, STRING, RAW_STRING, TEMPLATE_STRING, CHARLITERAL, END_OF_FILE

%%

/* text block */
<YYINITIAL>{

  /* if to YYSTATEMENT */
  {DelimiterStatementStartMatch}        { int length = yylength() - TEXT_BLOCK_END_LEN; appendToString('\\',length/2); if((length & 1) == 0){return popTextStatementSymbol(false);} else {appendToString('<', '%');} }

  /* if to INTERPOLATION */
  {DelimiterInterpolationStartMatch}      { int length = yylength() - INTERPOLATION_START_LEN; appendToString('\\',length/2); if((length & 1) == 0){return popTextStatementSymbol(true);} else {appendToString('$', '{');} }
  

  [^]                                  { pullToString(); }

  <<EOF>>                               { yybegin(END_OF_FILE); return symbol(Tokens.TEXT_STATEMENT, stringLine, stringColumn, (!trimCodeBlockBlankLine || this.leftInterpolationFlag) ? popAsCharArray() : popAsCharArrayOmitStartingLineSeparator());}
}


/* code block */
<YYSTATEMENT> {

  /* keywords */
  "break"                        { return symbol(Tokens.BREAK); }
  "case"                         { return symbol(Tokens.CASE); }
  "continue"                     { return symbol(Tokens.CONTINUE); }
  "do"                           { return symbol(Tokens.DO); }
  "else"                         { return symbol(Tokens.ELSE); }
  "for"                          { return symbol(Tokens.FOR); }
  "default"                      { return symbol(Tokens.DEFAULT); }
  "instanceof"                   { return symbol(Tokens.INSTANCEOF); }
  "new"                          { return symbol(Tokens.NEW); }
  "if"                           { return symbol(Tokens.IF); }
  "super"                        { return symbol(Tokens.SUPER); }
  "switch"                       { return symbol(Tokens.SWITCH); }
  "while"                        { return symbol(Tokens.WHILE); }
  "var"                          { return symbol(Tokens.VAR); }
  /* "in"                           { return symbol(Tokens.IN); } */
  "function"                     { return symbol(Tokens.FUNCTION); }
  "return"                       { return symbol(Tokens.RETURN); }
  "this"                         { return symbol(Tokens.THIS); }

  "throw"                        { return symbol(Tokens.THROW); }
  "try"                          { return symbol(Tokens.TRY); }
  "catch"                        { return symbol(Tokens.CATCH); }
  "finally"                      { return symbol(Tokens.FINALLY); }

  "native"                       { return symbol(Tokens.NATIVE); }
  "static"                       { return symbol(Tokens.STATIC); }

  "import"                       { return symbol(Tokens.IMPORT); }
  "include"                      { return symbol(Tokens.INCLUDE); }

  "echo"                         { return symbol(Tokens.ECHO); }

  "@import"                      { return symbol(Tokens.NATIVE_IMPORT); }

  "const"                        { return symbol(Tokens.CONST); }
  
  /* boolean literals */
  "true"                         { return symbol(Tokens.DIRECT_VALUE, Boolean.TRUE); }
  "false"                        { return symbol(Tokens.DIRECT_VALUE, Boolean.FALSE); }
  
  /* null literal */
  "null"                         { return symbol(Tokens.DIRECT_VALUE, null); }

  /* separators */

  "[?"                           { return symbol(Tokens.LDEBUG); }
  "?]"                           { return symbol(Tokens.RDEBUG); }
  "[?]"                          { return symbol(Tokens.LRDEBUG); }

  "("                            { return symbol(Tokens.LPAREN); }
  ")"                            { return symbol(Tokens.RPAREN); }
  "{"                            { if(templateStringFlag){ templateStringBraceClosingCounter++; } return symbol(Tokens.LBRACE); }
  "}"                            { if(templateStringFlag && templateStringBraceClosingCounter == 0){yybegin(TEMPLATE_STRING);return symbol(Tokens.TEMPLATE_STRING_INTERPOLATION_END);}else if(interpolationFlag){yybegin(YYINITIAL);leftInterpolationFlag = true;return symbol(Tokens.INTERPOLATION_END);}else{ if(templateStringFlag){templateStringBraceClosingCounter--;} return symbol(Tokens.RBRACE);} }
  "["                            { return symbol(Tokens.LBRACK); }
  "]"                            { return symbol(Tokens.RBRACK); }
  ";"                            { return symbol(Tokens.SEMICOLON); }
  ","                            { return symbol(Tokens.COMMA); }
  "."                            { return symbol(Tokens.DOT); }
  ".."                           { return symbol(Tokens.DOTDOT); }
  
  /* operators */
  "="                            { return symbol(Tokens.EQ); }
  ">"                            { return symbol(Tokens.GT); }
  "<"                            { return symbol(Tokens.LT); }
  "!"                            { return symbol(Tokens.NOT); }
  "~"                            { return symbol(Tokens.COMP); }
  "?"                            { return symbol(Tokens.QUESTION); }
  "::"                           { return symbol(Tokens.COLONCOLON); }
  ":"                            { return symbol(Tokens.COLON); }
//  "?:"                           { return symbol(Tokens.QUESTION_COLON); }
  "=="                           { return symbol(Tokens.EQEQ); }
  "<="                           { return symbol(Tokens.LTEQ); }
  ">="                           { return symbol(Tokens.GTEQ); }
  "!="                           { return symbol(Tokens.NOTEQ); }
  "&&"                           { return symbol(Tokens.ANDAND); }
  "||"                           { return symbol(Tokens.OROR); }
  "++"                           { return symbol(Tokens.PLUSPLUS); }
  "--"                           { return symbol(Tokens.MINUSMINUS); }
  "+"                            { return symbol(Tokens.PLUS); }
  "-"                            { return symbol(Tokens.MINUS); }
  "*"                            { return symbol(Tokens.MULT); }
  "/"                            { return symbol(Tokens.DIV); }
  "&"                            { return symbol(Tokens.AND); }
  "|"                            { return symbol(Tokens.OR); }
  "^"                            { return symbol(Tokens.XOR); }
  "%"                            { return symbol(Tokens.MOD); }
  "<<"                           { return symbol(Tokens.LSHIFT); }
  ">>"                           { return symbol(Tokens.RSHIFT); }
  ">>>"                          { return symbol(Tokens.URSHIFT); }
  "+="                           { return symbol(Tokens.SELFEQ, AbstractParser.OP_PLUSEQ); }
  "-="                           { return symbol(Tokens.SELFEQ, AbstractParser.OP_MINUSEQ); }
  "*="                           { return symbol(Tokens.SELFEQ, AbstractParser.OP_MULTEQ); }
  "/="                           { return symbol(Tokens.SELFEQ, AbstractParser.OP_DIVEQ); }
  "&="                           { return symbol(Tokens.SELFEQ, AbstractParser.OP_ANDEQ); }
  "|="                           { return symbol(Tokens.SELFEQ, AbstractParser.OP_OREQ); }
  "^="                           { return symbol(Tokens.SELFEQ, AbstractParser.OP_XOREQ); }
  "%="                           { return symbol(Tokens.SELFEQ, AbstractParser.OP_MODEQ); }
  "<<="                          { return symbol(Tokens.SELFEQ, AbstractParser.OP_LSHIFTEQ); }
  ">>="                          { return symbol(Tokens.SELFEQ, AbstractParser.OP_RSHIFTEQ); }
  ">>>="                         { return symbol(Tokens.SELFEQ, AbstractParser.OP_URSHIFTEQ); }

  ".~"                           { return symbol(Tokens.DYNAMIC_DOT); }
  "=>"                           { return symbol(Tokens.EQGT); }
  "->"                           { return symbol(Tokens.MINUSGT); }
  {lambdaArgsClosing}            { return symbol(Tokens.RPAREN_MINUSGT); }

  
  /* string literal */
  \"                             { yybegin(STRING); resetString(); }

  "r\""                          { yybegin(RAW_STRING); resetString(); }

  /* character literal */
  \'                             { yybegin(CHARLITERAL); }

  /* template string literal */
  "`"                             { if(templateStringFlag){ throw new ParseException("Illegal character '`', not support nesting template string.", getLine(), getColumn()); } yybegin(TEMPLATE_STRING); this.templateStringFlag = true; templateStringBraceClosingCounter = 0; return symbol(Tokens.TEMPLATE_STRING_START); }

  /* numeric literals */

  /* Note: This is matched together with the minus, because the number is too big to 
     be represented by a positive integer/long. */
  {IntegerMin}                   { return symbol(Tokens.DIRECT_VALUE, Integer.MIN_VALUE); }
  {LongMin}                      { return symbol(Tokens.DIRECT_VALUE, Long.MIN_VALUE); }

  {BinIntegerLiteral}            { return symbol(Tokens.DIRECT_VALUE, yyBinInteger(2, 0)); }
  {BinLongLiteral}               { return symbol(Tokens.DIRECT_VALUE, yyBinLong(2, -1)); }  

  {DecIntegerLiteral}            { return symbol(Tokens.DIRECT_VALUE, yyDecInt(0, 0)); }
  {DecLongLiteral}               { return symbol(Tokens.DIRECT_VALUE, yyDecLong(0, -1)); }

  {HexIntegerLiteral}            { return symbol(Tokens.DIRECT_VALUE, yyInt(2, 0, 16)); }
  {HexLongLiteral}               { return symbol(Tokens.DIRECT_VALUE, yyLong(2, -1, 16)); }

  {OctIntegerLiteral}            { return symbol(Tokens.DIRECT_VALUE, yyInt(1, 0, 8)); }  
  {OctLongLiteral}               { return symbol(Tokens.DIRECT_VALUE, yyLong(1, -1, 8)); }

  {FloatLiteral}                 { return symbol(Tokens.DIRECT_VALUE, Float.valueOf(yytext(0, -1))); }
  {DoubleLiteralPart}            { return symbol(Tokens.DIRECT_VALUE, Double.valueOf(yytext())); }
  {DoubleLiteral}                { return symbol(Tokens.DIRECT_VALUE, Double.valueOf(yytext(0, -1))); }

  /* comments */
  {Comment}                      { /* ignore */ }

  /* %> etc .. */
  {DelimiterStatementEnd}        { leftInterpolationFlag = false; yybegin(YYINITIAL); }

  /* identifiers */
  {Identifier}                   { return symbol(Tokens.IDENTIFIER, yytext().intern()); }

  {MethodReference}              { return symbol(Tokens.METHOD_REFERENCE, yytext()); }

  {LineTerminator}               { return SYM_NEW_LINE; }
  {Blanks}                       { /* ignore */ }
}

<END_OF_FILE>{
  <<EOF>>                          { return symbol(Tokens.EOF); }
}

<STRING> {
  \"                             { yybegin(YYSTATEMENT); return symbol(Tokens.DIRECT_VALUE, stringLine, stringColumn, popAsString()); }
  
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
  \\.                            { throw new ParseException("Illegal escape sequence \""+yytext()+"\"", getLine(), getColumn()); }
}

<RAW_STRING> {
  \"                             { yybegin(YYSTATEMENT); return symbol(Tokens.DIRECT_VALUE, stringLine, stringColumn, popAsString()); }
  [^\"]+                         { pullToString(); }
}

<TEMPLATE_STRING> {
  "`"                             { yybegin(YYSTATEMENT); this.templateStringFlag = false; return symbol(Tokens.TEMPLATE_STRING_END, stringLine, stringColumn, popAsString()); }
  
  "${"                             { yybegin(YYSTATEMENT); return symbol(Tokens.TEMPLATE_STRING_INTERPOLATION_START, stringLine, stringColumn, popAsString()); }

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
  \\.                            { throw new ParseException("Illegal escape sequence \""+yytext()+"\"", getLine(), getColumn()); }
}

<CHARLITERAL> {
  {CharCharacter}\'            { yybegin(YYSTATEMENT); return symbol(Tokens.DIRECT_VALUE, yyTextChar()); }
  
  /* escape sequences */
  "\\b"\'                        { yybegin(YYSTATEMENT); return symbol(Tokens.DIRECT_VALUE, '\b');}
  "\\t"\'                        { yybegin(YYSTATEMENT); return symbol(Tokens.DIRECT_VALUE, '\t');}
  "\\n"\'                        { yybegin(YYSTATEMENT); return symbol(Tokens.DIRECT_VALUE, '\n');}
  "\\f"\'                        { yybegin(YYSTATEMENT); return symbol(Tokens.DIRECT_VALUE, '\f');}
  "\\r"\'                        { yybegin(YYSTATEMENT); return symbol(Tokens.DIRECT_VALUE, '\r');}
  "\\\""\'                       { yybegin(YYSTATEMENT); return symbol(Tokens.DIRECT_VALUE, '\"');}
  "\\'"\'                        { yybegin(YYSTATEMENT); return symbol(Tokens.DIRECT_VALUE, '\'');}
  "\\/"\'                        { yybegin(YYSTATEMENT); return symbol(Tokens.DIRECT_VALUE, '/');}
  "\\\\"\'                       { yybegin(YYSTATEMENT); return symbol(Tokens.DIRECT_VALUE, '\\');}
  \\[0-3]?{OctDigit}?{OctDigit}\' { yybegin(YYSTATEMENT); return symbol(Tokens.DIRECT_VALUE, (char) yyInt(1, -1 ,8));}
  
  /* error cases */
  \\.                            { throw new ParseException("Illegal escape sequence \""+yytext()+"\"", getLine(), getColumn()); }
  {LineTerminator}               { throw new ParseException("Unterminated character literal at end of line", getLine(), getColumn()); }
}

/* error fallback */
[^]                              { throw new ParseException("Illegal character \""+yytext()+"\" at line "+yyline+", column "+yycolumn, getLine(), getColumn()); }
<<EOF>>                          { return symbol(Tokens.EOF); }

