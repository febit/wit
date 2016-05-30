// Copyright (c) 2013-2015, Webit Team. All Rights Reserved.
package webit.script.io.charset;

import java.io.IOException;
import java.io.Writer;

/**
 *
 * @author zqq90
 */
public interface Decoder {

    void write(byte[] bytes, int off, int len, Writer writer) throws IOException;
}
