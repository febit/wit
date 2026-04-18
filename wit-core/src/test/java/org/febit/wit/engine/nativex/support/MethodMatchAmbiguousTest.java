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
package org.febit.wit.engine.nativex.support;

import org.febit.wit.exception.AmbiguousMethodException;
import org.junit.jupiter.api.Test;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import static org.febit.wit.engine.nativex.support.MethodMatchSupport.findBest;
import static org.febit.wit.engine.nativex.support.MethodMatchSupport.findMixedBest;
import static org.febit.wit.util.NativeMethods.find;
import static org.junit.jupiter.api.Assertions.*;

class MethodMatchAmbiguousTest {

    @Test
    void multi() throws NoSuchMethodException {
        var methods = find(Foo.class, "multi").toList();

        assertEquals(
                Foo.class.getMethod("multi", Integer.class, Number.class),
                findMixedBest(methods, Foo.class, Integer.class, Number.class)
        );

        assertEquals(
                Foo.class.getMethod("multi", Integer.class, Object.class),
                findMixedBest(methods, Foo.class, Integer.class, Object.class)
        );
        assertEquals(
                Foo.class.getMethod("multi", Integer.class, Object.class),
                findMixedBest(methods, Foo.class, Integer.class, String.class)
        );

        assertEquals(
                Foo.class.getMethod("multi", Integer.class, Number.class),
                findMixedBest(methods, Foo.class, Integer.class, Integer.class)
        );

        assertEquals(
                Foo.class.getMethod("multi", Foo.class, Number.class, Long.class),
                findMixedBest(methods, Foo.class, Number.class, Long.class)
        );
    }

    @Test
    void ambiguous() throws NoSuchMethodException {
        var methods = find(Foo.class, "ambiguous").toList();

        assertEquals(
                Foo.class.getMethod("ambiguous", Collection.class),
                findMixedBest(methods, Collection.class)
        );

        assertEquals(
                Foo.class.getMethod("ambiguous", List.class),
                findMixedBest(methods, List.class)
        );
        assertEquals(
                Foo.class.getMethod("ambiguous", AbstractList.class),
                findMixedBest(methods, AbstractList.class)
        );

        assertEquals(
                Foo.class.getMethod("ambiguous", Collection.class),
                findMixedBest(methods, Set.class)
        );

        assertEquals(
                Foo.class.getMethod("ambiguous", AbstractList.class),
                findMixedBest(methods, ArrayList.class)
        );

    }

    @Test
    void ctor() throws NoSuchMethodException {
        var ctors = List.of(Foo.class.getConstructors());

        assertEquals(
                Foo.class.getConstructor(Collection.class),
                findBest(ctors, Collection.class)
        );

        assertEquals(
                Foo.class.getConstructor(List.class),
                findBest(ctors, List.class)
        );
        assertEquals(
                Foo.class.getConstructor(AbstractList.class),
                findBest(ctors, AbstractList.class)
        );

        assertEquals(
                Foo.class.getConstructor(Collection.class),
                findBest(ctors, Set.class)
        );

        assertEquals(
                Foo.class.getConstructor(AbstractList.class),
                findBest(ctors, ArrayList.class)
        );
    }

    @Test
    void cannotResolvedAmbiguous() {
        var methods = find(Foo.class, "cannotResolved").toList();
        assertThrows(AmbiguousMethodException.class,
                () -> findMixedBest(methods, Foo.class)
        );

        var ctors = List.of(Foo.class.getConstructors());
        assertThrows(AmbiguousMethodException.class, () ->
                findBest(ctors, Integer.class, Integer.class)
        );
    }

    @SuppressWarnings("all")
    public static class Foo {

        public Foo(Number a, Integer b) {
        }

        public Foo(Integer a, Number b) {
        }

        public Foo(Collection a) {
        }

        public Foo(List a) {
        }

        public Foo(AbstractList a) {
        }

        public static void ambiguous(Collection a) {
        }

        public static void ambiguous(List a) {
        }

        public static void ambiguous(AbstractList a) {
        }

        public static void cannotResolved(Foo self) {
        }

        public void cannotResolved() {
        }

        public void multi(Number a, Number b) {
        }

        public void multi(Integer a, Number b) {
        }

        public void multi(Integer a, Object b) {
        }

        public static void multi(Foo self, Number a, Long b) {
        }
    }

}
