// Copyright (c) 2013-2016, febit.org. All Rights Reserved.
package org.febit.wit.resolvers.impl;

import org.febit.wit.io.Out;
import org.febit.wit.resolvers.OutResolver;
import org.febit.wit.util.NumberUtil;

/**
 *
 * @author zqq90
 */
public class LongOutResolver implements OutResolver {

    @Override
    public void render(final Out out, Object number) {
        NumberUtil.print((Long) number, out);
    }

    @Override
    public Class getMatchClass() {
        return Long.class;
    }
}
