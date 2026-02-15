// Copyright (c) 2013-present, febit.org. All Rights Reserved.
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
