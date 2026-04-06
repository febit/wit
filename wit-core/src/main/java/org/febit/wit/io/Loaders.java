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
package org.febit.wit.io;

import lombok.Singular;
import lombok.experimental.UtilityClass;
import org.febit.wit.io.Source.BeginWith;
import org.febit.wit.io.loader.AdvancePathLoaderDecorator;
import org.febit.wit.io.loader.ClasspathLoader;
import org.febit.wit.io.loader.DebouncedLoaderDecorator;
import org.febit.wit.io.loader.DispatcherLoader;
import org.febit.wit.io.loader.EmptyLoader;
import org.febit.wit.io.loader.FileSystemLoader;
import org.febit.wit.io.loader.PathBasedLoader;
import org.febit.wit.io.loader.SecurityLoaderDecorator;
import org.febit.wit.io.loader.StringLoader;
import org.febit.wit.util.ClassUtils;
import org.jspecify.annotations.Nullable;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.time.Duration;
import java.util.List;

@UtilityClass
public class Loaders {

    public static EmptyLoader empty() {
        return new EmptyLoader();
    }

    public static DispatcherLoader.Builder dispatcher() {
        return DispatcherLoader.builder();
    }

    public static StringLoader.Builder string() {
        return StringLoader.builder();
    }

    public static SecurityLoaderDecorator.Builder security(Loader delegate) {
        return SecurityLoaderDecorator.builder(delegate);
    }

    public static DebouncedLoaderDecorator debounce(Loader delegate, long delayMillis) {
        return DebouncedLoaderDecorator.of(delegate, delayMillis);
    }

    public static DebouncedLoaderDecorator debounce(Loader delegate, Duration delay) {
        return DebouncedLoaderDecorator.of(delegate, (int) delay.toMillis());
    }

    public static AdvancePathLoaderDecorator.Builder advance(PathBasedLoader delegate) {
        return AdvancePathLoaderDecorator.builder()
                .delegate(delegate);
    }

    @lombok.Builder(
            builderClassName = "ClasspathBuilder",
            builderMethodName = "classpath"
    )
    private static Loader classpath0(
            @Nullable ClassLoader classLoader,
            @Nullable Charset charset,
            @Nullable BeginWith beginWith,
            @Nullable Boolean cacheEnabled,

            @Nullable String root,
            @Nullable String completeMissingSuffix,
            @Singular List<String> candidateSuffixes
    ) {
        if (classLoader == null) {
            classLoader = ClassUtils.loader();
        }
        if (charset == null) {
            charset = StandardCharsets.UTF_8;
        }
        if (beginWith == null) {
            beginWith = Source.BeginWith.SCRIPT;
        }

        var delegate = ClasspathLoader.of(classLoader, charset, beginWith);
        var advance = advance(delegate)
                .root(root)
                .completeMissingSuffix(completeMissingSuffix)
                .candidateSuffixes(candidateSuffixes);
        if (cacheEnabled != null) {
            advance.cacheEnabled(cacheEnabled);
        }
        return advance.build();
    }

    @lombok.Builder(
            builderClassName = "FileSystemBuilder",
            builderMethodName = "fileSystem"
    )
    private static Loader fileSystem0(
            @Nullable FileSystem fileSystem,
            @Nullable Charset charset,
            @Nullable BeginWith beginWith,
            @Nullable Boolean cacheEnabled,
            @Nullable String root,
            @Nullable String completeMissingSuffix,
            @Singular List<String> candidateSuffixes
    ) {
        if (fileSystem == null) {
            fileSystem = FileSystems.getDefault();
        }
        if (charset == null) {
            charset = StandardCharsets.UTF_8;
        }
        if (beginWith == null) {
            beginWith = Source.BeginWith.SCRIPT;
        }
        var delegate = FileSystemLoader.of(fileSystem, charset, beginWith);
        var advance = advance(delegate)
                .root(root)
                .completeMissingSuffix(completeMissingSuffix)
                .candidateSuffixes(candidateSuffixes);
        if (cacheEnabled != null) {
            advance.cacheEnabled(cacheEnabled);
        }
        return advance.build();
    }

}
