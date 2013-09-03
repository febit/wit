// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.io.charset;

/**
 *
 * @author Zqq
 */
public interface CoderFactory {

    public Encoder newEncoder(String encoding);

    public Decoder newDecoder(String encoding);
}
