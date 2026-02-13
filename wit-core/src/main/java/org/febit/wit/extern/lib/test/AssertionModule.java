// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.extern.lib.test;

import org.febit.wit.GlobalHeapRegister;
import org.febit.wit.runtime.InternalContext;
import org.febit.wit.runtime.Undefined;
import org.febit.wit.runtime.heap.GlobalHeap;
import org.jspecify.annotations.Nullable;

import java.lang.reflect.Array;
import java.util.concurrent.atomic.LongAdder;

import static org.febit.wit.util.ArrayUtils.get;

public class AssertionModule implements GlobalHeapRegister {

    public static final String ASSERT_COUNT_KEY = "$$LIB_ASSERT_COUNT";

    @Override
    public void register(final GlobalHeap heap) {
        heap.setConstMethod("assertTrue", AssertionModule::assertTrue);
        heap.setConstMethod("assertFalse", AssertionModule::assertFalse);
        heap.setConstMethod("assertNull", AssertionModule::assertNull);
        heap.setConstMethod("assertNotNull", AssertionModule::assertNotNull);
        heap.setConstMethod("assertSame", AssertionModule::assertSame);
        heap.setConstMethod("assertNotSame", AssertionModule::assertNotSame);
        heap.setConstMethod("assertEquals", AssertionModule::assertEquals);
        heap.setConstMethod("assertArrayEquals", AssertionModule::assertArrayEquals);
    }

    private static void plusAssertCount(InternalContext context) {
        LongAdder count = (LongAdder) context.local().get(ASSERT_COUNT_KEY);
        if (count == null) {
            count = new LongAdder();
            context.local().set(ASSERT_COUNT_KEY, count);
        }
        count.increment();
    }

    private static void assertObjectTrue(@Nullable Object condition) {
        if (condition instanceof Boolean bool) {
            assertTrue(bool);
        } else {
            fail("not a Boolean");
        }
    }

    private static void assertTrue(boolean condition) {
        if (!condition) {
            fail(null);
        }
    }

    private static void assertObjectFalse(@Nullable Object condition) {
        if (condition instanceof Boolean bool) {
            assertTrue(!bool);
        } else {
            fail("not a Boolean");
        }
    }

    private static void assertNotNull(@Nullable Object object) {
        assertTrue(object != null);
    }

    private static void assertNull(@Nullable Object object) {
        assertTrue(object == null);
    }

    private static void assertEquals(@Nullable Object expected, @Nullable Object actual) {
        if ((expected == null && actual != null)
                || (expected != null && !expected.equals(actual))) {
            failNotEquals(expected, actual);
        }
    }

    private static void assertSame(@Nullable Object expected, @Nullable Object actual) {
        if (expected == actual) {
            return;
        }
        failNotSame(expected, actual);
    }

    private static void assertNotSame(@Nullable Object unexpected, @Nullable Object actual) {
        if (unexpected == actual) {
            failSame();
        }
    }

    private static void assertArrayEquals(@Nullable Object expected, @Nullable Object actual) {
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

    private static int assertArraysAreSameLength(@Nullable Object expected, @Nullable Object actual) {
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
            return "expected:<" + expectedString + "> but was:<"
                    + actualString + ">";
        }
    }

    private static String formatClassAndValue(@Nullable Object value, String valueString) {
        return (value == null ? "null" : value.getClass().getName()) + "<" + valueString + ">";
    }

    private static Object assertTrue(InternalContext context, @Nullable Object @Nullable [] args) {
        plusAssertCount(context);
        assertObjectTrue(get(args, 0));
        return Undefined.UNDEFINED;
    }

    private static Object assertFalse(InternalContext context, @Nullable Object @Nullable [] args) {
        plusAssertCount(context);
        assertObjectFalse(get(args, 0));
        return Undefined.UNDEFINED;
    }

    private static Object assertNotNull(InternalContext context, @Nullable Object @Nullable [] args) {
        plusAssertCount(context);
        assertNotNull(get(args, 0));
        return Undefined.UNDEFINED;
    }

    private static Object assertNull(InternalContext context, @Nullable Object @Nullable [] args) {
        plusAssertCount(context);
        assertNull(get(args, 0));
        return Undefined.UNDEFINED;
    }

    private static Object assertEquals(InternalContext context, @Nullable Object @Nullable [] args) {
        plusAssertCount(context);
        assertEquals(get(args, 0), get(args, 1));
        return Undefined.UNDEFINED;
    }

    private static Object assertSame(InternalContext context, @Nullable Object @Nullable [] args) {
        plusAssertCount(context);
        assertSame(get(args, 0), get(args, 1));
        return Undefined.UNDEFINED;
    }

    private static Object assertNotSame(InternalContext context, @Nullable Object @Nullable [] args) {
        plusAssertCount(context);
        assertNotSame(get(args, 0), get(args, 1));
        return Undefined.UNDEFINED;
    }

    private static Object assertArrayEquals(InternalContext context, @Nullable Object @Nullable [] args) {
        plusAssertCount(context);
        assertArrayEquals(get(args, 0), get(args, 1));
        return Undefined.UNDEFINED;
    }
}
