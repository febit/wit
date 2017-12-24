// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.io.charset;

import java.io.IOException;
import java.io.Writer;

/**
 *
 * @author zqq90
 */
public interface Decoder {

    void write(byte[] bytes, int off, int len, Writer writer) throws IOException;
}
