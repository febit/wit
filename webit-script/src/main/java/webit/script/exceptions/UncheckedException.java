// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script.exceptions;

import java.io.PrintStream;
import java.io.PrintWriter;
import webit.script.Template;

/**
 *
 * @author Zqq
 */
public abstract class UncheckedException extends RuntimeException {

    protected String message;
    protected boolean isCause = false;
    private Template template;

    public Template getTemplate() {
        return template;
    }

    public final UncheckedException setTemplate(Template template) {
        this.template = template;
        return this;
    }

    public boolean isIsCause() {
        return isCause;
    }

    public void setIsCause(boolean isCause) {
        this.isCause = isCause;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public String getLocalizedMessage() {
        return getMessage();
    }

    public UncheckedException(String message) {
        this.message = message;
    }

    public UncheckedException(Throwable cause) {
        this(cause.getMessage(), cause);
    }

    public UncheckedException(String message, Throwable cause) {
        super(cause);
        this.message = message;
        if (cause instanceof UncheckedException) {
            ((UncheckedException) cause).setIsCause(true);
        }
    }

    @Override
    public void printStackTrace(PrintStream out) {
        synchronized (out) {
            printStackTrace(new WrappedPrintStream(out));
        }
    }

    @Override
    public void printStackTrace(PrintWriter out) {
        synchronized (out) {
            printStackTrace(new WrappedPrintWriter(out));
        }
    }

    public void printStackTrace(PrintStreamOrWriter out) {
        String prefix = isCause ? "\t" : "";

        out.print(prefix).println(this);
        if (this.template != null) {
            out.print(prefix)
                    .print("template: ")
                    .println(this.template.name);
        }
        printBody(out, prefix);

        Throwable ourCause;
        if ((ourCause = getCause()) != null) {
            out.print(prefix).println("\tCaused by: ");
            out.printStackTrace(ourCause);
        }
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }

    public abstract void printBody(PrintStreamOrWriter out, String prefix);

    protected abstract static interface PrintStreamOrWriter {

        PrintStreamOrWriter println(Object o);

        PrintStreamOrWriter print(Object o);

        void printStackTrace(Throwable cause);
    }

    private static class WrappedPrintStream implements PrintStreamOrWriter {

        private final PrintStream out;

        WrappedPrintStream(PrintStream out) {
            this.out = out;
        }

        public PrintStreamOrWriter println(Object o) {
            out.println(o);
            return this;
        }

        public PrintStreamOrWriter print(Object o) {
            out.print(o);
            return this;
        }

        public void printStackTrace(Throwable cause) {
            cause.printStackTrace(out);
        }
    }

    private static class WrappedPrintWriter implements PrintStreamOrWriter {

        private final PrintWriter out;

        WrappedPrintWriter(PrintWriter out) {
            this.out = out;
        }

        public PrintStreamOrWriter println(Object o) {
            out.println(o);
            return this;
        }

        public PrintStreamOrWriter print(Object o) {
            out.print(o);
            return this;
        }

        public void printStackTrace(Throwable cause) {
            cause.printStackTrace(out);
        }
    }
}
