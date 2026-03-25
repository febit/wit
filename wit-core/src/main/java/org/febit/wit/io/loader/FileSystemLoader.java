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

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import org.febit.wit.io.Source;

import java.nio.charset.Charset;
import java.nio.file.FileSystem;

@Accessors(fluent = true)
@RequiredArgsConstructor(staticName = "of")
public class FileSystemLoader implements PathBasedLoader {

    @Getter
    private final FileSystem fileSystem;
    @Getter
    private final Charset charset;
    @Getter
    private final Source.BeginWith beginWith;

    @Override
    public Source get(String path) {
        var p = fileSystem.getPath(path);
        return new FileSystemSource(p, charset, beginWith);
    }
}
