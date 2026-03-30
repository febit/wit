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
import lombok.experimental.Accessors;
import org.febit.wit.io.Loader;
import org.febit.wit.io.Source;
import org.febit.wit.util.PathUtils;
import org.jspecify.annotations.Nullable;

import java.util.List;

@lombok.Builder(
        builderClassName = "Builder"
)
@Accessors(fluent = true)
public class AdvancePathLoaderDecorator implements Loader {

    @Getter
    private final PathBasedLoader delegate;

    /**
     * Whether to enable caching.
     */
    @Getter
    private final boolean cacheEnabled;

    /**
     * The root path prefix.
     * <p>
     * Nullable means no root prefix.
     */
    @Nullable
    @Getter
    private final String root;

    /**
     * The suffix to complete if is missing.
     * <p>
     * Nullable means no suffix to complete.
     *
     * @see #candidateSuffixes
     */
    @Nullable
    @Getter
    private final String completeMissingSuffix;

    /**
     * The candidate suffixes to check if the path is missing the suffix.
     * <p>
     * if the path ends with any of these suffixes,
     * no missing suffix will be completed.
     *
     * @see #completeMissingSuffix
     */
    @Nullable
    private final List<String> candidateSuffixes;

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        /**
         * The root path prefix.
         * <p>
         * Nullable means no root prefix.
         */
        public Builder root(@Nullable String root) {
            var normalized = PathUtils.normalize(root, false);
            this.root = normalized == null || normalized.isEmpty()
                    ? null : normalized;
            return this;
        }
    }

    private String mappingPath(final String path) {
        return this.root != null
                ? this.root.concat(path)
                : path.substring(1);
    }

    @Override
    public Source get(String path) {
        var mapped = mappingPath(path);
        return this.delegate.get(mapped);
    }

    @Override
    public boolean isCacheEnabled(String path) {
        return cacheEnabled;
    }

    /**
     * Get path by reference and relative path.
     *
     * <pre>
     * example:
     * /path/to/tmpl1.wit , tmpl2.wit =&gt; /path/to/tmpl2.wit
     * /path/to/tmpl1.wit , /tmpl2.wit =&gt; /tmpl2.wit
     * /path/to/tmpl1.wit , ./tmpl2.wit =&gt; /path/to/tmpl2.wit
     * /path/to/tmpl1.wit , ../tmpl2.wit =&gt; /path/tmpl2.wit
     * </pre>
     *
     * @param refer    path to refence
     * @param relative path to relative
     * @return path
     */
    @Nullable
    @Override
    public String sibling(@Nullable final String refer, final String relative) {
        return PathUtils.sibling(refer, relative);
    }

    /**
     * Normalize path.
     *
     * <pre>
     * example:
     * path/to/tmpl.wit  /path/to/tmpl.wit
     * /path/to/./tmpl.wit  /path/to/tmpl.wit
     * /path/to/../tmpl.wit  /path/tmpl.wit
     * \path\to\..\tmpl.wit  /path/tmpl.wit
     * \path\to\..\..\tmpl.wit  /tmpl.wit
     * \path\to\..\..\..\tmpl.wit  null
     * </pre>
     *
     * @param path path to normalize
     * @return normalized path
     */
    @Nullable
    @Override
    public String normalize(@Nullable String path) {
        if (path == null) {
            return null;
        }
        if (path.isEmpty()) {
            return "/";
        }
        if (path.charAt(0) != '/' && path.charAt(0) != '\\') {
            path = "/" + path;
        }

        var normalized = PathUtils.normalize(path);
        if (normalized == null) {
            return null;
        }

        if (this.completeMissingSuffix == null
                || normalized.endsWith(this.completeMissingSuffix)
                || normalized.charAt(normalized.length() - 1) == '/') {
            return normalized;
        }

        if (this.candidateSuffixes != null) {
            for (var deputy : this.candidateSuffixes) {
                if (normalized.endsWith(deputy)) {
                    return normalized;
                }
            }
        }
        return normalized.concat(this.completeMissingSuffix);
    }
}
