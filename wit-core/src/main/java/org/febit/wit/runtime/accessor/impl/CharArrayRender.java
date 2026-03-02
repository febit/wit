// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.runtime.accessor.impl;

import org.febit.wit.io.Out;
import org.febit.wit.runtime.accessor.Render;

public class CharArrayRender implements Render<char[]> {

    @Override
    public void render(Out out, char[] arr) {
        out.write(arr);
    }
}
