// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script.io.charset;

import java.io.IOException;
import java.io.OutputStream;

/**
 *
 * @author Zqq
 */
public interface Encoder {

    void write(char[] chars, int offset, int length, OutputStream out) throws IOException;

    void write(String string, int offset, int length, OutputStream out) throws IOException;
}
