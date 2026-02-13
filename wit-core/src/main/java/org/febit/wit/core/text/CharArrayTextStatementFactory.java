// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.core.text;

import org.febit.wit.Template;
import org.febit.wit.core.TextStatementFactory;
import org.febit.wit.runtime.Position;
import org.febit.wit.runtime.ast.Statement;
import org.febit.wit.runtime.ast.text.CharArrayTextStatement;

public class CharArrayTextStatementFactory implements TextStatementFactory {

    @Override
    public void onParserStarted(Template template) {
        // Do nothing
    }

    @Override
    public void onParserCompleted(Template template) {
        // Do nothing
    }

    @Override
    public Statement create(Template template, char[] text, Position position) {
        return new CharArrayTextStatement(text, position);
    }
}
