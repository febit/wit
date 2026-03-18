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
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import org.febit.wit.io.Source;
import org.jspecify.annotations.Nullable;

import java.io.IOException;
import java.io.Reader;
import java.util.concurrent.atomic.AtomicReference;

@Getter
@Accessors(fluent = true)
@RequiredArgsConstructor
public class DebouncedSource implements Source {

    private final AtomicReference<@Nullable Stamp> stampRef = new AtomicReference<>();

    private final Source delegate;
    private final long delayMillis;

    public long now() {
        return System.currentTimeMillis();
    }

    @Override
    public Reader open() throws IOException {
        // force clear version stamp
        return this.delegate.open();
    }

    @Override
    public long version() {
        var stamp = this.stampRef.get();
        if (stamp == null || stamp.expire <= now()) {
            stamp = new Stamp(this.delegate.version(),
                    now() + this.delayMillis);
            this.stampRef.set(stamp);
        }
        return stamp.version;
    }

    @Override
    public boolean exists() {
        return this.delegate.exists();
    }

    @Override
    public BeginWith beginWith() {
        return this.delegate.beginWith();
    }

    public record Stamp(long version, long expire) {
    }

}
