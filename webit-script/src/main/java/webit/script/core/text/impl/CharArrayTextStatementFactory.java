// Copyright (c) 2013-2015, Webit Team. All Rights Reserved.
package webit.script.core.text.impl;

import webit.script.Template;
import webit.script.core.ast.Statement;
import webit.script.core.text.TextStatementFactory;

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
