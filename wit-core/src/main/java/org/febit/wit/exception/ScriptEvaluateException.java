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
package org.febit.wit.exception;

import org.febit.wit.runtime.ast.Statement;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ScriptEvaluateException extends ScriptException implements StatementTracker {

    private final List<Statement> statements = new ArrayList<>(8);

    public ScriptEvaluateException(String message) {
        super(message);
    }

    public ScriptEvaluateException(String message, Statement statement) {
        super(message);
        add(statement);
    }

    public ScriptEvaluateException(String message, Throwable cause) {
        super(message, cause);
    }

    public ScriptEvaluateException(String message, Throwable cause, Statement statement) {
        super(message, cause);
        add(statement);
    }

    public ScriptEvaluateException(Throwable cause) {
        super(cause);
    }

    public ScriptEvaluateException(Throwable cause, Statement statement) {
        super(cause);
        add(statement);
    }

    public static ScriptEvaluateException from(Exception ex, Statement statement) {
        if (ex instanceof ScriptEvaluateException see) {
            see.add(statement);
            return see;
        }
        return new ScriptEvaluateException(ex.toString(), ex, statement);
    }

    @Override
    public final void add(Statement statement) {
        statements.add(statement);
    }

    @Override
    public List<Statement> statements() {
        return Collections.unmodifiableList(statements);
    }
}
