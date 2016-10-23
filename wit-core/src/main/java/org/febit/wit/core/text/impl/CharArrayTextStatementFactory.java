// Copyright (c) 2013-2016, febit.org. All Rights Reserved.
package org.febit.wit.core.text.impl;

import org.febit.wit.Template;
import org.febit.wit.core.ast.Statement;
import org.febit.wit.core.text.TextStatementFactory;

/**
 *
 * @author zqq90
 */
public class CharArrayTextStatementFactory implements TextStatementFactory {

    @Override
    public void startTemplateParser(Template template) {
    }

    @Override
    public void finishTemplateParser(Template template) {
    }

    @Override
    public Statement getTextStatement(Template template, char[] text, int line, int column) {
        return new CharArrayTextStatement(text, line, column);
    }
}
