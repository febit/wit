/*
 * Copyright 2013-present febit.org (support@febit.org)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.febit.wit.parser.template;

import org.febit.wit.engine.ParseContext;
import org.febit.wit.engine.TemplateTextFactory;
import org.febit.wit.ir.Located;
import org.febit.wit.ir.Statement;
import org.febit.wit.ir.template.CharArrayTemplateText;

public class CharArrayTemplateTextFactory implements TemplateTextFactory {

    @Override
    public void onParseStarted(ParseContext context) {
        // Do nothing
    }

    @Override
    public void onParseCompleted(ParseContext context) {
        // Do nothing
    }

    @Override
    public Statement create(ParseContext context, char[] text, Located located) {
        return new CharArrayTemplateText(text, located.position());
    }
}
