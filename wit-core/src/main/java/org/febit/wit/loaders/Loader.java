// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.loaders;

import org.febit.wit.runtime.Resource;
import org.jspecify.annotations.Nullable;

public interface Loader {

    /**
     * Get resource by path.
     *
     * @param path resource path
     * @return resource
     */
    Resource get(String path);

    /**
     * Get path by reference and relative path.
     *
     * @param refer path to refence
     * @param path  path to relative
     * @return path
     */
    @Nullable
    String sibling(@Nullable String refer, String path);

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
     * @param path resource path
     * @return true if cache enabled for path, otherwise false
     */
    boolean isCacheEnabled(String path);
}
