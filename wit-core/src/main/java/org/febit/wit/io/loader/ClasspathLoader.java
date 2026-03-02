// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.io.loader;

import lombok.RequiredArgsConstructor;
import org.febit.wit.io.Source;

import java.nio.charset.Charset;

@RequiredArgsConstructor(staticName = "of")
public class ClasspathLoader implements PathBasedLoader {

    private final Charset charset;
    private final Source.BeginWith beginWith;

    @Override
    public Source get(String path) {
        return new ClasspathSource(path, charset, beginWith);
    }

}
