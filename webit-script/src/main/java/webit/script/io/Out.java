// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.io;

/**
 *
 * @author Zqq
 */
public interface Out {

    public void write(byte[] bytes, int offset, int length);

    public void write(byte[] bytes);

    public void write(char[] chars, int offset, int length);

    public void write(char[] chars);

    public void write(String string, int offset, int length);

    public void write(String string);

    String getEncoding();
    
    boolean isByteStream();
}
