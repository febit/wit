// Copyright (c) 2013-2016, febit.org. All Rights Reserved.
package org.febit.wit.io.charset;

import org.febit.wit.io.Buffers;
import org.febit.wit.util.InternedEncoding;

/**
 *
 * @author zqq90
 */
public interface CoderFactory {

    Encoder newEncoder(InternedEncoding encoding);
    Encoder newEncoder(InternedEncoding encoding, Buffers buffers);

    Decoder newDecoder(InternedEncoding encoding);
    Decoder newDecoder(InternedEncoding encoding, Buffers buffers);
}
