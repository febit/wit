// Copyright (c) 2013, Webit Team. All Rights Reserved.

package webit.script.io;


/**
 *
 * @author Zqq
 */
public interface Out {
    void write(byte[] bytes);
    void write(String string);
    String getEncoding();
}
