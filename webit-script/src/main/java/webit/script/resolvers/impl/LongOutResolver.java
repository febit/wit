// Copyright (c) 2013-2015, Webit Team. All Rights Reserved.
package webit.script.resolvers.impl;

import webit.script.io.Out;
import webit.script.resolvers.OutResolver;
import webit.script.util.NumberUtil;

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
