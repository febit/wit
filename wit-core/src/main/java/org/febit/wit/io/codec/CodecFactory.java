// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.io.codec;

import java.nio.charset.Charset;

public interface CodecFactory {

    Encoder encoder(Charset charset);

    Decoder decoder(Charset charset);
}
