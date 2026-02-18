// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.parser.text;

import org.febit.wit.Script;
import org.febit.wit.runtime.ast.Position;
import org.febit.wit.runtime.ast.Statement;
import org.febit.wit.runtime.ast.text.AdaptiveTextStatement;

public class AdaptiveTextStatementFactory extends ByteArrayTextStatementFactory {

    @Override
    public Statement create(Script script, char[] text, Position position) {
        return new AdaptiveTextStatement(text, encode(text), script.engine().charset(), position);
    }
}
