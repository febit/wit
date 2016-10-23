// Copyright (c) 2013-2016, febit.org. All Rights Reserved.
package org.febit.wit.resolvers.impl;

import org.febit.wit.io.Out;
import org.febit.wit.resolvers.OutResolver;
import org.febit.wit.resolvers.RegistModeResolver;
import org.febit.wit.resolvers.ResolverManager;
import org.febit.wit.util.NumberUtil;

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
