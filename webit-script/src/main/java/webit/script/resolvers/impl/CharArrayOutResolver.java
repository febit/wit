// Copyright (c) 2013-2015, Webit Team. All Rights Reserved.
package webit.script.resolvers.impl;

import webit.script.io.Out;
import webit.script.resolvers.OutResolver;

/**
 *
 * @author zqq90
 */
public class CharArrayOutResolver implements OutResolver {

    @Override
    public void render(Out out, Object bean) {
        out.write((char[]) bean);
    }

    @Override
    public Class getMatchClass() {
        return char[].class;
    }
}
