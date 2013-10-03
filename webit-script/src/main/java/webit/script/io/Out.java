// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.io;

/**
 *
 * @author Zqq
 */
public interface Out {

    void write(byte[] bytes, int offset, int length);

    void write(byte[] bytes);

    void write(char[] chars, int offset, int length);

    void write(char[] chars);

    void write(String string, int offset, int length);

    void write(String string);

    String getEncoding();
    
    boolean isByteStream();
}
