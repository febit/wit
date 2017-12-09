// Copyright (c) 2013-2016, febit.org. All Rights Reserved.
package org.febit.wit.resolvers;

/**
 *
 * @author zqq90
 * @param <T>
 */
public interface GetResolver<T> extends Resolver<T> {

    Object get(T object, Object property);
}
