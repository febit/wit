package org.febit.wit.parser;

import org.junit.jupiter.api.Test;

import static org.febit.wit.parser.Token.name;
import static org.junit.jupiter.api.Assertions.*;

class TokenTest {

    @Test
    void testHighestLevel() {
        assertTrue(Token.isHighestLevel(TokenKinds.COLON));
        assertTrue(Token.isHighestLevel(TokenKinds.SEMICOLON));
        assertTrue(Token.isHighestLevel(TokenKinds.RBRACE));
        assertTrue(Token.isHighestLevel(TokenKinds.INTERPOLATION_END));
        assertTrue(Token.isHighestLevel(TokenKinds.RPAREN));
        assertTrue(Token.isHighestLevel(TokenKinds.RBRACK));
        assertTrue(Token.isHighestLevel(TokenKinds.IDENTIFIER));
        assertTrue(Token.isHighestLevel(TokenKinds.DIRECT_VALUE));

        assertFalse(Token.isHighestLevel(TokenKinds.VAR));
    }

    @Test
    void testName() {
        assertEquals("EOF", name(TokenKinds.EOF));
        assertEquals("EOF", name(TokenKinds.EOF));
        assertEquals("ERROR", name(TokenKinds.ERROR));
        assertEquals("var", name(TokenKinds.VAR));
        assertEquals("if", name(TokenKinds.IF));
        assertEquals("else", name(TokenKinds.ELSE));
        assertEquals("for", name(TokenKinds.FOR));
        assertEquals("this", name(TokenKinds.THIS));
        assertEquals("super", name(TokenKinds.SUPER));
        assertEquals("switch", name(TokenKinds.SWITCH));
        assertEquals("case", name(TokenKinds.CASE));
        assertEquals("default", name(TokenKinds.DEFAULT));
        assertEquals("do", name(TokenKinds.DO));
        assertEquals("while", name(TokenKinds.WHILE));
        assertEquals("throw", name(TokenKinds.THROW));
        assertEquals("try", name(TokenKinds.TRY));
        assertEquals("catch", name(TokenKinds.CATCH));
        assertEquals("finally", name(TokenKinds.FINALLY));
        assertEquals("new", name(TokenKinds.NEW));
        assertEquals("instanceof", name(TokenKinds.INSTANCEOF));
        assertEquals("function", name(TokenKinds.FUNCTION));
        assertEquals("echo", name(TokenKinds.ECHO));
        assertEquals("static", name(TokenKinds.STATIC));
        assertEquals("native", name(TokenKinds.NATIVE));
        assertEquals("import", name(TokenKinds.IMPORT));
        assertEquals("include", name(TokenKinds.INCLUDE));
        assertEquals("@import", name(TokenKinds.NATIVE_IMPORT));
        assertEquals("break", name(TokenKinds.BREAK));
        assertEquals("continue", name(TokenKinds.CONTINUE));
        assertEquals("return", name(TokenKinds.RETURN));
        assertEquals("++", name(TokenKinds.PLUSPLUS));
        assertEquals("--", name(TokenKinds.MINUSMINUS));
        assertEquals("+", name(TokenKinds.PLUS));
        assertEquals("-", name(TokenKinds.MINUS));
        assertEquals("*", name(TokenKinds.MULTI));
        assertEquals("/", name(TokenKinds.DIV));
        assertEquals("%", name(TokenKinds.MOD));
        assertEquals("<<", name(TokenKinds.LSHIFT));
        assertEquals(">>", name(TokenKinds.RSHIFT));
        assertEquals(">>>", name(TokenKinds.URSHIFT));
        assertEquals("<", name(TokenKinds.LT));
        assertEquals(">", name(TokenKinds.GT));
        assertEquals("<=", name(TokenKinds.LTEQ));
        assertEquals(">=", name(TokenKinds.GTEQ));
        assertEquals("==", name(TokenKinds.EQEQ));
        assertEquals("!=", name(TokenKinds.NOTEQ));
        assertEquals("&", name(TokenKinds.AND));
        assertEquals("^", name(TokenKinds.XOR));
        assertEquals("|", name(TokenKinds.OR));
        assertEquals("~", name(TokenKinds.COMP));
        assertEquals("&&", name(TokenKinds.ANDAND));
        assertEquals("||", name(TokenKinds.OROR));
        assertEquals("!", name(TokenKinds.NOT));
        assertEquals("?", name(TokenKinds.QUESTION));
        assertEquals("*=", name(TokenKinds.SELFEQ));
        assertEquals("-", name(TokenKinds.UMINUS));
        assertEquals(".", name(TokenKinds.DOT));
        assertEquals(":", name(TokenKinds.COLON));
        assertEquals("::", name(TokenKinds.COLONCOLON));
        assertEquals(",", name(TokenKinds.COMMA));
        assertEquals(";", name(TokenKinds.SEMICOLON));
        assertEquals("{", name(TokenKinds.LBRACE));
        assertEquals("}", name(TokenKinds.RBRACE));
        assertEquals("}", name(TokenKinds.INTERPOLATION_END));
        assertEquals("(", name(TokenKinds.LPAREN));
        assertEquals(")", name(TokenKinds.RPAREN));
        assertEquals("[", name(TokenKinds.LBRACK));
        assertEquals("]", name(TokenKinds.RBRACK));
        assertEquals("[?", name(TokenKinds.LDEBUG));
        assertEquals("?]", name(TokenKinds.RDEBUG));
        assertEquals("[?]", name(TokenKinds.LRDEBUG));
        assertEquals("=>", name(TokenKinds.EQGT));
        assertEquals(")->", name(TokenKinds.RPAREN_MINUSGT));
        assertEquals("->", name(TokenKinds.MINUSGT));
        assertEquals(".~", name(TokenKinds.DYNAMIC_DOT));
        assertEquals("..", name(TokenKinds.DOTDOT));
        assertEquals("=", name(TokenKinds.EQ));
        assertEquals("`", name(TokenKinds.TEMPLATE_STRING_START));
        assertEquals("}", name(TokenKinds.TEMPLATE_STRING_INTERPOLATION_END));
        assertEquals("${", name(TokenKinds.TEMPLATE_STRING_INTERPOLATION_START));
        assertEquals("`", name(TokenKinds.TEMPLATE_STRING_END));
        assertEquals("IDENTIFIER", name(TokenKinds.IDENTIFIER));
        assertEquals("::", name(TokenKinds.METHOD_REFERENCE));
        assertEquals("TEXT", name(TokenKinds.TEXT_STATEMENT));
        assertEquals("DIRECT_VALUE", name(TokenKinds.DIRECT_VALUE));
        assertEquals("const", name(TokenKinds.CONST));
    }

}
