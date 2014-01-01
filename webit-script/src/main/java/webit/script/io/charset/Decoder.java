// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script.io.charset;

import java.io.IOException;
import java.io.Writer;

/**
 *
 * @author Zqq
 */
public interface Decoder {

    public static final char REPLACEMENT = '\uFFFD';

    void write(byte[] bytes, int off, int len, Writer writer) throws IOException;
}
