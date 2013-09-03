// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.exceptions;

import java.io.PrintStream;
import java.io.PrintWriter;

/**
 *
 * @author Zqq
 */
public class NativeSecurityException extends Exception {

    public NativeSecurityException(String path) {
        super("Not accessable: "+path);
    }

    @Override
    public void printStackTrace(PrintStream s) {
        synchronized (s) {
            s.println(this);
        }
    }

    @Override
    public void printStackTrace(PrintWriter s) {
        synchronized (s) {
            s.println(this);
        }
    }
}
