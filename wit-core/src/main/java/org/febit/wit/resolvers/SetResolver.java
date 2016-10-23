// Copyright (c) 2013-2016, febit.org. All Rights Reserved.
package org.febit.wit.resolvers;

/**
 *
 * @author zqq90
 */
public interface SetResolver extends Resolver {

    void set(Object object, Object property, Object value);
}
