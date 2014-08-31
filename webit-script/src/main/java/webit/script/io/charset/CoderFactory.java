// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script.io.charset;

import webit.script.io.Buffers;

/**
 *
 * @author Zqq
 */
public interface CoderFactory {

    Encoder newEncoder(String encoding);
    Encoder newEncoder(String encoding, Buffers buffers);

    Decoder newDecoder(String encoding);
    Decoder newDecoder(String encoding, Buffers buffers);
}
