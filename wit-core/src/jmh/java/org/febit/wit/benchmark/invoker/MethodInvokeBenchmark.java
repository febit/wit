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
package org.febit.wit.benchmark.invoker;

import org.febit.wit.engine.WitFunction;
import org.febit.wit.engine.nativex.support.MethodInvoker;
import org.febit.wit.engine.nativex.support.MethodInvokerUtils;
import org.febit.wit.extern.asm.AsmNativeFunctionFactory;
import org.febit.wit.util.NativeMethods;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;

import java.lang.invoke.MethodHandle;
import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Warmup(iterations = 5, time = 1)
@Measurement(iterations = 5, time = 1)
@Fork(1)
@State(Scope.Benchmark)
public class MethodInvokeBenchmark {

    private Method method;
    private MethodHandle handle;
    private Object[] args;
    private TestClass instance;
    private WitFunction.Constable asm;
    private MethodInvoker invoker;

    @SuppressWarnings({"unused", "SameReturnValue"})
    public static class TestClass {
        public String echo(String value) {
            return value;
        }
    }

    @Setup(Level.Trial)
    public void setup() throws ReflectiveOperationException {
        instance = new TestClass();
        args = new Object[]{instance, "test"};

        method = TestClass.class.getMethod("echo", String.class);
        invoker = MethodInvokerUtils.of(method);
        handle = invoker.handle();
        asm = (WitFunction.Constable) AsmNativeFunctionFactory.create().method(method);
    }

    @Benchmark
    public Object benchmarkMethodInvoke() {
        return NativeMethods.invoke(method, args);
    }

    @Benchmark
    public Object benchmarkHandleExact() throws Throwable {
        return handle.invokeExact(args[0], args[1]);
    }

    @Benchmark
    public Object benchmarkAsm() {
        return asm.apply(args);
    }

    @Benchmark
    public Object benchmarkInvoker() throws Throwable {
        return invoker.invoke(args);
    }
}

