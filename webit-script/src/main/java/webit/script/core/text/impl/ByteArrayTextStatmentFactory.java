// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.core.text.impl;

import java.io.UnsupportedEncodingException;
import webit.script.Template;
import webit.script.core.ast.Statment;
import webit.script.core.text.TextStatmentFactory;

/**
 *
 * @author Zqq
 */
public class ByteArrayTextStatmentFactory implements TextStatmentFactory {

    public void startTemplate(Template template) {
    }

    public void finishTemplate(Template template) {
    }

    public Statment getTextStatment(Template template, String text, int line, int column) {
        try {
            return new ByteArrayTextStatment(text.getBytes(template.engine.getEncoding()), line, column);
        } catch (UnsupportedEncodingException ex) {
            //Note:ignore
            return new ByteArrayTextStatment(text.getBytes(), line, column);
        }
    }
}
