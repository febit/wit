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
import org.febit.wit.util.PathTrie;
import org.febit.wit.util.PathUtils;

import java.util.Collection;

@Accessors(fluent = true)
public class SecurityLoader implements Loader.Decorator {

    @Getter
    private final Loader delegate;
    private final PathTrie trie;

    public static Builder builder(Loader delegate) {
        return new Builder(delegate);
    }

    private SecurityLoader(Loader delegate, PathTrie trie) {
        this.delegate = delegate;
        this.trie = trie;
    }

    @Override
    public Source get(String path) {
        var normalizedPath = PathUtils.normalize(path);
        if (this.trie.match(normalizedPath)) {
            return this.delegate.get(path);
        }
        return new EmptySource(path, "Access denied.");
    }

    public static class Builder {
        private final PathTrie.Builder trieBuilder = PathTrie.builder('/');

        private final Loader delegate;

        private Builder(Loader delegate) {
            this.delegate = delegate;
        }

        public Builder allow(String path) {
            this.trieBuilder.allow(path);
            return this;
        }

        public Builder allow(String... paths) {
            for (var p : paths) {
                allow(p);
            }
            return this;
        }

        public Builder allow(Collection<String> paths) {
            for (var p : paths) {
                allow(p);
            }
            return this;
        }

        public Builder deny(String path) {
            this.trieBuilder.deny(path);
            return this;
        }

        public Builder deny(String... paths) {
            for (var p : paths) {
                deny(p);
            }
            return this;
        }

        public Builder deny(Collection<String> paths) {
            for (var p : paths) {
                deny(p);
            }
            return this;
        }

        public Builder rule(String path, boolean allowed) {
            this.trieBuilder.rule(path, allowed);
            return this;
        }

        public SecurityLoader build() {
            return new SecurityLoader(this.delegate, this.trieBuilder.build());
        }
    }

}
