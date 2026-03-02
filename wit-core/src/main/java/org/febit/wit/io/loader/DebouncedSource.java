// Copyright (c) 2013-present, febit.org. All Rights Reserved.
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
    public Reader openReader() throws IOException {
        // force clear version stamp
        return this.delegate.openReader();
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
