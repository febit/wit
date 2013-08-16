package webit.script.core;

import webit.script.core.java_cup.runtime.Symbol;
import webit.script.core.java_cup.runtime.Scanner;
import webit.script.util.FastCharBuffer;
import webit.script.util.RepeatChars;


%%
%public
%class Lexer
/* %cup */
%implements Tokens,Scanner
%function next_token
%type Symbol
%line
%column

%{
    //================ >> user code

    public boolean placeHolderFlag = false;

    private FastCharBuffer stringBuffer = new FastCharBuffer(256);
    private int stringLine = 0;
    private int stringColumn = 0;
    
    public int getColumn(){
        return yycolumn+1;
    }
    
    public int getLine(){
        return yyline+1;
    }
    public char yychar(){
        return (char)yychar;
    }

    private String popString() {
        String str = stringBuffer.toString();
        stringBuffer.clear();
        return str;
    }

    private void resetString() {
        stringBuffer.clear();
        stringLine = yyline;
        stringColumn = yycolumn;
    }

    private void appendToString(char c) {
        stringBuffer.append(c);
    }

    private void appendToString(char c, int repeat) {
        if(repeat > 12){
            stringBuffer.append(new RepeatChars(c, repeat));
        }else if(repeat >2){
            char[] chars = new char[repeat];
            for (int i = 0; i < repeat; i++) {
                chars[i] = c;
            }
            stringBuffer.append(chars);
        }else if (repeat == 2) {
            stringBuffer.append(c).append(c);
        }else if (repeat == 1) {
            stringBuffer.append(c);
        }
    }

    private void appendToString(String string) {
        stringBuffer.append(string);
    }

    private void pullToString() {
        stringBuffer.append(zzBuffer, zzStartRead, zzMarkedPos - zzStartRead);
    }

    private void pullToString(int startOffset, int endOffset) {
        stringBuffer.append(zzBuffer, zzStartRead + startOffset, zzMarkedPos - zzStartRead + endOffset);
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

    public final String yytext(int startOffset, int endOffset) {
        return new String(zzBuffer, zzStartRead + startOffset, zzMarkedPos - zzStartRead + endOffset);
    }

    public final String yytext(int endOffset) {
        return new String(zzBuffer, zzStartRead, zzMarkedPos - zzStartRead + endOffset);
    }

    public final char yyTextChar(int startOffset) {
        return zzBuffer[zzStartRead + startOffset];
    }

    public final char yyTextChar() {
        return zzBuffer[zzStartRead];
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

        for (int i = start; i < end; i++) {
            result <<= 1;
            if(buffer[i] == '1'){
                ++ result;
            }
        }

        return result;
    }

    /**
     * assumes correct representation of a long value for specified radix in
     * scanner buffer from
     * <code>start</code> to
     * <code>end</code>
     */
    private long parseLong(char[] buffer, int start, int end, int radix) {
        long result = 0;
        int digit;
        for (int i = start; i < end; i++) {
            digit = Character.digit(buffer[i], radix);
            result = result * radix + digit;
        }

        return result;
    }

    //================ << user code
%}


/* main character classes */
LineTerminator = \r|\n|\r\n
InputCharacter = [^\r\n]

WhiteSpace = {LineTerminator} | [ \t\f]

/* comments */
Comment = {TraditionalComment} | {EndOfLineComment} | 
          {DocumentationComment}

TraditionalComment = "/*" [^*] ~"*/" | "/*" "*"+ "/"
EndOfLineComment = "//" {InputCharacter}* {LineTerminator}?
DocumentationComment = "/*" "*"+ [^/*] ~"*/"

/* identifiers */
Identifier = [:jletter:][:jletterdigit:]*
/* Identifier = {Identifier0} | "for." {Identifier0} */

/* integer literals */

BinIntegerLiteral = 0 [bB] [01] {1,32}
BinLongLiteral = 0 [bB] [01] {1,64} [lL]

DecIntegerLiteral = 0 | [1-9][0-9]*
DecLongLiteral    = {DecIntegerLiteral} [lL]

HexIntegerLiteral = 0 [xX] 0* {HexDigit} {1,8}
HexLongLiteral    = 0 [xX] 0* {HexDigit} {1,16} [lL]
HexDigit          = [0-9a-fA-F]

OctIntegerLiteral = 0+ [1-3]? {OctDigit} {1,15}
OctLongLiteral    = 0+ 1? {OctDigit} {1,21} [lL]
OctDigit          = [0-7]

/* floating point literals */        
FloatLiteral  = ({FLit1}|{FLit2}|{FLit3}) {Exponent}? [fF]
DoubleLiteral = ({FLit1}|{FLit2}|{FLit3}) {Exponent}?

FLit1    = [0-9]+ \. [0-9]* 
FLit2    = \. [0-9]+ 
FLit3    = [0-9]+ 
Exponent = [eE] [+-]? [0-9]+

/* string and character literals */
/* StringCharacter = [^\r\n\"\\] */
StringCharacter = [^\"\\]

SingleCharacter = [^\r\n\'\\]

/* Delimiter */

DelimiterStatementStart     = "<%"
DelimiterStatementEnd       = "%>"
DelimiterPlaceholderStart   = "${"
DelimiterPlaceholderEnd     = "}"

DelimiterStatementStartMatch   = [\\]* {DelimiterStatementStart}
DelimiterPlaceholderStartMatch   = [\\]* {DelimiterPlaceholderStart}





%state  YYSTATEMENT, STRING, CHARLITERAL, END_OF_FILE

%%

/* text block */
<YYINITIAL>{

  /* if to YYSTATEMENT */
  {DelimiterStatementStartMatch}        { int length = yylength()-2;appendToString('\\',length/2);if(length%2 == 0){placeHolderFlag = false; yybegin(YYSTATEMENT);
												return symbol(TEXT_STATMENT, stringLine,stringColumn, popString());} else {appendToString("<%");} }

  /* if to PLACEHOLDER */
  {DelimiterPlaceholderStartMatch}      { int length = yylength()-2;appendToString('\\',length/2);if(length%2 == 0){placeHolderFlag = true; yybegin(YYSTATEMENT);
												return symbol(TEXT_STATMENT,  stringLine,stringColumn, popString());} else {appendToString("${");} }
  

  .|\n                                  { pullToString(); }

  <<EOF>>                               { yybegin(END_OF_FILE); return symbol(TEXT_STATMENT,  stringLine,stringColumn, popString());}
}


/* code block */
<YYSTATEMENT> {

  /* keywords */
  "break"                        { return symbol(BREAK); }
  "case"                         { return symbol(CASE); }
  "continue"                     { return symbol(CONTINUE); }
  "do"                           { return symbol(DO); }
  "else"                         { return symbol(ELSE); }
  "for"                          { return symbol(FOR); }
  "default"                      { return symbol(DEFAULT); }
  "instanceof"                   { return symbol(INSTANCEOF); }
  "new"                          { return symbol(NEW); }
  "if"                           { return symbol(IF); }
  "super"                        { return symbol(SUPER); }
  "switch"                       { return symbol(SWITCH); }
  "while"                        { return symbol(WHILE); }
  "var"                          { return symbol(VAR); }
  /* "in"                           { return symbol(IN); } */
  "function"                     { return symbol(FUNCTION); }
  "return"                       { return symbol(RETURN); }
  "this"                         { return symbol(THIS); }
  

  "throw"                        { return symbol(THROW); }
  "try"                          { return symbol(TRY); }
  "catch"                        { return symbol(CATCH); }
  "finally"                      { return symbol(FINALLY); }


  "native"                       { return symbol(NATIVE); }
  "static"                       { return symbol(STATIC); }


  "import"                       { return symbol(IMPORT); }
  "include"                      { return symbol(INCLUDE); }

  "as"                           { return symbol(AS); }
  "echo"                         { return symbol(ECHO); }

  "@import"                      { return symbol(NATIVE_IMPORT); }


/*  "const"                        { return symbol(CONST); } */
/*  "as"                           { return symbol(AS); } */



  
  /* boolean literals */
  "true"                         { return symbol(BOOLEAN_LITERAL, Boolean.TRUE); }
  "false"                        { return symbol(BOOLEAN_LITERAL, Boolean.FALSE); }
  
  /* null literal */
  "null"                         { return symbol(NULL_LITERAL, null); }
  
  
  /* separators */
  "("                            { return symbol(LPAREN); }
  ")"                            { return symbol(RPAREN); }
  "{"                            { return symbol(LBRACE); }
  "}"                            { if(!placeHolderFlag){return symbol(RBRACE);}else{yybegin(YYINITIAL); return symbol(PLACE_HOLDER_END);} }
  "["                            { return symbol(LBRACK); }
  "]"                            { return symbol(RBRACK); }
  ";"                            { return symbol(SEMICOLON); }
  ","                            { return symbol(COMMA); }
  "."                            { return symbol(DOT); }
  ".."                           { return symbol(DOTDOT); }
  
  /* operators */
  "="                            { return symbol(EQ); }
  ">"                            { return symbol(GT); }
  "<"                            { return symbol(LT); }
  "!"                            { return symbol(NOT); }
  "~"                            { return symbol(COMP); }
  "?"                            { return symbol(QUESTION); }
  ":"                            { return symbol(COLON); }
  "?:"                           { return symbol(QUESTION_COLON); }
  "=="                           { return symbol(EQEQ); }
  "<="                           { return symbol(LTEQ); }
  ">="                           { return symbol(GTEQ); }
  "!="                           { return symbol(NOTEQ); }
  "&&"                           { return symbol(ANDAND); }
  "||"                           { return symbol(OROR); }
  "++"                           { return symbol(PLUSPLUS); }
  "--"                           { return symbol(MINUSMINUS); }
  "+"                            { return symbol(PLUS); }
  "-"                            { return symbol(MINUS); }
  "*"                            { return symbol(MULT); }
  "/"                            { return symbol(DIV); }
  "&"                            { return symbol(AND); }
  "|"                            { return symbol(OR); }
  "^"                            { return symbol(XOR); }
  "%"                            { return symbol(MOD); }
  "<<"                           { return symbol(LSHIFT); }
  ">>"                           { return symbol(RSHIFT); }
  ">>>"                          { return symbol(URSHIFT); }
  "+="                           { return symbol(PLUSEQ); }
  "-="                           { return symbol(MINUSEQ); }
  "*="                           { return symbol(MULTEQ); }
  "/="                           { return symbol(DIVEQ); }
  "&="                           { return symbol(ANDEQ); }
  "|="                           { return symbol(OREQ); }
  "^="                           { return symbol(XOREQ); }
  "%="                           { return symbol(MODEQ); }
  "<<="                          { return symbol(LSHIFTEQ); }
  ">>="                          { return symbol(RSHIFTEQ); }
  ">>>="                         { return symbol(URSHIFTEQ); }

  "@"                            { return symbol(AT); }
  "=>"                           { return symbol(EQGT); }

  
  /* string literal */
  \"                             { yybegin(STRING); resetString(); }

  /* character literal */
  \'                             { yybegin(CHARLITERAL); }

  /* numeric literals */

  /* Note: This is matched together with the minus, because the number is too big to 
     be represented by a positive integer. */
  "-2147483648"                  { return symbol(INTEGER_LITERAL, Integer.MIN_VALUE); }


  {BinIntegerLiteral}            { return symbol(INTEGER_LITERAL, yyBinInteger(2, 0)); }
  {BinLongLiteral}               { return symbol(INTEGER_LITERAL, yyBinLong(2, -1)); }  

  {DecIntegerLiteral}            { return symbol(INTEGER_LITERAL, yyInt(0, 0, 10)); }
  {DecLongLiteral}               { return symbol(INTEGER_LITERAL, yyLong(0, -1, 10)); }
  
  {HexIntegerLiteral}            { return symbol(INTEGER_LITERAL, yyInt(2, 0, 16)); }
  {HexLongLiteral}               { return symbol(INTEGER_LITERAL, yyLong(2, -1, 16)); }
 
  {OctIntegerLiteral}            { return symbol(INTEGER_LITERAL, yyInt(1, 0, 8)); }  
  {OctLongLiteral}               { return symbol(INTEGER_LITERAL, yyLong(1, -1, 8)); }
  
  {FloatLiteral}                 { return symbol(FLOATING_POINT_LITERAL, new Float(yytext(0, -1))); }
  {DoubleLiteral}                { return symbol(FLOATING_POINT_LITERAL, new Double(yytext())); }
  {DoubleLiteral}[dD]            { return symbol(FLOATING_POINT_LITERAL, new Double(yytext(0, -1))); }
  
  /* comments */
  {Comment}                      { /* ignore */ }

  /* whitespace */
  {WhiteSpace}                   { /* ignore */ }

  /* identifiers */
  {Identifier}                   { return symbol(IDENTIFIER, yytext().intern()); }

  /* %> */
  {DelimiterStatementEnd}        { yybegin(YYINITIAL); }


}
<END_OF_FILE>{
  <<EOF>>                          { return symbol(EOF); }
}


<STRING> {
  \"                             { yybegin(YYSTATEMENT); return symbol(STRING_LITERAL, stringLine,stringColumn, popString()); }
  
  {StringCharacter}+             { pullToString(); }
  
  /* escape sequences */
  "\\b"                          { appendToString( '\b' ); }
  "\\t"                          { appendToString( '\t' ); }
  "\\n"                          { appendToString( '\n' ); }
  "\\f"                          { appendToString( '\f' ); }
  "\\r"                          { appendToString( '\r' ); }
  "\\\""                         { appendToString( '\"' ); }
  "\\'"                          { appendToString( '\'' ); }
  "\\\\"                         { appendToString( '\\' ); }
  \\[0-3]?{OctDigit}?{OctDigit}  { char val = (char) Integer.parseInt(yytext(1,0),8); appendToString(val); }

  \\{LineTerminator}             { /* escape new line */ }
  
  /* error cases */
  \\.                            { throw new RuntimeException("Illegal escape sequence \""+yytext()+"\""); }
}

<CHARLITERAL> {
  {SingleCharacter}\'            { yybegin(YYSTATEMENT); return symbol(CHARACTER_LITERAL, yyTextChar()); }
  
  /* escape sequences */
  "\\b"\'                        { yybegin(YYSTATEMENT); return symbol(CHARACTER_LITERAL, '\b');}
  "\\t"\'                        { yybegin(YYSTATEMENT); return symbol(CHARACTER_LITERAL, '\t');}
  "\\n"\'                        { yybegin(YYSTATEMENT); return symbol(CHARACTER_LITERAL, '\n');}
  "\\f"\'                        { yybegin(YYSTATEMENT); return symbol(CHARACTER_LITERAL, '\f');}
  "\\r"\'                        { yybegin(YYSTATEMENT); return symbol(CHARACTER_LITERAL, '\r');}
  "\\\""\'                       { yybegin(YYSTATEMENT); return symbol(CHARACTER_LITERAL, '\"');}
  "\\'"\'                        { yybegin(YYSTATEMENT); return symbol(CHARACTER_LITERAL, '\'');}
  "\\\\"\'                       { yybegin(YYSTATEMENT); return symbol(CHARACTER_LITERAL, '\\');}
  \\[0-3]?{OctDigit}?{OctDigit}\' { yybegin(YYSTATEMENT); return symbol(CHARACTER_LITERAL, (char) yyInt(1, -1 ,8));}
  
  /* error cases */
  \\.                            { throw new RuntimeException("Illegal escape sequence \""+yytext()+"\""); }
  {LineTerminator}               { throw new RuntimeException("Unterminated character literal at end of line"); }
}

/* error fallback */
.|\n                             { throw new RuntimeException("Illegal character \""+yytext()+
                                                              "\" at line "+yyline+", column "+yycolumn); }
<<EOF>>                          { return symbol(EOF); }


















