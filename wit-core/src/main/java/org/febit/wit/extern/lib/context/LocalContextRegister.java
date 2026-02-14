// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.extern.lib.context;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import org.febit.wit.Engine;
import org.febit.wit.EngineModule;
import org.febit.wit.exceptions.ScriptRuntimeException;
import org.febit.wit.runtime.InternalContext;
import org.jspecify.annotations.Nullable;

@Accessors(fluent = true)
@RequiredArgsConstructor(staticName = "create")
public class LocalContextRegister implements EngineModule {

    public static final String DEFAULT_NAME = "$LOCAL";

    @Getter
    private final String name;

    public static LocalContextRegister create() {
        return create(DEFAULT_NAME);
    }

    @Override
    public void apply(Engine engine) {
        var heap = engine.staticHeaps().constant();
        heap.setFunction(this.name, LocalContextRegister::local);
    }

    @Nullable
    public static Object local(InternalContext context, @Nullable Object @Nullable [] args) {
        final int len = args == null ? 0 : args.length;
        if (args == null || len < 1) {
            throw new ScriptRuntimeException("One more arguments expected for local context function");
        }
        var key = args[0];
        if (key == null) {
            throw new ScriptRuntimeException("Key of local context can not be null");
        }
        if (len == 1) {
            return context.local().get(key);
        }
        context.local().set(key, args[1]);
        return args[1];
    }
}
