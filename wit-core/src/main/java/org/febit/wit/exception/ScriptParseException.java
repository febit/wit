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
import org.febit.wit.runtime.ast.Position;
import org.febit.wit.runtime.ast.TextPosition;

public class ScriptParseException extends ScriptException {

    @Getter
    private final Position position;

    public ScriptParseException(String message) {
        this(message, TextPosition.UNKNOWN);
    }

    public ScriptParseException(String message, Position position) {
        super(message);
        this.position = position;
    }

    public ScriptParseException(String message, Throwable cause) {
        this(message, cause, TextPosition.UNKNOWN);
    }

    public ScriptParseException(String message, Throwable cause, Position position) {
        super(message, cause);
        this.position = position;
    }

    public ScriptParseException(Throwable cause) {
        this(cause, TextPosition.UNKNOWN);
    }

    public ScriptParseException(Throwable cause, Position position) {
        super(cause);
        this.position = position;
    }

    @Override
    public void printTraceBody(ScriptTracker.PrintProxy out, String prefix) {
        out.print(prefix)
                .print("\tat ")
                .print(this.position);
    }
}
