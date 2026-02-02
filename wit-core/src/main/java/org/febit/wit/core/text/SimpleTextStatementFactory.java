// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.core.text;

import org.febit.wit.Template;
import org.febit.wit.lang.Position;
import org.febit.wit.lang.ast.Statement;
import org.febit.wit.lang.ast.text.SimpleTextStatement;

public class SimpleTextStatementFactory extends ByteArrayTextStatementFactory {

    @Override
    public Statement create(Template template, char[] text, Position position) {
        return new SimpleTextStatement(text, encode(text), template.engine().charset(), position);
    }
}
