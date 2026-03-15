package org.febit.wit.util;

import org.febit.wit.exception.AmbiguousMethodException;
import org.junit.jupiter.api.Test;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class NativeMethodsChooseCtorTest {

    @Test
    void ambiguous() throws NoSuchMethodException {
        var ctors = List.of(Foo.class.getConstructors());

        assertEquals(
                Foo.class.getConstructor(Collection.class),
                NativeMethods.chooseConstructor(ctors, new Class<?>[]{Collection.class})
        );

        assertEquals(
                Foo.class.getConstructor(List.class),
                NativeMethods.chooseConstructor(ctors, new Class<?>[]{List.class})
        );
        assertEquals(
                Foo.class.getConstructor(AbstractList.class),
                NativeMethods.chooseConstructor(ctors, new Class<?>[]{AbstractList.class})
        );

        assertEquals(
                Foo.class.getConstructor(Collection.class),
                NativeMethods.chooseConstructor(ctors, new Class<?>[]{Set.class})
        );

        assertEquals(
                Foo.class.getConstructor(AbstractList.class),
                NativeMethods.chooseConstructor(ctors, new Class<?>[]{ArrayList.class})
        );
    }

    @Test
    void cannotResolved() {
        var ctors = List.of(Foo.class.getConstructors());
        assertThrows(AmbiguousMethodException.class, () ->
                NativeMethods.chooseConstructor(ctors, new Class<?>[]{Integer.class, Integer.class})
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
