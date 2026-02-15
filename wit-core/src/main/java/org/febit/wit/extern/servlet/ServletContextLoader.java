// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.extern.servlet;

import jakarta.servlet.ServletContext;
import lombok.RequiredArgsConstructor;
import org.febit.wit.loaders.BasicPathLoader;
import org.febit.wit.loaders.impl.FileSystemSource;
import org.febit.wit.runtime.Source;

import java.nio.charset.Charset;
import java.nio.file.Path;

@RequiredArgsConstructor(staticName = "of")
public class ServletContextLoader implements BasicPathLoader {

    private final ServletContext servletContext;
    private final Charset charset;
    private final boolean codeFirst;

    @Override
    public Source get(String path) {
        var real = servletContext.getRealPath(path);
        if (real != null) {
            return new FileSystemSource(Path.of(real), charset, codeFirst);
        }
        return new ServletContextSource(path, charset, servletContext, codeFirst);
    }
}
