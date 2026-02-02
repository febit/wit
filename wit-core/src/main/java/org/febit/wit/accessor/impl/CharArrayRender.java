// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.accessor.impl;

import org.febit.wit.accessor.Render;
import org.febit.wit.lang.Out;

public class CharArrayRender implements Render<char[]> {

    @Override
    public void render(Out out, char[] arr) {
        out.write(arr);
    }
}
