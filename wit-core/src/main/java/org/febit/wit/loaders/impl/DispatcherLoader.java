// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.loaders.impl;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import org.febit.wit.loaders.Loader;
import org.febit.wit.loaders.Loaders;
import org.febit.wit.runtime.Source;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static org.febit.wit.util.Defaults.nvl;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class DispatcherLoader implements Loader {

    private static final Comparator<Rule> RULE_PREFIX_DESC = Comparator.comparing(Rule::prefix).reversed();

    private final Loader fallback;
    private final List<Rule> rules;

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private final List<Rule> rules = new ArrayList<>();

        @Nullable
        private Loader fallback;

        public Builder fallback(@Nullable Loader fallback) {
            this.fallback = fallback;
            return this;
        }

        public Builder rule(String prefix, Loader loader) {
            this.rules.add(new Rule(prefix, loader));
            return this;
        }

        public DispatcherLoader build() {
            var sorted = new ArrayList<>(rules);
            sorted.sort(RULE_PREFIX_DESC);

            return new DispatcherLoader(
                    nvl(this.fallback, Loaders::noop),
                    List.copyOf(sorted)
            );
        }
    }

    private DispatcherLoader.@Nullable Rule lookup(@Nullable String path) {
        if (path == null) {
            return null;
        }
        for (var rule : this.rules) {
            if (path.startsWith(rule.prefix())) {
                return rule;
            }
        }
        return null;
    }

    @Override
    public Source get(String path) {
        var rule = lookup(path);
        if (rule != null) {
            return rule.get(path);
        }
        return this.fallback.get(path);
    }

    @Nullable
    @Override
    public String sibling(@Nullable String refer, String path) {
        var rule = lookup(path);
        if (rule != null) {
            return rule.normalize(path);
        }
        if (refer != null) {
            var referRule = lookup(refer);
            if (referRule != null) {
                return referRule.sibling(refer, path);
            }
        }
        return this.fallback.sibling(refer, path);
    }

    @Nullable
    @Override
    public String normalize(@Nullable String path) {
        var rule = lookup(path);
        if (rule != null) {
            return rule.normalize(path);
        }
        return this.fallback.normalize(path);
    }

    @Override
    public boolean isCacheEnabled(String path) {
        var rule = lookup(path);
        if (rule != null) {
            return rule.isCacheEnabled(path);
        }
        return this.fallback.isCacheEnabled(path);
    }

    @Getter
    @Accessors(fluent = true)
    private static class Rule {

        private final String prefix;
        private final Loader loader;
        private final int prefixLength;

        public Rule(String prefix, Loader loader) {
            this.prefix = prefix;
            this.loader = loader;
            this.prefixLength = prefix.length();
        }

        public Source get(String path) {
            return this.loader.get(path.substring(this.prefixLength));
        }

        @Nullable
        public String sibling(String refer, String subpath) {
            return fix(this.loader.sibling(refer.substring(this.prefixLength), subpath));
        }

        @Nullable
        public String normalize(String path) {
            return fix(this.loader.normalize(path.substring(this.prefixLength)));
        }

        @Nullable
        private String fix(@Nullable final String path) {
            if (path == null) {
                return null;
            }
            return this.prefix.concat(path);
        }

        public boolean isCacheEnabled(String path) {
            return this.loader.isCacheEnabled(path.substring(this.prefixLength));
        }
    }
}
