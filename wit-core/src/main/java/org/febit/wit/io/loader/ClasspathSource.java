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

import org.febit.wit.exception.NoSuchSourceException;
import org.febit.wit.io.Source;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;

public record ClasspathSource(
        ClassLoader classLoader,
        String path,
        Charset charset,
        BeginWith beginWith
) implements Source {

    @Override
    public boolean exists() {
        return classLoader.getResource(path) != null;
    }

    @Override
    public Reader open() throws IOException {
        var in = classLoader.getResourceAsStream(path);
        if (in == null) {
            throw new NoSuchSourceException("No such resource: " + path);
        }
        return new InputStreamReader(in, charset);
    }

    @Override
    public long version() {
        return 0L;
    }
}
