// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script.resolvers;

import webit.script.io.Out;

/**
 *
 * @author Zqq
 */
public interface OutResolver extends Resolver {

    void render(Out out, Object bean);
}
