// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.io;

import org.febit.wit.util.InternedEncoding;

/**
 *
 * @author zqq90
 */
public interface Out {

    void write(byte[] bytes, int offset, int length);

    void write(byte[] bytes);

    void write(char[] chars, int offset, int length);

    void write(char[] chars);

    void write(String string, int offset, int length);

    void write(String string);

    InternedEncoding getEncoding();

    boolean isByteStream();
}
