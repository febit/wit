// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.io.loader;

import lombok.RequiredArgsConstructor;
import org.febit.wit.io.Source;

import java.nio.charset.Charset;
import java.nio.file.Path;

@RequiredArgsConstructor(staticName = "of")
public class FileSystemLoader implements PathBasedLoader {

    private final Charset charset;
    private final Source.BeginWith beginWith;

    @Override
    public Source get(String path) {
        var p = Path.of(path);
        return new FileSystemSource(p, charset, beginWith);
    }
}
