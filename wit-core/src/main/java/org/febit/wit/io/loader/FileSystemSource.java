/*
 * Copyright 2013-present febit.org (support@febit.org)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.febit.wit.io.loader;

import org.febit.wit.io.Source;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public record FileSystemSource(
        Path path,
        Charset charset,
        BeginWith beginWith
) implements Source {

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
    public Reader open() throws IOException {
        return new InputStreamReader(
                Files.newInputStream(path, StandardOpenOption.READ),
                charset
        );
    }
}
