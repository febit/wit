// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 *
 * @author Zqq
 */
public class StreamUtil {

    public static void flushAndClose(OutputStream out) {
        if (out != null) {
            try {
                out.flush();
            } catch (IOException ioex) {
                // ignore
            }
            try {
                out.close();
            } catch (IOException ioex) {
                // ignore
            }
        }
    }

    public static void close(OutputStream out) {
        if (out != null) {
            try {
                out.close();
            } catch (IOException ioex) {
                // ignore
            }
        }
    }

    public static void close(InputStream in) {
        if (in != null) {
            try {
                in.close();
            } catch (IOException ioex) {
                // ignore
            }
        }
    }
}
