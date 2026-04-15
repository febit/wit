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
package org.febit.wit.util;

import org.febit.wit.exception.AmbiguousMethodException;
import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Executable;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import static org.febit.wit.util.NativeMethods.find;
import static org.junit.jupiter.api.Assertions.*;

class NativeMethodsChooseMethodTest {

    static <T extends Executable> T choose(
            List<T> executables, boolean mix, @Nullable Class<?>... args) {
        return NativeMethods.choose(executables,
                mix ? NativeMethods::distanceMix : NativeMethods::distance,
                args, 0);
    }

    @Test
    void arraysFill() throws NoSuchMethodException {
        var methods = find(Arrays.class, "fill").toList();

        assertEquals(
                Arrays.class.getMethod("fill", int[].class, int.class),
                choose(methods, false, int[].class, int.class)
        );
        assertEquals(
                Arrays.class.getMethod("fill", int[].class, int.class),
                choose(methods, true, int[].class, int.class)
        );

        assertEquals(
                Arrays.class.getMethod("fill", long[].class, long.class),
                choose(methods, false, long[].class, long.class)
        );
        assertEquals(
                Arrays.class.getMethod("fill", long[].class, long.class),
                choose(methods, true, long[].class, long.class)
        );

        assertEquals(
                Arrays.class.getMethod("fill", long[].class, int.class, int.class, long.class),
                choose(methods, false, long[].class, int.class)
        );
        assertEquals(
                Arrays.class.getMethod("fill", long[].class, int.class, int.class, long.class),
                choose(methods, true, long[].class, int.class)
        );

        assertEquals(
                Arrays.class.getMethod("fill", Object[].class, int.class, int.class, Object.class),
                choose(methods, false, Object[].class, int.class, int.class, Object.class)
        );
        assertEquals(
                Arrays.class.getMethod("fill", Object[].class, int.class, int.class, Object.class),
                choose(methods, true, Object[].class, int.class, int.class, Object.class)
        );
    }

    @Test
    void multi() throws NoSuchMethodException {
        var methods = find(Foo.class, "multi").toList();

        assertEquals(
                Foo.class.getMethod("multi", Integer.class, Number.class),
                choose(methods, true, Foo.class, Integer.class, Number.class)
        );

        assertEquals(
                Foo.class.getMethod("multi", Integer.class, Object.class),
                choose(methods, true, Foo.class, Integer.class, Object.class)
        );
        assertEquals(
                Foo.class.getMethod("multi", Integer.class, Object.class),
                choose(methods, true, Foo.class, Integer.class, String.class)
        );

        assertEquals(
                Foo.class.getMethod("multi", Integer.class, Number.class),
                choose(methods, true, Foo.class, Integer.class, Integer.class)
        );

        assertEquals(
                Foo.class.getMethod("multi", Foo.class, Number.class, Long.class),
                choose(methods, true, Foo.class, Number.class, Long.class)
        );
    }

    @Test
    void ambiguous() throws NoSuchMethodException {
        var methods = find(Foo.class, "ambiguous").toList();

        assertEquals(
                Foo.class.getMethod("ambiguous", Collection.class),
                choose(methods, true, Collection.class)
        );

        assertEquals(
                Foo.class.getMethod("ambiguous", List.class),
                choose(methods, true, List.class)
        );
        assertEquals(
                Foo.class.getMethod("ambiguous", AbstractList.class),
                choose(methods, true, AbstractList.class)
        );

        assertEquals(
                Foo.class.getMethod("ambiguous", Collection.class),
                choose(methods, true, Set.class)
        );

        assertEquals(
                Foo.class.getMethod("ambiguous", AbstractList.class),
                choose(methods, true, ArrayList.class)
        );

    }

    @Test
    void cannotResolvedAmbiguous() {
        var methods = find(Foo.class, "cannotResolved").toList();

        assertThrows(AmbiguousMethodException.class,
                () -> choose(methods, true, Foo.class)
        );
    }

    @SuppressWarnings("all")
    public static class Foo {

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
