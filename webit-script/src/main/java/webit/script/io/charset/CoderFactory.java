// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.io.charset;

import webit.script.util.BufferPeers;

/**
 *
 * @author Zqq
 */
public interface CoderFactory {

    Encoder newEncoder(String encoding);
    Encoder newEncoder(String encoding, BufferPeers bufferPeers);

    Decoder newDecoder(String encoding);
    Decoder newDecoder(String encoding, BufferPeers bufferPeers);
}
