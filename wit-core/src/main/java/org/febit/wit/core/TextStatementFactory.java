// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.core;

import org.febit.wit.Template;
import org.febit.wit.runtime.Position;
import org.febit.wit.runtime.ast.Statement;

public interface TextStatementFactory {

    void onParserStarted(Template template);

    void onParserCompleted(Template template);

    Statement create(Template template, char[] text, Position position);
}
