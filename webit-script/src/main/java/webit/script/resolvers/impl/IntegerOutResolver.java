// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script.resolvers.impl;

import webit.script.io.Out;
import webit.script.resolvers.OutResolver;
import webit.script.resolvers.RegistModeResolver;
import webit.script.resolvers.ResolverManager;
import webit.script.util.NumberUtil;

/**
 *
 * @author zqq90
 */
public class IntegerOutResolver implements OutResolver, RegistModeResolver {

    @Override
    public void render(final Out out, final Object number) {
        NumberUtil.print(((Number) number).intValue(), out);
    }

    @Override
    public Class getMatchClass() {
        return null;
    }

    @Override
    public void regist(ResolverManager resolverManager) {
        resolverManager.registResolver(Integer.class, this);
        resolverManager.registResolver(Short.class, this);
        resolverManager.registResolver(Byte.class, this);
    }
}
