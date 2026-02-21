// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.parser.template;

import org.febit.wit.Script;
import org.febit.wit.runtime.ast.Position;
import org.febit.wit.runtime.ast.Statement;
import org.febit.wit.runtime.ast.template.AdaptiveTemplateText;

public class AdaptiveTemplateTextFactory extends ByteArrayTemplateTextFactory {

    @Override
    public Statement create(Script script, char[] text, Position position) {
        return new AdaptiveTemplateText(text, encode(text), script.wit().charset(), position);
    }
}
