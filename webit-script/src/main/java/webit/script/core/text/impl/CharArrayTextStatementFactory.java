// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.core.text.impl;

import webit.script.Template;
import webit.script.core.ast.Statement;
import webit.script.core.text.TextStatementFactory;

/**
 *
 * @author zqq90 <zqq_90@163.com>
 */
public class CharArrayTextStatementFactory implements TextStatementFactory {

    public void startTemplateParser(Template template) {
    }

    public void finishTemplateParser(Template template) {
    }

    public Statement getTextStatement(Template template, char[] text, int line, int column) {
        return new CharArrayTextStatement(text, line, column);
    }
}
