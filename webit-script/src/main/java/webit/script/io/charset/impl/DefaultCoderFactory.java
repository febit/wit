// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.io.charset.impl;

import webit.script.io.charset.CoderFactory;
import webit.script.io.charset.Decoder;
import webit.script.io.charset.Encoder;
import webit.script.io.charset.impl.special.UTF_8_Decoder;
import webit.script.io.charset.impl.special.UTF_8_Encoder;

/**
 *
 * @author Zqq
 */
public class DefaultCoderFactory implements CoderFactory {

    public Encoder newEncoder(String encoding) {
        if (encoding.equalsIgnoreCase("UTF-8")) {
            return new UTF_8_Encoder();
        }
        return new DefaultEncoder(encoding);
    }

    public Decoder newDecoder(String encoding) {
        if (encoding.equalsIgnoreCase("UTF-8")) {
            return new UTF_8_Decoder();
        }
        return new DefaultDecoder(encoding);
    }
}
