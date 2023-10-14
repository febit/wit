// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.core.text;

import org.febit.wit.Template;
import org.febit.wit.lang.Position;
import org.febit.wit.lang.ast.Statement;

/**
 * @author zqq90
 */
public interface TextStatementFactory {

    void startTemplateParser(Template template);

    void finishTemplateParser(Template template);

    Statement getTextStatement(Template template, char[] text, Position position);
}
