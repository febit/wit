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
package org.febit.wit.extern.servlet;

import jakarta.servlet.ServletContext;
import org.febit.wit.io.Source;
import org.febit.wit.io.loader.FileSystemSource;
import org.febit.wit.io.loader.PathBasedLoader;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

@lombok.Builder(
        builderClassName = "Builder"
)
public class ServletContextLoader implements PathBasedLoader {

    @lombok.NonNull
    private final ServletContext context;

    @lombok.Builder.Default
    private final Charset charset = StandardCharsets.UTF_8;
    @lombok.Builder.Default
    private final Source.BeginWith beginWith = Source.BeginWith.SCRIPT;

    @Override
    public Source get(String path) {
        var real = context.getRealPath(path);
        if (real != null) {
            return new FileSystemSource(Path.of(real), charset, beginWith);
        }
        return new ServletContextSource(path, charset, context, beginWith);
    }
}
