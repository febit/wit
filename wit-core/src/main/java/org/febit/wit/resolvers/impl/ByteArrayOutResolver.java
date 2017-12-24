// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.resolvers.impl;

import org.febit.wit.io.Out;
import org.febit.wit.resolvers.OutResolver;

/**
 *
 * @author zqq90
 */
public class ByteArrayOutResolver implements OutResolver<byte[]> {

    @Override
    public void render(Out out, byte[] bean) {
        out.write(bean);
    }

    @Override
    public Class<byte[]> getMatchClass() {
        return byte[].class;
    }
}
