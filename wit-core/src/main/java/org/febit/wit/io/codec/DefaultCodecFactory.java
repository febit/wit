// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.io.codec;

import java.nio.charset.Charset;

public class DefaultCodecFactory implements CodecFactory {

    @Override
    public Encoder encoder(Charset charset) {
        return new DefaultEncoder(charset);
    }

    @Override
    public Decoder decoder(Charset charset) {
        return new DefaultDecoder(charset);
    }
}
