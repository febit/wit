package org.febit.wit.loader.impl;

import org.febit.wit.loader.BasicPathLoader;
import org.febit.wit.loader.Loader;
import org.febit.wit.runtime.Source;
import org.febit.wit.util.PathUtils;
import org.jspecify.annotations.Nullable;

import java.util.List;

@lombok.Builder(
        builderClassName = "Builder"
)
public class AdvancePathLoaderDecorator implements Loader {

    private final BasicPathLoader delegate;

    /**
     * Whether to enable caching.
     */
    private final boolean cacheEnabled;

    /**
     * The root path prefix.
     * <p>
     * Nullable means no root prefix.
     */
    @Nullable
    private final String root;

    /**
     * The suffix to append when missing.
     * <p>
     * Nullable means no suffix appending.
     *
     * @see #deputySuffixes
     */
    @Nullable
    private final String missingSuffix;

    /**
     * The deputy suffixes that are also acceptable.
     * <p>
     * if the path ends with any of these suffixes,
     * no missing suffix will be appended.
     *
     * @see #missingSuffix
     */
    @Nullable
    private final List<String> deputySuffixes;

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
            var normalized = PathUtils.normalize(root);
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
     * @param refer path to refence
     * @param relative  path to relative
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

        if (this.missingSuffix == null
                || normalized.endsWith(this.missingSuffix)
                || normalized.charAt(normalized.length() - 1) == '/') {
            return normalized;
        }

        if (this.deputySuffixes != null) {
            for (var deputy : this.deputySuffixes) {
                if (normalized.endsWith(deputy)) {
                    return normalized;
                }
            }
        }
        return normalized.concat(this.missingSuffix);
    }
}
