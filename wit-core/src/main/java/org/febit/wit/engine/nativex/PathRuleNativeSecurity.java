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
package org.febit.wit.engine.nativex;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.febit.wit.util.PathTrie;

import java.util.Collection;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class PathRuleNativeSecurity implements NativeSecurity {

    static final String ROOT_PATH = "*";

    private final PathTrie trie;

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public boolean allowed(String path) {
        if (ROOT_PATH.equals(path)) {
            return this.trie.match("");
        }
        return this.trie.match(path);
    }

    public static class Builder {
        private final PathTrie.Builder trieBuilder = PathTrie.builder('.');

        public Builder allowRoot() {
            return rule(ROOT_PATH, true);
        }

        public Builder denyRoot() {
            return rule(ROOT_PATH, false);
        }

        public Builder allow(String path) {
            return rule(path, true);
        }

        public Builder allow(String... paths) {
            for (var p : paths) {
                rule(p, true);
            }
            return this;
        }

        public Builder allow(Collection<String> paths) {
            for (var p : paths) {
                rule(p, true);
            }
            return this;
        }

        public Builder deny(String path) {
            return rule(path, false);
        }

        public Builder deny(String... paths) {
            for (var p : paths) {
                rule(p, false);
            }
            return this;
        }

        public Builder deny(Collection<String> paths) {
            for (var p : paths) {
                rule(p, false);
            }
            return this;
        }

        public Builder rule(String path, boolean allowed) {
            this.trieBuilder.rule(ROOT_PATH.equals(path) ? "" : path, allowed);
            return this;
        }

        public PathRuleNativeSecurity build() {
            return new PathRuleNativeSecurity(trieBuilder.build());
        }
    }

}
