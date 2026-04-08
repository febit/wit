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

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import org.febit.wit.io.Loader;
import org.febit.wit.io.Loaders;
import org.febit.wit.io.Source;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static org.febit.wit.util.Defaults.nvl;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class DispatchLoader implements Loader {

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

        public DispatchLoader build() {
            var sorted = new ArrayList<>(rules);
            sorted.sort(RULE_PREFIX_DESC);

            return new DispatchLoader(
                    nvl(this.fallback, Loaders::empty),
                    List.copyOf(sorted)
            );
        }
    }

    private DispatchLoader.@Nullable Rule lookup(@Nullable String path) {
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
    public String sibling(@Nullable String refer, String relative) {
        var rule = lookup(relative);
        if (rule != null) {
            return rule.normalize(relative);
        }
        if (refer != null) {
            var referRule = lookup(refer);
            if (referRule != null) {
                return referRule.sibling(refer, relative);
            }
        }
        return this.fallback.sibling(refer, relative);
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
