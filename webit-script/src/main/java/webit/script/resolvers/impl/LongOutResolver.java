// Copyright (c) 2013, Webit Team. All Rights Reserved.
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
        final long i = ((Long) bean).longValue();

        if (i != Long.MIN_VALUE) {
            final char[] buf = (i > 99 || i < -9) ? NumberUtil.get() : new char[2];
            final int pos = NumberUtil.getChars(i, buf.length, buf);
            out.write(buf, pos, buf.length - pos);
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
