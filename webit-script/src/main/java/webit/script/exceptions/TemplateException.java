// Copyright (c) 2013-2015, Webit Team. All Rights Reserved.
package webit.script.exceptions;

import java.io.PrintStream;
import java.io.PrintWriter;
import webit.script.Template;

/**
 *
 * @author zqq90
 */
public abstract class TemplateException extends RuntimeException {

    protected boolean isCause;
    protected Template template;

    public TemplateException(String message) {
        super(message);
    }

    public TemplateException(Throwable cause) {
        this(cause.getMessage(), cause);
    }

    public TemplateException(String message, Throwable cause) {
        super(message, cause);
        if (cause instanceof TemplateException) {
            ((TemplateException) cause).isCause = true;
        }
    }

    public Template getTemplate() {
        return template;
    }

    public TemplateException setTemplate(Template template) {
        this.template = template;
        return this;
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

    protected void printStackTrace(PrintStreamOrWriter out) {
        String prefix = isCause ? "\t" : "";

        out.print(prefix).println(this);
        if (this.template != null) {
            out.print(prefix)
                    .print("template: ")
                    .println(this.template.name);
        }
        printBody(out, prefix);

        Throwable ourCause = getCause();
        if (ourCause != null) {
            out.print(prefix).println("\tCaused by: ");
            out.printTrace(ourCause);
        }
    }

    @Override
    public Throwable fillInStackTrace() {
        return this;
    }

    protected abstract void printBody(PrintStreamOrWriter out, String prefix);

    protected static interface PrintStreamOrWriter {

        PrintStreamOrWriter println(Object o);

        PrintStreamOrWriter print(Object o);

        void printTrace(Throwable cause);
    }

    private static class WrappedPrintStream implements PrintStreamOrWriter {

        private final PrintStream out;

        WrappedPrintStream(PrintStream out) {
            this.out = out;
        }

        @Override
        public PrintStreamOrWriter println(Object o) {
            out.println(o);
            return this;
        }

        @Override
        public PrintStreamOrWriter print(Object o) {
            out.print(o);
            return this;
        }

        @Override
        public void printTrace(Throwable cause) {
            cause.printStackTrace(out);
        }
    }

    private static class WrappedPrintWriter implements PrintStreamOrWriter {

        private final PrintWriter out;

        WrappedPrintWriter(PrintWriter out) {
            this.out = out;
        }

        @Override
        public PrintStreamOrWriter println(Object o) {
            out.println(o);
            return this;
        }

        @Override
        public PrintStreamOrWriter print(Object o) {
            out.print(o);
            return this;
        }

        @Override
        public void printTrace(Throwable cause) {
            cause.printStackTrace(out);
        }
    }
}
