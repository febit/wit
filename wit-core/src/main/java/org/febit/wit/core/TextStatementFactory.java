// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.core;

import org.febit.wit.Script;
import org.febit.wit.runtime.ast.Position;
import org.febit.wit.runtime.ast.Statement;

public interface TextStatementFactory {

    void onParserStarted(Script script);

    void onParserCompleted(Script script);

    Statement create(Script script, char[] text, Position position);
}
