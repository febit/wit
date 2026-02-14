// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.core.text;

import org.febit.wit.Template;
import org.febit.wit.runtime.Position;
import org.febit.wit.runtime.ast.Statement;
import org.febit.wit.runtime.ast.text.AdaptiveTextStatement;

public class AdaptiveTextStatementFactory extends ByteArrayTextStatementFactory {

    @Override
    public Statement create(Template template, char[] text, Position position) {
        return new AdaptiveTextStatement(text, encode(text), template.engine().charset(), position);
    }
}
