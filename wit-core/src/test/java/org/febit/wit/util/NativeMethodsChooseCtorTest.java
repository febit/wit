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
import java.util.Collection;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class NativeMethodsChooseCtorTest {

    static <T extends Executable> T choose(
            List<T> executables, @Nullable Class<?>... args) {
        return NativeMethods.choose(executables, NativeMethods::distance, args, 0);
    }

    @Test
    void ambiguous() throws NoSuchMethodException {
        var ctors = List.of(Foo.class.getConstructors());

        assertEquals(
                Foo.class.getConstructor(Collection.class),
                choose(ctors, Collection.class)
        );

        assertEquals(
                Foo.class.getConstructor(List.class),
                choose(ctors, List.class)
        );
        assertEquals(
                Foo.class.getConstructor(AbstractList.class),
                choose(ctors, AbstractList.class)
        );

        assertEquals(
                Foo.class.getConstructor(Collection.class),
                choose(ctors, Set.class)
        );

        assertEquals(
                Foo.class.getConstructor(AbstractList.class),
                choose(ctors, ArrayList.class)
        );
    }

    @Test
    void cannotResolved() {
        var ctors = List.of(Foo.class.getConstructors());
        assertThrows(AmbiguousMethodException.class, () ->
                choose(ctors, Integer.class, Integer.class)
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
    }

}
