// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.loaders.impl;

import lombok.RequiredArgsConstructor;
import org.febit.wit.loaders.BasicPathLoader;
import org.febit.wit.runtime.Source;

import java.nio.charset.Charset;

@RequiredArgsConstructor(staticName = "of")
public class ClasspathLoader implements BasicPathLoader {

    private final Charset charset;
    private final boolean codeFirst;

    @Override
    public Source get(String path) {
        return new ClasspathSource(path, charset, codeFirst);
    }

}
