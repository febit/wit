// Copyright (c) 2013-2016, febit.org. All Rights Reserved.
package org.febit.wit.resolvers;

import org.febit.wit.io.Out;

/**
 *
 * @author zqq90
 * @param <T>
 */
public interface OutResolver<T> extends Resolver<T> {

    void render(Out out, T bean);
}
