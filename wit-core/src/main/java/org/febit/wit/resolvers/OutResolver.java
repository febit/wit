// Copyright (c) 2013-2016, febit.org. All Rights Reserved.
package org.febit.wit.resolvers;

import org.febit.wit.io.Out;

/**
 *
 * @author zqq90
 */
public interface OutResolver extends Resolver {

    void render(Out out, Object bean);
}
