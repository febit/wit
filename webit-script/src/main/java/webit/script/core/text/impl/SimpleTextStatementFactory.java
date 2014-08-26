// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script.core.text.impl;

import webit.script.Template;
import webit.script.core.ast.Statement;

/**
 *
 * @author zqq90
 */
public class SimpleTextStatementFactory extends ByteArrayTextStatementFactory{

    @Override
    public Statement getTextStatement(Template template, char[] text, int line, int column) {
        return new SimpleTextStatement(text, getBytes(text), this.encoding, line, column);
    }
}
