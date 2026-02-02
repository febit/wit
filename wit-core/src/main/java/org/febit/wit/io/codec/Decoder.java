// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.io.codec;

import java.io.IOException;
import java.io.Writer;

public interface Decoder {

    void write(byte[] bytes, int off, int len, Writer writer) throws IOException;
}
