// Copyright (c) 2013, Webit Team. All Rights Reserved.

package webit.script.core.text.impl;

import webit.script.Template;
import webit.script.core.ast.Statment;
import webit.script.core.text.TextStatmentFactory;

/**
 *
 * @author Zqq
 */
public class SimpleTextStatmentFactory implements TextStatmentFactory {

    public void startTemplate(Template template) {
    }

    public void finishTemplate(Template template) {
    }

    public Statment getTextStatment(Template template, String text, int line, int column) {
        return new SimpleTextStatment(text,template.engine.getEncoding(), line, column);
    }
}
