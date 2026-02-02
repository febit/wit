// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.loaders.impl;

import lombok.RequiredArgsConstructor;
import org.febit.wit.lang.Resource;
import org.febit.wit.loaders.BasicPathLoader;

import java.nio.charset.Charset;
import java.nio.file.Path;

@RequiredArgsConstructor(staticName = "of")
public class FileSystemLoader implements BasicPathLoader {

    private final Charset charset;
    private final boolean codeFirst;

    @Override
    public Resource get(String path) {
        var p = Path.of(path);
        return new FileSystemResource(p, charset, codeFirst);
    }
}
