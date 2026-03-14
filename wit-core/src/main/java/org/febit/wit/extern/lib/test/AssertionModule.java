// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.extern.lib.test;

import lombok.experimental.UtilityClass;
import org.febit.wit.Wit;
import org.febit.wit.WitModule;
import org.febit.wit.runtime.InternalContext;
import org.febit.wit.runtime.Undefined;
import org.febit.wit.runtime.WitFunction;
import org.jspecify.annotations.Nullable;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.atomic.LongAdder;

import static org.febit.wit.util.Args.at;
import static org.febit.wit.util.ClassUtils.className;

public class AssertionModule implements WitModule {

    public static final String ASSERT_COUNT_KEY = "$$LIB_ASSERT_COUNT";

    @Override
    public void apply(Wit wit) {
        var heap = wit.staticHeaps().constants();
        heap.setFunction("assertTrue", Assertions::assertTrue);
        heap.setFunction("assertFalse", Assertions::assertFalse);

        heap.setFunction("assertNull", Assertions::assertNull);
        heap.setFunction("assertNotNull", Assertions::assertNotNull);

        heap.setFunction("assertSame", Assertions::assertSame);
        heap.setFunction("assertNotSame", Assertions::assertNotSame);

        heap.setFunction("assertEquals", Assertions::assertEquals);
        heap.setFunction("assertNotEquals", Assertions::assertNotEquals);

        heap.setFunction("assertArrayEquals", Assertions::assertArrayEquals);

        heap.setFunction("assertInstanceOf", Assertions::assertInstanceOf);
        heap.setFunction("assertNotInstanceOf", Assertions::assertNotInstanceOf);

        heap.setFunction("assertThrows", Assertions::assertThrows);
        heap.setFunction("assertDoesNotThrow", Assertions::assertDoesNotThrow);
    }

    @UtilityClass
    private static class Assertions {

        private static Object assertTrue(InternalContext context, @Nullable Object @Nullable [] args) {
            plusAssertCount(context);
            AssertionModule.assertObjectTrue(at(args, 0));
            return Undefined.UNDEFINED;
        }

        private static Object assertFalse(InternalContext context, @Nullable Object @Nullable [] args) {
            plusAssertCount(context);
            AssertionModule.assertObjectFalse(at(args, 0));
            return Undefined.UNDEFINED;
        }

        private static Object assertNotNull(InternalContext context, @Nullable Object @Nullable [] args) {
            plusAssertCount(context);
            AssertionModule.assertNotNull(at(args, 0));
            return Undefined.UNDEFINED;
        }

        private static Object assertNull(InternalContext context, @Nullable Object @Nullable [] args) {
            plusAssertCount(context);
            AssertionModule.assertNull(at(args, 0));
            return Undefined.UNDEFINED;
        }

        private static Object assertEquals(InternalContext context, @Nullable Object @Nullable [] args) {
            plusAssertCount(context);
            AssertionModule.assertEquals(at(args, 0), at(args, 1));
            return Undefined.UNDEFINED;
        }

        private static Object assertNotEquals(InternalContext context, @Nullable Object @Nullable [] args) {
            plusAssertCount(context);
            AssertionModule.assertNotEquals(at(args, 0), at(args, 1));
            return Undefined.UNDEFINED;
        }

        private static Object assertSame(InternalContext context, @Nullable Object @Nullable [] args) {
            plusAssertCount(context);
            AssertionModule.assertSame(at(args, 0), at(args, 1));
            return Undefined.UNDEFINED;
        }

        private static Object assertNotSame(InternalContext context, @Nullable Object @Nullable [] args) {
            plusAssertCount(context);
            AssertionModule.assertNotSame(at(args, 0), at(args, 1));
            return Undefined.UNDEFINED;
        }

        private static Object assertArrayEquals(InternalContext context, @Nullable Object @Nullable [] args) {
            plusAssertCount(context);
            AssertionModule.assertArrayEquals(at(args, 0), at(args, 1));
            return Undefined.UNDEFINED;
        }

        private static Object assertInstanceOf(InternalContext context, @Nullable Object @Nullable [] args) {
            plusAssertCount(context);
            var expected = at(args, 0);
            if (expected instanceof Class<?> expectedClass) {
                AssertionModule.assertInstanceOf(expectedClass, at(args, 1));
            } else {
                fail("expected should be a Class, but was " + className(expected));
            }
            return Undefined.UNDEFINED;
        }

        private static Object assertNotInstanceOf(InternalContext context, @Nullable Object @Nullable [] args) {
            plusAssertCount(context);
            var unexpected = at(args, 0);
            if (unexpected instanceof Class<?> unexpectedClass) {
                AssertionModule.assertNotInstanceOf(unexpectedClass, at(args, 1));
            } else {
                fail("unexpected should be a Class, but was " + className(unexpected));
            }
            return Undefined.UNDEFINED;
        }

        @SuppressWarnings("unchecked")
        private static Object assertThrows(InternalContext context, @Nullable Object @Nullable [] args) {
            plusAssertCount(context);
            var arg0 = at(args, 0);
            if (!(arg0 instanceof Class<?> expected)) {
                fail("expected should be a Class, but was " + className(arg0));
                return Undefined.UNDEFINED;
            }
            if (!Throwable.class.isAssignableFrom(expected)) {
                fail("expected should be a Class of Throwable, but was " + expected.getName());
                return Undefined.UNDEFINED;
            }
            var func = at(args, 1);
            if (!(func instanceof WitFunction)) {
                fail("executable should be a function, but was " + className(func));
                return Undefined.UNDEFINED;
            }

            Objects.requireNonNull(args);
            var funcArgs = Arrays.copyOfRange(args, 2, args.length);
            return AssertionModule.assertThrows(
                    (Class<? extends Throwable>) expected,
                    () -> ((WitFunction) func).apply(context, funcArgs)
            );
        }

        private static Object assertDoesNotThrow(InternalContext context, @Nullable Object @Nullable [] args) {
            plusAssertCount(context);
            var func = at(args, 0);
            if (!(func instanceof WitFunction)) {
                fail("executable should be a function, but was " + className(func));
                return Undefined.UNDEFINED;
            }

            Objects.requireNonNull(args);
            var funcArgs = Arrays.copyOfRange(args, 1, args.length);
            try {
                ((WitFunction) func).apply(context, funcArgs);
            } catch (Throwable actualThrown) {
                fail("Expected no exception to be thrown, but was " + actualThrown);
            }
            return Undefined.UNDEFINED;
        }
    }

    static void plusAssertCount(InternalContext context) {
        LongAdder count = (LongAdder) context.local().get(ASSERT_COUNT_KEY);
        if (count == null) {
            count = new LongAdder();
            context.local().set(ASSERT_COUNT_KEY, count);
        }
        count.increment();
    }

    static void assertObjectTrue(@Nullable Object condition) {
        if (condition instanceof Boolean bool) {
            assertTrue(bool);
        } else {
            fail("not a Boolean");
        }
    }

    static void assertInstanceOf(Class<?> expectedType, @Nullable Object actual) {
        if (!expectedType.isInstance(actual)) {
            fail("Expected instance of "
                    + expectedType.getName()
                    + " but was "
                    + className(actual)
            );
        }
    }

    static void assertNotInstanceOf(Class<?> unexpectedType, @Nullable Object actual) {
        if (unexpectedType.isInstance(actual)) {
            fail("Expected not instance of "
                    + unexpectedType.getName()
                    + " but was "
                    + className(actual)
            );
        }
    }

    static Throwable assertThrows(Class<? extends Throwable> expectedType, Runnable executable) {
        try {
            executable.run();
        } catch (Throwable actualThrown) {
            if (expectedType.isInstance(actualThrown)) {
                return actualThrown;
            }
            fail("Expected " + expectedType.getName() + " to be thrown, but was " + actualThrown);
        }
        fail("Expected " + expectedType.getName() + " to be thrown, but nothing was thrown.");
        return null;
    }

    static void assertTrue(boolean condition) {
        if (!condition) {
            fail(null);
        }
    }

    static void assertObjectFalse(@Nullable Object condition) {
        if (condition instanceof Boolean bool) {
            assertTrue(!bool);
        } else {
            fail("not a Boolean");
        }
    }

    static void assertNotNull(@Nullable Object object) {
        assertTrue(object != null);
    }

    static void assertNull(@Nullable Object object) {
        assertTrue(object == null);
    }

    static void assertEquals(@Nullable Object expected, @Nullable Object actual) {
        if ((expected == null && actual != null)
                || (expected != null && !expected.equals(actual))) {
            failNotEquals(expected, actual);
        }
    }

    static void assertNotEquals(@Nullable Object unexpected, @Nullable Object actual) {
        if ((unexpected == null && actual == null)
                || (unexpected != null && unexpected.equals(actual))) {
            fail("expected not equals:<" + unexpected + ">");
        }
    }

    static void assertSame(@Nullable Object expected, @Nullable Object actual) {
        if (expected == actual) {
            return;
        }
        failNotSame(expected, actual);
    }

    static void assertNotSame(@Nullable Object unexpected, @Nullable Object actual) {
        if (unexpected == actual) {
            failSame();
        }
    }

    static void assertArrayEquals(@Nullable Object expected, @Nullable Object actual) {
        if (expected == actual) {
            return;
        }
        final int expectedLength = assertArraysAreSameLength(expected, actual);
        for (int i = 0; i < expectedLength; i++) {
            try {
                assertEquals(Array.get(expected, i), Array.get(actual, i));
            } catch (AssertionError e) {
                fail("arrays first differed at element " + i);
            }
        }
    }

    static int assertArraysAreSameLength(@Nullable Object expected, @Nullable Object actual) {
        if (expected == null) {
            fail("expected array was null");
        }
        if (actual == null) {
            fail("actual array was null");
        }
        int actualLength = Array.getLength(actual);
        int expectedLength = Array.getLength(expected);
        if (actualLength != expectedLength) {
            fail("array lengths differed, expected.length="
                    + expectedLength + " actual.length=" + actualLength);
        }
        return expectedLength;
    }

    private static void fail(@Nullable String message) {
        throw new AssertionError(message == null ? "" : message);
    }

    private static void failSame() {
        fail("expected not same");
    }

    private static void failNotSame(@Nullable Object expected, @Nullable Object actual) {
        fail("expected same:<" + expected + "> was not:<" + actual + ">");
    }

    private static void failNotEquals(@Nullable Object expected, @Nullable Object actual) {
        fail(format(expected, actual));
    }

    private static String format(@Nullable Object expected, @Nullable Object actual) {
        String expectedString = String.valueOf(expected);
        String actualString = String.valueOf(actual);
        if (expectedString.equals(actualString)) {
            return "expected: "
                    + formatClassAndValue(expected, expectedString)
                    + " but was: " + formatClassAndValue(actual, actualString);
        } else {
            return "expected:<" + expectedString + "> but was:<" + actualString + ">";
        }
    }

    private static String formatClassAndValue(@Nullable Object value, String valueString) {
        return className(value) + "<" + valueString + ">";
    }

}
