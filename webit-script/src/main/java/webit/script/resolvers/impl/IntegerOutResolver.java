// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
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

    public void render(final Out out, final Object number) {
        NumberUtil.print((Integer) number, out);
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
