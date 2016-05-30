// Copyright (c) 2013-2015, Webit Team. All Rights Reserved.
package webit.script.resolvers;

/**
 *
 * @author zqq90
 */
public interface SetResolver extends Resolver {

    void set(Object object, Object property, Object value);
}
