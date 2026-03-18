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

import org.jspecify.annotations.Nullable;

public interface Loader {

    /**
     * Get source by path.
     *
     * @param path source path
     * @return source
     */
    Source get(String path);

    /**
     * Get path by reference and relative path.
     *
     * @param refer path to refence
     * @param relative  path to relative
     * @return path
     */
    @Nullable
    String sibling(@Nullable String refer, String relative);

    /**
     * Normalize path.
     *
     * @param path path to normalize
     * @return normalized path
     */
    @Nullable
    String normalize(@Nullable String path);

    /**
     * Whether cache enabled for path.
     *
     * @param path source path
     * @return true if cache enabled for path, otherwise false
     */
    boolean isCacheEnabled(String path);

    interface Decorator extends Loader {

        Loader delegate();

        @Override
        default Source get(String path) {
            return delegate().get(path);
        }

        @Nullable
        @Override
        default String sibling(@Nullable String refer, String relative) {
            return delegate().sibling(refer, relative);
        }

        @Nullable
        @Override
        default String normalize(@Nullable String path) {
            return delegate().normalize(path);
        }

        @Override
        default boolean isCacheEnabled(String path) {
            return delegate().isCacheEnabled(path);
        }
    }
}
