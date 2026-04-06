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
import org.junit.jupiter.api.Test;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import static org.febit.wit.util.NativeMethods.chooseMethod;
import static org.febit.wit.util.NativeMethods.find;
import static org.junit.jupiter.api.Assertions.*;

class NativeMethodsChooseMethodTest {

    @Test
    void arraysFill() throws NoSuchMethodException {
        var methods = find(Arrays.class, "fill")
                .toList();

        assertEquals(
                Arrays.class.getMethod("fill", int[].class, int.class),
                chooseMethod(methods, new Class<?>[]{int[].class, int.class})
        );
        assertEquals(
                Arrays.class.getMethod("fill", int[].class, int.class),
                chooseMethod(methods, new Class<?>[]{int[].class, int.class}, true)
        );

        assertEquals(
                Arrays.class.getMethod("fill", long[].class, long.class),
                chooseMethod(methods, new Class<?>[]{long[].class, long.class})
        );
        assertEquals(
                Arrays.class.getMethod("fill", long[].class, long.class),
                chooseMethod(methods, new Class<?>[]{long[].class, long.class}, true)
        );

        assertEquals(
                Arrays.class.getMethod("fill", long[].class, int.class, int.class, long.class),
                chooseMethod(methods, new Class<?>[]{long[].class, int.class})
        );
        assertEquals(
                Arrays.class.getMethod("fill", long[].class, int.class, int.class, long.class),
                chooseMethod(methods, new Class<?>[]{long[].class, int.class}, true)
        );

        assertEquals(
                Arrays.class.getMethod("fill", Object[].class, int.class, int.class, Object.class),
                chooseMethod(methods, new Class<?>[]{Object[].class, int.class, int.class, Object.class})
        );
        assertEquals(
                Arrays.class.getMethod("fill", Object[].class, int.class, int.class, Object.class),
                chooseMethod(methods, new Class<?>[]{Object[].class, int.class, int.class, Object.class}, true)
        );
    }

    @Test
    void multi() throws NoSuchMethodException {
        var methods = find(Foo.class, "multi")
                .toList();

        assertEquals(
                Foo.class.getMethod("multi", Integer.class, Number.class),
                chooseMethod(methods, new Class<?>[]{Foo.class, Integer.class, Number.class}, true)
        );

        assertEquals(
                Foo.class.getMethod("multi", Integer.class, Object.class),
                chooseMethod(methods, new Class<?>[]{Foo.class, Integer.class, Object.class}, true)
        );
        assertEquals(
                Foo.class.getMethod("multi", Integer.class, Object.class),
                chooseMethod(methods, new Class<?>[]{Foo.class, Integer.class, String.class}, true)
        );

        assertEquals(
                Foo.class.getMethod("multi", Integer.class, Number.class),
                chooseMethod(methods, new Class<?>[]{Foo.class, Integer.class, Integer.class}, true)
        );

        assertEquals(
                Foo.class.getMethod("multi", Foo.class, Number.class, Long.class),
                chooseMethod(methods, new Class<?>[]{Foo.class, Number.class, Long.class}, true)
        );
    }

    @Test
    void ambiguous() throws NoSuchMethodException {
        var methods = find(Foo.class, "ambiguous")
                .toList();

        assertEquals(
                Foo.class.getMethod("ambiguous", Collection.class),
                chooseMethod(methods, new Class<?>[]{Collection.class}, true)
        );

        assertEquals(
                Foo.class.getMethod("ambiguous", List.class),
                chooseMethod(methods, new Class<?>[]{List.class}, true)
        );
        assertEquals(
                Foo.class.getMethod("ambiguous", AbstractList.class),
                chooseMethod(methods, new Class<?>[]{AbstractList.class}, true)
        );

        assertEquals(
                Foo.class.getMethod("ambiguous", Collection.class),
                chooseMethod(methods, new Class<?>[]{Set.class}, true)
        );

        assertEquals(
                Foo.class.getMethod("ambiguous", AbstractList.class),
                chooseMethod(methods, new Class<?>[]{ArrayList.class}, true)
        );

    }

    @Test
    void cannotResolvedAmbiguous() {
        var methods = find(Foo.class, "cannotResolved")
                .toList();

        assertThrows(AmbiguousMethodException.class,
                () -> chooseMethod(methods, new Class<?>[]{Foo.class}, true)
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
