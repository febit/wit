// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.util;

import java.io.PrintStream;
import java.io.PrintWriter;

/**
 *
 * @author zqq90
 */
public class ExceptionUtil {

    private ExceptionUtil() {
    }

    public static void ignore(Throwable ex) {
        // ignore 
    }

    public static PrintStreamOrWriter wrap(final PrintStream out) {
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

    public static PrintStreamOrWriter wrap(final PrintWriter out) {
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
