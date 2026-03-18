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

import org.febit.wit.io.Loader;
import org.febit.wit.io.Source;
import org.jspecify.annotations.Nullable;

/**
 * A loader that without any sources.
 */
public class EmptyLoader implements Loader {

    @Override
    public Source get(String path) {
        return new EmptySource(path, "No such source: " + path);
    }

    @Override
    public @Nullable String sibling(@Nullable String refer, String relative) {
        return relative;
    }

    @Override
    public @Nullable String normalize(@Nullable String path) {
        return path;
    }

    @Override
    public boolean isCacheEnabled(String path) {
        return false;
    }
}
