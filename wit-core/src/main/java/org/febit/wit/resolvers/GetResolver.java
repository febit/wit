// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.resolvers;

/**
 * @param <T>
 * @author zqq90
 */
public interface GetResolver<T> extends Resolver<T> {

    Object get(T object, Object property);
}
