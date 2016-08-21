// Copyright (c) 2013-2015, Webit Team. All Rights Reserved.
package webit.script.io;

import webit.script.util.InternedEncoding;

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
