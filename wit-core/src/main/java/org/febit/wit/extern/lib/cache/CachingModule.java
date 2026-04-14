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
package org.febit.wit.extern.lib.cache;

import org.febit.wit.Wit;
import org.febit.wit.WitModule;
import org.febit.wit.engine.WitFunction;
import org.febit.wit.exception.ScriptEvaluateException;
import org.febit.wit.runtime.RuntimeContext;
import org.febit.wit.runtime.Undefined;
import org.febit.wit.util.Args;
import org.jspecify.annotations.Nullable;

import java.io.ByteArrayOutputStream;
import java.io.CharArrayWriter;
import java.io.Serializable;
import java.util.Arrays;

@lombok.Builder(
        builderClassName = "Builder"
)
public class CachingModule implements WitModule {

    public static final String DEFAULT_NAME = "cache";

    @lombok.Builder.Default
    private final String name = DEFAULT_NAME;
    @lombok.Builder.Default
    private final boolean withClear = false;
    @lombok.Builder.Default
    private final boolean withRemove = true;

    @lombok.NonNull
    private final Cache<Object, CachingEntry> using;

    @Override
    public void apply(Wit wit) {
        var heap = wit.globals().constants();
        heap.setAsFunction(name, this::doPut);
        if (withRemove) {
            heap.setAsFunction(name + "_remove", this::doRemove);
        }
        if (withClear) {
            heap.setAsFunction(name + "_clear", this::doClear);
        }
    }

    private Object doClear(RuntimeContext context, @Nullable Object @Nullable [] args) {
        this.using.clear();
        return Undefined.UNDEFINED;
    }

    private Object doRemove(RuntimeContext context, @Nullable Object @Nullable [] args) {
        var key = Args.at(args, 0);
        this.using.remove(key);
        return Undefined.UNDEFINED;
    }

    @Nullable
    public Object doPut(RuntimeContext context, @Nullable Object @Nullable [] args) {
        if (args == null || args.length < 1) {
            throw new ScriptEvaluateException("At least one argument is required for cache function:"
                    + " put(key?, factory, ...args?).");
        }
        var arg0 = args[0];
        var arg1 = Args.at(args, 1);

        CachingEntry entry;
        if (arg0 instanceof WitFunction func) {
            entry = this.using.computeIfAbsent(arg0,
                    () -> compute(context, func, args, 1)
            );
        } else if (arg1 instanceof WitFunction func) {
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
            RuntimeContext context,
            WitFunction func,
            @Nullable Object[] args,
            int paramsStartedAt
    ) {
        var methodArgs = args.length > paramsStartedAt
                ? Arrays.copyOfRange(args, paramsStartedAt, args.length)
                : Args.empty();

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
