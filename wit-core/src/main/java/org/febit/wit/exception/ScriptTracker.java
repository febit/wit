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

import org.febit.wit.Script;
import org.jspecify.annotations.Nullable;

import java.io.PrintStream;
import java.io.PrintWriter;

public interface ScriptTracker {

    boolean nested();

    @Nullable
    Script script();

    @Nullable
    Throwable getCause();

    void printTraceBody(PrintProxy out, String indent);

    default void printTrace(PrintStream out) {
        printTrace(PrintProxy.wrap(out));
    }

    default void printTrace(PrintWriter out) {
        printTrace(PrintProxy.wrap(out));
    }

    default void printTrace(PrintProxy out) {
        synchronized (out.lock()) {
            var indent = nested() ? "\t" : "";
            out.print(indent).print(this).print('\n');

            var script = script();
            if (script != null) {
                out.print(indent)
                        .print("script: ")
                        .print(script.path())
                        .print('\n');
            }

            printTraceBody(out, indent);

            var cause = getCause();
            if (cause != null) {
                out.print(indent).print("\tCaused by: \n");
                out.printCauseTrace(cause);
            }
        }
    }

    interface PrintProxy {

        static PrintProxy wrap(PrintStream out) {
            return new PrintProxy() {
                @Override
                public Object lock() {
                    return out;
                }

                @Override
                public PrintProxy print(Object o) {
                    out.print(o);
                    return this;
                }

                @Override
                public void printCauseTrace(Throwable cause) {
                    cause.printStackTrace(out);
                }
            };
        }

        static PrintProxy wrap(PrintWriter out) {
            return new PrintProxy() {
                @Override
                public Object lock() {
                    return out;
                }

                @Override
                public PrintProxy print(Object o) {
                    out.print(o);
                    return this;
                }

                @Override
                public void printCauseTrace(Throwable cause) {
                    cause.printStackTrace(out);
                }
            };
        }

        Object lock();

        PrintProxy print(Object o);

        void printCauseTrace(Throwable cause);
    }
}
