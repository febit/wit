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

import lombok.extern.slf4j.Slf4j;
import org.febit.wit.Feature;
import org.febit.wit.Script;
import org.febit.wit.exception.ScriptParseException;
import org.febit.wit.exception.UncheckedException;
import org.febit.wit.ir.ScriptIR;
import org.febit.wit.ir.TextPosition;
import org.febit.wit.parser.support.Stack;
import org.febit.wit.util.ClassUtils;
import org.jspecify.annotations.Nullable;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Objects;

@Slf4j
abstract class AbstractParser {

    private static final short[][] PRODUCTION_TABLE = loadTable("Production");
    private static final short[][] ACTION_TABLE = loadTable("Action");
    private static final short[][] REDUCE_TABLE = loadTable("Reduce");

    final Stack<Token> tokenStack = new Stack<>(24);

    Assembler assembler;

    /**
     * flag to stop parser
     */
    boolean goonParse;

    AbstractParser() {
    }

    private static short lookupAction(int state, final int sym) {
        var row = ACTION_TABLE[state];
        var size = row.length;

        // linear search if we are < 10 entries
        if (size < 20) {
            for (int i = 0; i < size; i++) {
                if (row[i++] == sym) {
                    return row[i];
                }
            }
            // No match
            return 0;
        }

        // otherwise binary search
        int first = 0;
        int last = (size - 1) >> 1;
        while (first <= last) {
            var i = (first + last) >> 1;
            var j = i << 1;
            if (sym == row[j]) {
                return row[j + 1];
            }
            if (sym > row[j]) {
                first = i + 1;
            } else {
                last = i - 1;
            }
        }
        // No match
        return 0;
    }

    @SuppressWarnings({
            "squid:ForLoopCounterChangedCheck"
    })
    private static short lookupReduce(int state, int sym) {
        var row = REDUCE_TABLE[state];
        if (row != null) {
            for (int i = 0, len = row.length; i < len; i++) {
                if (row[i++] == sym) {
                    return row[i];
                }
            }
        }
        //error
        return -1;
    }

    private static short[][] loadTable(String name) {
        try (var in = new ObjectInputStream(ClassUtils.loader()
                .getResourceAsStream("org/febit/wit/parser/Parser$" + name + ".data")
        )) {
            return (short[][]) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new UncheckedException(e);
        }
    }

    private static String buildSimpleHintMessage(Token token) {
        var row = ACTION_TABLE[token.state];
        var len = row.length;
        if (len == 0) {
            return "[no hints]";
        }
        var higherLevel = len > 8;
        if (higherLevel && lookupAction(token.state, TokenKinds.SEMICOLON) != 0) {
            return "forget ';' ?";
        }
        var buf = new StringBuilder();
        boolean notFirst = false;
        short sym;
        for (int i = 0; i < len; i += 2) {
            sym = row[i];
            if (higherLevel && !Token.isHighestLevel(sym)) {
                continue;
            }
            if (notFirst) {
                buf.append(", ");
            } else {
                notFirst = true;
            }
            buf.append('\'')
                    .append(Token.name(sym))
                    .append('\'');
        }
        return buf.toString();
    }

    private static Token createLooseSemicolonToken(Token refer) {
        return new Token(TokenKinds.SEMICOLON, refer.pos, null);
    }

    public static ScriptIR parse(Script script) throws ScriptParseException {
        return new Parser().parse0(script);
    }

    @Nullable
    abstract Object doAction(int actionId) throws ScriptParseException;

    /**
     * @param script Script
     * @return ScriptIR
     * @throws ScriptParseException ScriptParseException
     */
    protected ScriptIR parse0(Script script) throws ScriptParseException {
        this.assembler = new Assembler(script);

        var source = script.source();
        Lexer lexer = null;
        try {
            //ISSUE: LexerProvider
            lexer = new Lexer(source.open());
            lexer.setTrimCodeBlockBlankLine(assembler.isEnabled(Feature.TRIM_CODE_BLOCK_BLANK_LINE));
            lexer.beginWith(source.beginWith());
            lexer.setOffset(source);
            this.assembler.onParserStarted();

            var ast = this.process(lexer).value;
            Objects.requireNonNull(ast, "Parser result is null.");
            return (ScriptIR) ast;
        } catch (ScriptParseException e) {
            throw e;
        } catch (Exception e) {
            throw new ScriptParseException(e);
        } finally {
            this.assembler.onParserCompleted();
            if (lexer != null) {
                try {
                    lexer.close();
                } catch (IOException ex) {
                    log.warn("Cannot close lexer", ex);
                }
            }
        }
    }

    @SuppressWarnings({
            "squid:S135", // Loops should not contain more than a single "break" or "continue" statement
            "java:S6541", // Methods should not perform too many tasks (aka Brain method)
            "squid:S3776" // Cognitive Complexity of methods should not be too high
    })
    private Token process(Lexer lexer) throws IOException {
        var looseSemicolon = assembler.isEnabled(Feature.LOOSE_SEMICOLON);

        var stack = this.tokenStack;
        stack.clear();

        // Start Token
        var tail = new Token(0, TextPosition.UNKNOWN, null);
        tail.state = 0;
        stack.push(tail);

        Token pendingPending = null;
        var pending = lexer.nextToken();

        goonParse = true;
        do {
            // look up action out of the current state with the current input
            var act = lookupAction(tail.state, pending.kind);

            // decode the action -- > 0 encodes shift
            if (act > 0) {
                // shift to the encoded state by pushing it on the _stack
                pending.state = act - 1;
                stack.push(pending);
                tail = pending;

                // next token
                if (pendingPending != null) {
                    pending = pendingPending;
                    pendingPending = null;
                } else {
                    pending = lexer.nextToken();
                    if (looseSemicolon && tail.isAtEdgeOfNewLine) {
                        switch (pending.kind) {
                            case TokenKinds.LBRACK -> {
                                if (tail.kind != TokenKinds.COMMA
                                        && tail.kind != TokenKinds.LBRACE) {
                                    pendingPending = pending;
                                    pending = createLooseSemicolonToken(pendingPending);
                                }
                            }
                            case TokenKinds.LBRACE,
                                 TokenKinds.LPAREN,
                                 TokenKinds.PLUSPLUS,
                                 TokenKinds.MINUSMINUS -> {
                                pendingPending = pending;
                                pending = createLooseSemicolonToken(pendingPending);
                            }
                            default -> {
                                // Do nothing
                            }
                        }
                    }
                }
                if (looseSemicolon
                        && pendingPending == null
                        && pending.isAtEdgeOfNewLine) {
                    switch (pending.kind) {
                        case TokenKinds.RETURN,
                             TokenKinds.BREAK,
                             TokenKinds.CONTINUE -> pendingPending = createLooseSemicolonToken(pending);
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
                    && pending.kind != TokenKinds.SEMICOLON
                    && (tail.isAtEdgeOfNewLine || pending.kind == TokenKinds.RBRACE)) {
                act = lookupAction(tail.state, TokenKinds.SEMICOLON);
                if (act != 0) {
                    pendingPending = pending;
                    pending = createLooseSemicolonToken(pendingPending);
                    if (act > 0) {
                        // go back to do
                        continue;
                    }
                }
            }
            if (act == 0) {
                throw new ScriptParseException("Syntax error at line " + lexer.getLine()
                        + " column " + lexer.getColumn()
                        + ", Hints: " + buildSimpleHintMessage(tail),
                        TextPosition.of(lexer.getLine(), lexer.getColumn())
                );
            }
            boolean isLastSymbolAtEdgeOfNewLine = tail.isAtEdgeOfNewLine;
            // if its less than zero, then it encodes a reduce action
            tail = productAction(-act - 1);
            tail.isAtEdgeOfNewLine = isLastSymbolAtEdgeOfNewLine;
            stack.push(tail);
        } while (goonParse);

        return stack.peek();
    }

    private Token productAction(int act) {
        var stack = this.tokenStack;
        var result = doAction(act);

        var production = PRODUCTION_TABLE[act];
        var tokenKind = production[0];
        var handleSize = production[1];

        Token token;
        if (handleSize == 0) {
            token = new Token(tokenKind, TextPosition.UNKNOWN, result);
        } else {
            //position based on left
            token = new Token(tokenKind, stack.peek(handleSize - 1).pos, result);
            //pop the handle
            stack.pops(handleSize);
        }

        // look up the state to go to from the one popped back to shift to that state
        token.state = lookupReduce(stack.peek().state, tokenKind);
        return token;
    }

}
