// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.core.text.impl;

import org.febit.wit.Template;
import org.febit.wit.core.ast.Statement;

/**
 * @author zqq90
 */
public class SimpleTextStatementFactory extends ByteArrayTextStatementFactory {

    @Override
    public Statement getTextStatement(Template template, char[] text, int line, int column) {
        return new SimpleTextStatement(text, getBytes(text), this.encoding, line, column);
    }
}
