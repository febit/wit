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
package org.febit.wit.benchmark.accessor;

import org.febit.wit.engine.accessor.Getter;
import org.febit.wit.engine.accessor.Setter;
import org.febit.wit.extern.asm.AsmBeanAccessorFactory;
import org.febit.wit.runtime.accessor.ReflectBeanAccessorFactory;
import org.febit.wit.util.bean.PropertyAccessor;
import org.febit.wit.util.bean.PropertyAccessors;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;

import java.util.concurrent.TimeUnit;

@Warmup(time = 1, batchSize = 10)
@Measurement(time = 1, batchSize = 10)
@OutputTimeUnit(TimeUnit.SECONDS)
@State(Scope.Benchmark)
public class BeanAccessorBenchmark {

    private static final String NAME = "name";
    private static final String NAME_VALUE = "new_name";

    private Getter<Foo> reflectGetter;
    private Setter<Foo> reflectSetter;
    private Getter<Foo> asmGetter;
    private Setter<Foo> asmSetter;

    private PropertyAccessor.Getter directReflectGetter;
    private PropertyAccessor.Setter directReflectSetter;

    private Foo bean;

    @Setup(Level.Trial)
    public void setup() {
        bean = new Foo();

        var reflectFactory = ReflectBeanAccessorFactory.get();
        reflectGetter = reflectFactory.getter(Foo.class);
        reflectSetter = reflectFactory.setter(Foo.class);

        var asmFactory = AsmBeanAccessorFactory.get();
        asmGetter = asmFactory.getter(Foo.class);
        asmSetter = asmFactory.setter(Foo.class);

        var accessors = PropertyAccessors.of(Foo.class);
        var propertyAccessor = accessors.get(NAME);
        directReflectGetter = propertyAccessor.getter();
        directReflectSetter = propertyAccessor.setter();
    }

    @Benchmark
    public Object directReflectGetter() {
        return directReflectGetter.get(bean);
    }

    @Benchmark
    public void directReflectSetter() {
        directReflectSetter.set(bean, NAME_VALUE);
    }

    @Benchmark
    public Object reflectGetter() {
        return reflectGetter.get(bean, NAME);
    }

    @Benchmark
    public void reflectSetter() {
        reflectSetter.set(bean, NAME, NAME_VALUE);
    }

    @Benchmark
    public Object asmGetter() {
        return asmGetter.get(bean, NAME);
    }

    @Benchmark
    public void asmSetter() {
        asmSetter.set(bean, NAME, NAME_VALUE);
    }

    @lombok.Getter
    @lombok.Setter
    public static class Foo {
        private String name = "init";

        // More properties，but we only benchmark "name"
        private int value = 123;
        private boolean active = true;
        private double price = 99.99;
        private long timestamp = System.currentTimeMillis();
        private String description = "A sample bean for benchmarking.";
        private String category = "benchmark";
        private String type = "test";
        private String status = "active";
        private String code = "ABC123";
        private String extra = "extra";
    }
}
