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
import org.febit.wit.ir.expr.DynamicNativeCall;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;

import java.lang.invoke.MethodHandle;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

@Warmup(time = 1, batchSize = 10)
@Measurement(time = 1, batchSize = 10)
@OutputTimeUnit(TimeUnit.SECONDS)
@State(Scope.Benchmark)
public class MethodInvokeBenchmark {

    private TestClass instance;

    private Method echo1;
    private Method echo8;
    private MethodHandle handle1;

    private Object[] args1;
    private Object[] invokeArgs1;
    private Object[] args4;
    private Object[] args8;
    private Object[] invokeArgs8;
    private Object[] args10;

    private WitFunction.Constable asm1;
    private MethodInvoker<?> invoker1;
    private MethodInvoker<?> invoker4;
    private MethodInvoker<?> invoker8;
    private MethodInvoker<?> invoker10;

    @SuppressWarnings({"unused", "SameReturnValue"})
    public static class TestClass {

        public String echo(String value) {
            return value;
        }

        public String echo4(String a1, String a2, String a3, String a4) {
            return a1;
        }

        public String echo8(String a1, String a2, String a3, String a4, String a5,
                            String a6, String a7, String a8) {
            return a1;
        }

        public String echo10(String a1, String a2, String a3, String a4, String a5,
                             String a6, String a7, String a8, String a9, String a10) {
            return a1;
        }
    }

    @Setup(Level.Trial)
    public void setup() throws ReflectiveOperationException {
        instance = new TestClass();
        args1 = new Object[]{instance, "test"};
        args4 = new Object[]{instance, "a1", "a2", "a3", "a4"};
        args8 = new Object[]{instance, "a1", "a2", "a3", "a4", "a5", "a6", "a7", "a8"};
        args10 = new Object[]{instance, "a1", "a2", "a3", "a4", "a5", "a6", "a7", "a8", "a9", "a10"};

        invokeArgs1 = Arrays.copyOfRange(args1, 1, args1.length);
        invokeArgs8 = Arrays.copyOfRange(args8, 1, args8.length);

        echo1 = TestClass.class.getMethod("echo", String.class);
        invoker1 = MethodInvokerUtils.of(echo1);
        handle1 = invoker1.handle();
        asm1 = (WitFunction.Constable) AsmNativeFunctionFactory.create().method(echo1);

        var echo4 = TestClass.class.getMethod("echo4", String.class, String.class, String.class, String.class);
        invoker4 = MethodInvokerUtils.of(echo4);

        echo8 = TestClass.class.getMethod("echo8", String.class, String.class, String.class, String.class, String.class,
                String.class, String.class, String.class);
        invoker8 = MethodInvokerUtils.of(echo8);

        var echo10 = TestClass.class.getMethod("echo10", String.class, String.class, String.class, String.class, String.class,
                String.class, String.class, String.class, String.class, String.class);
        invoker10 = MethodInvokerUtils.of(echo10);
    }

    @Benchmark
    public Object benchmarkMethodInvoke() {
        return DynamicNativeCall.invoke(echo1, instance, invokeArgs1);
    }

    @Benchmark
    public Object benchmarkMethodInvoke8() {
        return DynamicNativeCall.invoke(echo8, instance, invokeArgs8);
    }

    @Benchmark
    public Object benchmarkHandleExact() throws Throwable {
        return handle1.invokeExact(args1[0], args1[1]);
    }

    @Benchmark
    public Object benchmarkAsm() {
        return asm1.apply(args1);
    }

    @Benchmark
    public Object benchmarkInvoker() throws Throwable {
        return invoker1.invoke(args1);
    }

    @Benchmark
    public Object benchmarkInvoker4() throws Throwable {
        return invoker4.invoke(args4);
    }

    @Benchmark
    public Object benchmarkInvoker8() throws Throwable {
        return invoker8.invoke(args8);
    }

    @Benchmark
    public Object benchmarkInvoker10() throws Throwable {
        return invoker10.invoke(args10);
    }
}
