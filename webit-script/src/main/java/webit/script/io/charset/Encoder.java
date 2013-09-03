// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.io.charset;

import java.io.IOException;
import java.io.OutputStream;

/**
 *
 * @author Zqq
 */
public interface Encoder {

    public static final byte REPLACEMENT = (byte) '?';

    public void write(char[] chars, int offset, int length, OutputStream out) throws IOException;

    public void write(String string, int offset, int length, OutputStream out) throws IOException;
}
