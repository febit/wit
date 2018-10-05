// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.resolvers;

/**
 * @param <T>
 * @author zqq90
 */
public interface SetResolver<T> extends Resolver<T> {

    void set(T object, Object property, Object value);
}
