// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.io.charset.impl;

import org.febit.wit.io.Buffers;
import org.febit.wit.io.charset.CoderFactory;
import org.febit.wit.io.charset.Decoder;
import org.febit.wit.io.charset.Encoder;
import org.febit.wit.util.InternedEncoding;

/**
 *
 * @author zqq90
 */
public class DefaultCoderFactory implements CoderFactory {

    @Override
    public Encoder newEncoder(final InternedEncoding encoding) {
        return this.newEncoder(encoding, Buffers.getNormalPeers());
    }

    @Override
    public Decoder newDecoder(final InternedEncoding encoding) {
        return this.newDecoder(encoding, Buffers.getMiniPeers());
    }

    @Override
    public Encoder newEncoder(InternedEncoding encoding, Buffers buffers) {
        if (encoding == InternedEncoding.UTF_8) {
            return new UTF8Encoder(buffers);
        } else {
            return new DefaultEncoder(encoding.value, buffers);
        }
    }

    @Override
    public Decoder newDecoder(InternedEncoding encoding, Buffers buffers) {
        if (encoding == InternedEncoding.UTF_8) {
            return new UTF8Decoder(buffers);
        } else {
            return new DefaultDecoder(encoding.value, buffers);
        }
    }
}
