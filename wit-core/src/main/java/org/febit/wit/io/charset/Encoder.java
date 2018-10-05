// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.io.charset;

import java.io.IOException;
import java.io.OutputStream;

/**
 * @author zqq90
 */
public interface Encoder {

    void write(char[] chars, int offset, int length, OutputStream out) throws IOException;

    void write(String string, int offset, int length, OutputStream out) throws IOException;
}
