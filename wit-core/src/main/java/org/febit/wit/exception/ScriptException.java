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
import org.febit.wit.Script;
import org.jspecify.annotations.Nullable;

import java.io.PrintStream;
import java.io.PrintWriter;

public abstract class ScriptException extends RuntimeException {

    private boolean isCaused;
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
        if (cause instanceof ScriptException ex) {
            ex.isCaused = true;
        }
    }

    protected abstract void printBody(PrintStreamOrWriter out, String prefix);

    public ScriptException setScript(Script script) {
        this.script = script;
        return this;
    }

    @Override
    public void printStackTrace(PrintStream out) {
        synchronized (out) {
            printStackTrace(wrap(out));
        }
    }

    @Override
    public void printStackTrace(PrintWriter out) {
        synchronized (out) {
            printStackTrace(wrap(out));
        }
    }

    private void printStackTrace(PrintStreamOrWriter out) {
        String prefix = isCaused ? "\t" : "";
        out.print(prefix).print(this).print('\n');
        if (this.script != null) {
            out.print(prefix)
                    .print("script: ")
                    .print(this.script.path())
                    .print('\n');
        }
        printBody(out, prefix);
        Throwable ourCause = getCause();
        if (ourCause != null) {
            out.print(prefix).print("\tCaused by: \n");
            out.printTrace(ourCause);
        }
    }

    private static PrintStreamOrWriter wrap(PrintStream out) {
        return new PrintStreamOrWriter() {
            @Override
            public PrintStreamOrWriter print(Object o) {
                out.print(o);
                return this;
            }

            @Override
            public void printTrace(Throwable cause) {
                cause.printStackTrace(out);
            }
        };
    }

    private static PrintStreamOrWriter wrap(PrintWriter out) {
        return new PrintStreamOrWriter() {
            @Override
            public PrintStreamOrWriter print(Object o) {
                out.print(o);
                return this;
            }

            @Override
            public void printTrace(Throwable cause) {
                cause.printStackTrace(out);
            }
        };
    }

    public interface PrintStreamOrWriter {

        PrintStreamOrWriter print(Object o);

        void printTrace(Throwable cause);
    }
}
