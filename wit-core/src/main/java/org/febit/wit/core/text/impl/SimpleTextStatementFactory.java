// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.core.text.impl;

import org.febit.wit.Template;
import org.febit.wit.lang.Position;
import org.febit.wit.lang.ast.Statement;

/**
 * @author zqq90
 */
public class SimpleTextStatementFactory extends ByteArrayTextStatementFactory {

    @Override
    public Statement getTextStatement(Template template, char[] text, Position position) {
        return new SimpleTextStatement(text, getBytes(text), this.encoding, position);
    }
}
