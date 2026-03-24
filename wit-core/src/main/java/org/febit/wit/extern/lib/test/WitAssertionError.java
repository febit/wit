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
package org.febit.wit.extern.lib.test;

import lombok.Getter;
import lombok.experimental.Accessors;
import org.febit.wit.Script;
import org.febit.wit.exception.StatementTracker;
import org.febit.wit.runtime.ast.Statement;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Accessors(fluent = true)
public class WitAssertionError extends AssertionError implements StatementTracker {

    private final List<Statement> statements = new ArrayList<>(8);

    @Getter
    private final transient Script script;

    public WitAssertionError(String message, Script script) {
        super(message);
        this.script = script;
    }

    @Override
    public void add(Statement statement) {
        statements.add(statement);
    }

    @Override
    public List<Statement> statements() {
        return Collections.unmodifiableList(statements);
    }

    @Override
    public boolean nested() {
        return false;
    }

    @Override
    public void printStackTrace(PrintStream out) {
        printTrace(out);
    }

    @Override
    public void printStackTrace(PrintWriter out) {
        printTrace(out);
    }
}
