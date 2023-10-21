// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.resolvers.impl;

import org.febit.wit.io.Out;
import org.febit.wit.resolvers.Serializer;

/**
 * @author zqq90
 */
public class CharArraySerializer implements Serializer<char[]> {

    @Override
    public void render(Out out, char[] bean) {
        out.write(bean);
    }

    @Override
    public Class<char[]> getMatchClass() {
        return char[].class;
    }
}
