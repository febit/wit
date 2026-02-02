// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.global.impl;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import org.febit.wit.InternalContext;
import org.febit.wit.exceptions.ScriptRuntimeException;
import org.febit.wit.global.GlobalHeap;
import org.febit.wit.global.GlobalHeapRegister;
import org.jspecify.annotations.Nullable;

@Accessors(fluent = true)
@RequiredArgsConstructor(staticName = "create")
public class LocalContextRegister implements GlobalHeapRegister {

    public static final String DEFAULT_NAME = "$LOCAL";

    @Getter
    private final String name;

    public static LocalContextRegister create() {
        return create(DEFAULT_NAME);
    }

    @Override
    public void register(GlobalHeap heap) {
        heap.setConstMethod(this.name, LocalContextRegister::local);
    }

    @Nullable
    public static Object local(InternalContext context, @Nullable Object @Nullable [] args) {
        final int len = args == null ? 0 : args.length;
        if (args == null || len < 1) {
            throw new ScriptRuntimeException("This function need at least 1 arg: ");
        }
        var key = args[0];
        if (key == null) {
            throw new ScriptRuntimeException("Local var name can't be null");
        }
        if (len == 1) {
            return context.getLocalVar(key);
        }
        context.setLocalVar(key, args[1]);
        return args[1];
    }
}
