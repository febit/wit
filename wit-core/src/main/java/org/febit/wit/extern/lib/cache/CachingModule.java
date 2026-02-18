// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.extern.lib.cache;

import org.febit.wit.Engine;
import org.febit.wit.EngineModule;
import org.febit.wit.exception.ScriptEvaluateException;
import org.febit.wit.runtime.InternalContext;
import org.febit.wit.runtime.Undefined;
import org.febit.wit.runtime.function.FunctionDeclare;
import org.febit.wit.util.ArrayUtils;
import org.jspecify.annotations.Nullable;

import java.io.ByteArrayOutputStream;
import java.io.CharArrayWriter;
import java.io.Serializable;
import java.util.Arrays;

@lombok.Builder(
        builderClassName = "Builder"
)
public class CachingModule implements EngineModule {

    public static final String DEFAULT_NAME = "cache";

    @lombok.Builder.Default
    private final String name = DEFAULT_NAME;
    @lombok.Builder.Default
    private final boolean withClear = false;
    @lombok.Builder.Default
    private final boolean withRemove = true;

    @lombok.NonNull
    @SuppressWarnings("NullableProblems")
    private final Cache<Object, CachingEntry> using;

    @Override
    public void apply(Engine engine) {
        var heap = engine.staticHeaps().constant();
        heap.setFunction(name, this::doPut);
        if (withRemove) {
            heap.setFunction(name + "_remove", this::doRemove);
        }
        if (withClear) {
            heap.setFunction(name + "_clear", this::doClear);
        }
    }

    private Object doClear(InternalContext context, @Nullable Object @Nullable [] args) {
        this.using.clear();
        return Undefined.UNDEFINED;
    }

    private Object doRemove(InternalContext context, @Nullable Object @Nullable [] args) {
        var key = ArrayUtils.get(args, 0);
        this.using.remove(key);
        return Undefined.UNDEFINED;
    }

    @Nullable
    public Object doPut(InternalContext context, @Nullable Object @Nullable [] args) {
        if (args == null || args.length < 1) {
            throw new ScriptEvaluateException("At least one argument is required for cache function:"
                    + " put(key?, factory, ...args?).");
        }
        var arg0 = args[0];
        var arg1 = ArrayUtils.get(args, 1);

        CachingEntry entry;
        if (arg0 instanceof FunctionDeclare func) {
            entry = this.using.computeIfAbsent(arg0,
                    () -> compute(context, func, args, 1)
            );
        } else if (arg1 instanceof FunctionDeclare func) {
            entry = this.using.computeIfAbsent(arg0,
                    () -> compute(context, func, args, 2)
            );
        } else {
            throw new ScriptEvaluateException("Invalid arguments for cache function: put(key?, factory, ...args?)."
                    + " The first or second argument must be a factory function.");
        }
        context.out(entry.rendered);
        return entry.returned;
    }

    protected static CachingEntry compute(
            InternalContext context,
            FunctionDeclare func,
            @Nullable Object[] args,
            int paramsStartedAt
    ) {
        var methodArgs = args.length > paramsStartedAt
                ? Arrays.copyOfRange(args, paramsStartedAt, args.length)
                : ArrayUtils.emptyObjects();

        Object returned;
        Object outed;
        if (context.out().preferBytes()) {
            var buffer = new ByteArrayOutputStream(256);
            returned = context.redirect(buffer, c -> func.apply(c, methodArgs));
            outed = buffer.toByteArray();
        } else {
            var buffer = new CharArrayWriter(256);
            returned = context.redirect(buffer, c -> func.apply(c, methodArgs));
            outed = buffer.toCharArray();
        }
        return new CachingEntry(returned, outed);
    }

    protected record CachingEntry(
            @Nullable Object returned,
            Object rendered
    ) implements Serializable {

    }
}
