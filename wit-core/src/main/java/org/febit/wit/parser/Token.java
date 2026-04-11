/*
 * Copyright 2013-present febit.org (support@febit.org)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.febit.wit.parser;

import lombok.RequiredArgsConstructor;
import org.febit.wit.ir.TextPosition;
import org.jspecify.annotations.Nullable;

@RequiredArgsConstructor
public final class Token {

    public final int kind;
    public final TextPosition pos;

    @Nullable
    public final Object value;

    /**
     * The parse state.
     */
    int state;
    boolean isAtEdgeOfNewLine = false;

    static boolean isHighestLevel(int kind) {
        return switch (kind) {
            case TokenKinds.COLON, //":"
                 TokenKinds.SEMICOLON,  //";"
                 TokenKinds.RBRACE, //"}"
                 TokenKinds.INTERPOLATION_END,  //"}"
                 TokenKinds.RPAREN,  //")"
                 TokenKinds.RBRACK,  //"]"
                 TokenKinds.IDENTIFIER, //"IDENTIFIER"
                 TokenKinds.DIRECT_VALUE  //"DIRECT_VALUE"
                    -> true;
            default -> false;
        };
    }

    @SuppressWarnings({
            "java:S1479" // too many case clauses
    })
    static String name(int kind) {
        return switch (kind) {
            case TokenKinds.EOF -> "EOF";
            case TokenKinds.ERROR -> "ERROR";
            case TokenKinds.VAR -> "var";
            case TokenKinds.IF -> "if";
            case TokenKinds.ELSE -> "else";
            case TokenKinds.FOR -> "for";
            case TokenKinds.THIS -> "this";
            case TokenKinds.SUPER -> "super";
            case TokenKinds.SWITCH -> "switch";
            case TokenKinds.CASE -> "case";
            case TokenKinds.DEFAULT -> "default";
            case TokenKinds.DO -> "do";
            case TokenKinds.WHILE -> "while";
            case TokenKinds.THROW -> "throw";
            case TokenKinds.TRY -> "try";
            case TokenKinds.CATCH -> "catch";
            case TokenKinds.FINALLY -> "finally";
            case TokenKinds.NEW -> "new";
            case TokenKinds.INSTANCEOF -> "instanceof";
            case TokenKinds.FUNCTION -> "function";
            case TokenKinds.ECHO -> "echo";
            case TokenKinds.STATIC -> "static";
            case TokenKinds.NATIVE -> "native";
            case TokenKinds.IMPORT -> "import";
            case TokenKinds.INCLUDE -> "include";
            case TokenKinds.NATIVE_IMPORT -> "@import";
            case TokenKinds.BREAK -> "break";
            case TokenKinds.CONTINUE -> "continue";
            case TokenKinds.RETURN -> "return";
            case TokenKinds.PLUSPLUS -> "++";
            case TokenKinds.MINUSMINUS -> "--";
            case TokenKinds.PLUS -> "+";
            case TokenKinds.MINUS -> "-";
            case TokenKinds.MULTI -> "*";
            case TokenKinds.DIV -> "/";
            case TokenKinds.MOD -> "%";
            case TokenKinds.LSHIFT -> "<<";
            case TokenKinds.RSHIFT -> ">>";
            case TokenKinds.URSHIFT -> ">>>";
            case TokenKinds.LT -> "<";
            case TokenKinds.GT -> ">";
            case TokenKinds.LTEQ -> "<=";
            case TokenKinds.GTEQ -> ">=";
            case TokenKinds.EQEQ -> "==";
            case TokenKinds.NOTEQ -> "!=";
            case TokenKinds.AND -> "&";
            case TokenKinds.XOR -> "^";
            case TokenKinds.OR -> "|";
            case TokenKinds.COMP -> "~";
            case TokenKinds.ANDAND -> "&&";
            case TokenKinds.OROR -> "||";
            case TokenKinds.NOT -> "!";
            case TokenKinds.QUESTION -> "?";
            case TokenKinds.SELFEQ -> "*=";
            case TokenKinds.UMINUS -> "-";
            case TokenKinds.DOT -> ".";
            case TokenKinds.COLON -> ":";
            case TokenKinds.COLONCOLON -> "::";
            case TokenKinds.COMMA -> ",";
            case TokenKinds.SEMICOLON -> ";";
            case TokenKinds.LBRACE -> "{";
            case TokenKinds.RBRACE -> "}";
            case TokenKinds.INTERPOLATION_END -> "}";
            case TokenKinds.LPAREN -> "(";
            case TokenKinds.RPAREN -> ")";
            case TokenKinds.LBRACK -> "[";
            case TokenKinds.RBRACK -> "]";
            case TokenKinds.LDEBUG -> "[?";
            case TokenKinds.RDEBUG -> "?]";
            case TokenKinds.LRDEBUG -> "[?]";
            case TokenKinds.EQGT -> "=>";
            case TokenKinds.RPAREN_MINUSGT -> ")->";
            case TokenKinds.MINUSGT -> "->";
            case TokenKinds.DYNAMIC_DOT -> ".~";
            case TokenKinds.DOTDOT -> "..";
            case TokenKinds.EQ -> "=";
            case TokenKinds.TEMPLATE_STRING_START -> "`";
            case TokenKinds.TEMPLATE_STRING_INTERPOLATION_END -> "}";
            case TokenKinds.TEMPLATE_STRING_INTERPOLATION_START -> "${";
            case TokenKinds.TEMPLATE_STRING_END -> "`";
            case TokenKinds.IDENTIFIER -> "IDENTIFIER";
            case TokenKinds.METHOD_REFERENCE -> "::";
            case TokenKinds.TEXT_STATEMENT -> "TEXT";
            case TokenKinds.DIRECT_VALUE -> "DIRECT_VALUE";
            case TokenKinds.CONST -> "const";
            default -> "UNKNOWN";
        };

    }
}
