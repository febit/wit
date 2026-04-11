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

import lombok.Getter;
import lombok.experimental.Accessors;
import org.febit.wit.Script;
import org.febit.wit.ir.Located;
import org.jspecify.annotations.Nullable;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Accessors(fluent = true)
public abstract class ScriptException extends RuntimeException implements ScriptTracker {

    private final List<Located> locations = new ArrayList<>(8);
    @Getter
    private boolean nested;
    @Getter
    @Nullable
    private Script script;

    protected ScriptException(String message) {
        this(message, null);
    }

    protected ScriptException(Throwable cause) {
        this(cause.getMessage(), cause);
    }

    protected ScriptException(String message, @Nullable Throwable cause) {
        super(message, cause, true, false);
        if (cause instanceof ScriptException se) {
            se.nested = true;
        }
    }

    public ScriptException script(Script script) {
        this.script = script;
        return this;
    }

    @Override
    public void add(Located located) {
        locations.add(located);
    }

    @Override
    public List<Located> locations() {
        return Collections.unmodifiableList(locations);
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
