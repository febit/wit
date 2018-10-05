// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.resolvers;

/**
 * @param <T>
 * @author zqq90
 */
public interface Resolver<T> {

    Class<T> getMatchClass();

    default void register(ResolverManager resolverManager) {
        resolverManager.registerResolver(getMatchClass(), this);
    }
}
