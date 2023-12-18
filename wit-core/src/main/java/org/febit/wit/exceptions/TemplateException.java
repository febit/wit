// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.exceptions;

import lombok.Getter;
import org.febit.wit.Template;
import org.febit.wit.util.ExceptionUtils;
import org.febit.wit.util.ExceptionUtils.PrintStreamOrWriter;

import java.io.PrintStream;
import java.io.PrintWriter;

/**
 * @author zqq90
 */
@SuppressWarnings({
        "WeakerAccess"
})
public abstract class TemplateException extends RuntimeException {

    protected boolean isCaused;
    @Getter
    protected Template template;

    public TemplateException(String message) {
        this(message, null);
    }

    public TemplateException(Throwable cause) {
        this(cause.getMessage(), cause);
    }

    public TemplateException(String message, Throwable cause) {
        super(message, cause, true, false);
        if (cause instanceof TemplateException) {
            ((TemplateException) cause).isCaused = true;
        }
    }

    protected abstract void printBody(PrintStreamOrWriter out, String prefix);

    public TemplateException setTemplate(Template template) {
        this.template = template;
        return this;
    }

    @Override
    public void printStackTrace(PrintStream out) {
        synchronized (out) {
            printStackTrace(ExceptionUtils.wrap(out));
        }
    }

    @Override
    public void printStackTrace(PrintWriter out) {
        synchronized (out) {
            printStackTrace(ExceptionUtils.wrap(out));
        }
    }

    private void printStackTrace(PrintStreamOrWriter out) {
        String prefix = isCaused ? "\t" : "";
        out.print(prefix).print(this).print('\n');
        if (this.template != null) {
            out.print(prefix)
                    .print("template: ")
                    .print(this.template.getName())
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
