// Copyright (c) 2013-2016, febit.org. All Rights Reserved.
package org.febit.wit.resolvers.impl;

import org.febit.wit.io.Out;
import org.febit.wit.resolvers.OutResolver;

/**
 *
 * @author zqq90
 */
public class ByteArrayOutResolver implements OutResolver {

    @Override
    public void render(Out out, Object bean) {
        out.write((byte[]) bean);
    }

    @Override
    public Class getMatchClass() {
        return byte[].class;
    }
}
