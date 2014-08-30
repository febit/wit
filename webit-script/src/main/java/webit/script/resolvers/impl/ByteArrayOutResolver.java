// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.resolvers.impl;

import webit.script.io.Out;
import webit.script.resolvers.OutResolver;

/**
 *
 * @author zqq
 */
public class ByteArrayOutResolver implements OutResolver {

    public void render(Out out, Object bean) {
        out.write((byte[]) bean);
    }

    public Class getMatchClass() {
        return byte[].class;
    }
}
