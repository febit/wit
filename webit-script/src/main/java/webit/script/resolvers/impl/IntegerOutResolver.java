// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.resolvers.impl;

import webit.script.io.Out;
import webit.script.resolvers.MatchMode;
import webit.script.resolvers.OutResolver;
import webit.script.resolvers.RegistModeResolver;
import webit.script.resolvers.ResolverManager;
import webit.script.util.NumberUtil;

/**
 *
 * @author zqq90
 */
public class IntegerOutResolver implements OutResolver, RegistModeResolver {

    public void render(final Out out, final Object bean) {
        final int i = ((Number) bean).intValue();

        if (i != Integer.MIN_VALUE) {
            //int size = (i < 0) ? NumberUtil.stringSize(-i) + 1 : NumberUtil.stringSize(i);
            final char[] buf = (i > 99 || i < -9) ? NumberUtil.get() : new char[2];
            final int pos = NumberUtil.getChars(i, buf.length, buf);
            out.write(buf, pos, buf.length - pos);
        } else {
            out.write("-2147483648");
        }
    }

    public MatchMode getMatchMode() {
        return MatchMode.REGIST;
    }

    public Class<?> getMatchClass() {
        return Integer.class;
    }

    public void regist(ResolverManager resolverManager) {
        resolverManager.registResolver(Integer.class, this, MatchMode.EQUALS);
        resolverManager.registResolver(Short.class, this, MatchMode.EQUALS);
    }
}
