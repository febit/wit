// Copyright (c) 2013-2016, febit.org. All Rights Reserved.
package org.febit.wit.exceptions;

import java.io.PrintStream;
import java.io.PrintWriter;
import org.febit.wit.Template;
import org.febit.wit.util.ExceptionUtil;
import org.febit.wit.util.ExceptionUtil.PrintStreamOrWriter;

/**
 *
 * @author zqq90
 */
public abstract class TemplateException extends RuntimeException {

    protected boolean isCaused;
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
            ((TemplateException) cause).isCaused = true;
        }
    }

    protected abstract void printBody(PrintStreamOrWriter out, String prefix);

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
            printStackTrace(ExceptionUtil.wrap(out));
        }
    }

    @Override
    public void printStackTrace(PrintWriter out) {
        synchronized (out) {
            printStackTrace(ExceptionUtil.wrap(out));
        }
    }

    protected void printStackTrace(PrintStreamOrWriter out) {
        String prefix = isCaused ? "\t" : "";
        out.print(prefix).print(this).print('\n');
        if (this.template != null) {
            out.print(prefix)
                    .print("template: ")
                    .print(this.template.name)
                    .print('\n');
        }
        printBody(out, prefix);
        Throwable ourCause = getCause();
        if (ourCause != null) {
            out.print(prefix).print("\tCaused by: \n");
            out.printTrace(ourCause);
        }
    }

}
