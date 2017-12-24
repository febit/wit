// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.resolvers;

/**
 *
 * @author zqq90
 * @param <T>
 */
public interface SetResolver<T> extends Resolver<T> {

    void set(T object, Object property, Object value);
}
