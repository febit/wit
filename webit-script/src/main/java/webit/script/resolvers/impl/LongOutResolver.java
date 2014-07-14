// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script.resolvers.impl;

import webit.script.io.Out;
import webit.script.resolvers.MatchMode;
import webit.script.resolvers.OutResolver;
import webit.script.util.NumberUtil;

/**
 *
 * @author zqq90
 */
public class LongOutResolver implements OutResolver {

    public void render(final Out out, Object bean) {
        final long i;
        if ((i = (Long) bean) != Long.MIN_VALUE) {
            final char[] buf;
            final int pos = NumberUtil.getChars(i, NumberUtil.SIZE, (buf = NumberUtil.get()));
            out.write(buf, pos, NumberUtil.SIZE - pos);
        } else {
            out.write("-9223372036854775808");
        }
    }

    public MatchMode getMatchMode() {
        return MatchMode.EQUALS;
    }

    public Class<?> getMatchClass() {
        return Long.class;
    }
}
