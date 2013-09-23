// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.core.text.impl;

import webit.script.Template;
import webit.script.core.ast.Statment;
import webit.script.core.text.TextStatmentFactory;

/**
 *
 * @author Zqq
 */
public class CharArrayTextStatmentFactory implements TextStatmentFactory {

    public void startTemplateParser(Template template) {
    }

    public void finishTemplateParser(Template template) {
    }

    public Statment getTextStatment(Template template, char[] text, int line, int column) {
        return new CharArrayTextStatment(text, line, column);
    }
}
