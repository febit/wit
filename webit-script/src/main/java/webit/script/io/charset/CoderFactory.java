// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.io.charset;

/**
 *
 * @author Zqq
 */
public interface CoderFactory {

    Encoder newEncoder(String encoding);

    Decoder newDecoder(String encoding);
}
