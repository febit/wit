// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.io.charset.impl;

import webit.script.io.charset.CoderFactory;
import webit.script.io.charset.Decoder;
import webit.script.io.charset.Encoder;
import webit.script.io.charset.impl.special.UTF_8_Decoder;
import webit.script.io.charset.impl.special.UTF_8_Encoder;
import webit.script.util.StringPool;

/**
 *
 * @author Zqq
 */
public class DefaultCoderFactory implements CoderFactory {

    public Encoder newEncoder(final String encoding) {
        if (encoding == StringPool.UTF_8 || encoding.equalsIgnoreCase(StringPool.UTF_8)) {
            return new UTF_8_Encoder();
        }
        return new DefaultEncoder(encoding);
    }

    public Decoder newDecoder(final String encoding) {
        if (encoding == StringPool.UTF_8 || encoding.equalsIgnoreCase(StringPool.UTF_8)) {
            return new UTF_8_Decoder();
        }
        return new DefaultDecoder(encoding);
    }
}
