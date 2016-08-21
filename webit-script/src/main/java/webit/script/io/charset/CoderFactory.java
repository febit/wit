// Copyright (c) 2013-2015, Webit Team. All Rights Reserved.
package webit.script.io.charset;

import webit.script.io.Buffers;
import webit.script.util.InternedEncoding;

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
