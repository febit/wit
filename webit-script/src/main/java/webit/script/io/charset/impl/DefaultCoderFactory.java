// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script.io.charset.impl;

import webit.script.io.charset.CoderFactory;
import webit.script.io.charset.Decoder;
import webit.script.io.charset.Encoder;
import webit.script.io.charset.impl.special.UTF_8_Decoder;
import webit.script.io.charset.impl.special.UTF_8_Encoder;
import webit.script.io.Buffers;
import webit.script.util.EncodingPool;

/**
 *
 * @author Zqq
 */
public class DefaultCoderFactory implements CoderFactory {

    public Encoder newEncoder(final String encoding) {
        return this.newEncoder(encoding, Buffers.getNormalPeers());
    }

    public Decoder newDecoder(final String encoding) {
        return this.newDecoder(encoding, Buffers.getMiniPeers());
    }

    public Encoder newEncoder(String encoding, Buffers buffers) {
        if (encoding == EncodingPool.UTF_8) {
            return new UTF_8_Encoder(buffers);
        } else {
            return new DefaultEncoder(encoding, buffers);
        }
    }

    public Decoder newDecoder(String encoding, Buffers buffers) {
        if (encoding == EncodingPool.UTF_8) {
            return new UTF_8_Decoder(buffers);
        } else {
            return new DefaultDecoder(encoding, buffers);
        }
    }
}
