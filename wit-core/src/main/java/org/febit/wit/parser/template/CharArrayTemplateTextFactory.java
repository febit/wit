// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.parser.template;

import org.febit.wit.Script;
import org.febit.wit.parser.TemplateTextFactory;
import org.febit.wit.runtime.ast.Position;
import org.febit.wit.runtime.ast.Statement;
import org.febit.wit.runtime.ast.template.CharArrayTemplateText;

public class CharArrayTemplateTextFactory implements TemplateTextFactory {

    @Override
    public void onParserStarted(Script script) {
        // Do nothing
    }

    @Override
    public void onParserCompleted(Script script) {
        // Do nothing
    }

    @Override
    public Statement create(Script script, char[] text, Position position) {
        return new CharArrayTemplateText(text, position);
    }
}
