// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.runtime.accessor.impl;

import org.febit.wit.io.Out;
import org.febit.wit.runtime.accessor.Render;

public class ByteArrayRender implements Render<byte[]> {

    @Override
    public void render(Out out, byte[] arr) {
        out.write(arr);
    }
}
