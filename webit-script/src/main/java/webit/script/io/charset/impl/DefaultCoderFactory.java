// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.io.charset.impl;

import webit.script.io.charset.CoderFactory;
import webit.script.io.charset.Decoder;
import webit.script.io.charset.Encoder;
import webit.script.io.charset.impl.special.UTF_8_Decoder;
import webit.script.io.charset.impl.special.UTF_8_Encoder;
import webit.script.util.EncodingPool;

/**
 *
 * @author Zqq
 */
public class DefaultCoderFactory implements CoderFactory {

    public Encoder newEncoder(final String encoding) {
        if (encoding == EncodingPool.UTF_8) {
            return new UTF_8_Encoder();
        } else {
            return new DefaultEncoder(encoding);
        }
    }

    public Decoder newDecoder(final String encoding) {
        if (encoding == EncodingPool.UTF_8) {
            return new UTF_8_Decoder();
        } else {
            return new DefaultDecoder(encoding);
        }
    }
}
