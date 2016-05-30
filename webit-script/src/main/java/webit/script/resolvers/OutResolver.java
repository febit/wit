// Copyright (c) 2013-2015, Webit Team. All Rights Reserved.
package webit.script.resolvers;

import webit.script.io.Out;

/**
 *
 * @author zqq90
 */
public interface OutResolver extends Resolver {

    void render(Out out, Object bean);
}
