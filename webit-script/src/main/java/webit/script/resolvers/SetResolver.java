// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script.resolvers;

/**
 *
 * @author Zqq
 */
public interface SetResolver extends Resolver {

    void set(Object object, Object property, Object value);
}
