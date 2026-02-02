// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.loaders.impl;

import org.febit.wit.lang.Resource;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public record FileSystemResource(
        Path path,
        Charset charset,
        boolean codeFirst
) implements Resource {

    @Override
    public long version() {
        try {
            return Files.getLastModifiedTime(path).toMillis();
        } catch (FileNotFoundException e) {
            return -1;
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public boolean exists() {
        return Files.exists(path);
    }

    @Override
    public Reader openReader() throws IOException {
        return new InputStreamReader(
                Files.newInputStream(path, StandardOpenOption.READ),
                charset
        );
    }
}
