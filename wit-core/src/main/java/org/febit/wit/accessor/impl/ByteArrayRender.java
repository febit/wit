// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.accessor.impl;

import org.febit.wit.accessor.Render;
import org.febit.wit.lang.Out;

public class ByteArrayRender implements Render<byte[]> {

    @Override
    public void render(Out out, byte[] arr) {
        out.write(arr);
    }
}
