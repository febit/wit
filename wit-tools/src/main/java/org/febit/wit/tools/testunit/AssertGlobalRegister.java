// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.tools.testunit;

import org.febit.wit.InternalContext;
import org.febit.wit.global.GlobalManager;
import org.febit.wit.global.GlobalRegister;

import java.lang.reflect.Array;
import java.util.concurrent.atomic.LongAdder;

import static org.febit.wit.Context.VOID;
import static org.febit.wit.util.ArrayUtil.get;

/**
 * @author zqq90
 */
public class AssertGlobalRegister implements GlobalRegister {

    public static final String ASSERT_COUNT_KEY = "$$LIB_ASSERT_COUNT";

    @Override
    public void register(final GlobalManager manager) {
        manager.setConstMethod("assertTrue", AssertGlobalRegister::assertTrue);
        manager.setConstMethod("assertFalse", AssertGlobalRegister::assertFalse);
        manager.setConstMethod("assertNull", AssertGlobalRegister::assertNull);
        manager.setConstMethod("assertNotNull", AssertGlobalRegister::assertNotNull);
        manager.setConstMethod("assertSame", AssertGlobalRegister::assertSame);
        manager.setConstMethod("assertNotSame", AssertGlobalRegister::assertNotSame);
        manager.setConstMethod("assertEquals", AssertGlobalRegister::assertEquals);
        manager.setConstMethod("assertArrayEquals", AssertGlobalRegister::assertArrayEquals);
    }

    private static void plusAssertCount(InternalContext context) {
        LongAdder count = (LongAdder) context.getLocalVar(ASSERT_COUNT_KEY);
        if (count == null) {
            count = new LongAdder();
            context.setLocalVar(ASSERT_COUNT_KEY, count);
        }
        count.increment();
    }

    private static void assertObjectTrue(Object condition) {
        if (condition instanceof Boolean) {
            assertTrue((Boolean) condition);
        } else {
            fail("not a Boolean");
        }
    }

    private static void assertTrue(boolean condition) {
        if (!condition) {
            fail(null);
        }
    }

    private static void assertObjectFalse(Object condition) {
        if (condition instanceof Boolean) {
            assertTrue(!(Boolean) condition);
        } else {
            fail("not a Boolean");
        }
    }

    private static void assertNotNull(Object object) {
        assertTrue(object != null);
    }

    private static void assertNull(Object object) {
        assertTrue(object == null);
    }

    private static void assertEquals(Object expected, Object actual) {
        if ((expected == null && actual != null)
                || (expected != null && !expected.equals(actual))) {
            failNotEquals(expected, actual);
        }
    }

    private static void assertSame(Object expected, Object actual) {
        if (expected == actual) {
            return;
        }
        failNotSame(expected, actual);
    }

    private static void assertNotSame(Object unexpected, Object actual) {
        if (unexpected == actual) {
            failSame();
        }
    }

    private static void assertArrayEquals(Object expected, Object actual) {
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

    private static int assertArraysAreSameLength(Object expected, Object actual) {
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

    private static void fail(String message) {
        throw new AssertionError(message == null ? "" : message);
    }

    private static void failSame() {
        fail("expected not same");
    }

    private static void failNotSame(Object expected, Object actual) {
        fail("expected same:<" + expected + "> was not:<" + actual + ">");
    }

    private static void failNotEquals(Object expected, Object actual) {
        fail(format(expected, actual));
    }

    private static String format(Object expected, Object actual) {
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

    private static String formatClassAndValue(Object value, String valueString) {
        return (value == null ? "null" : value.getClass().getName()) + "<" + valueString + ">";
    }

    private static Object assertTrue(InternalContext context, Object[] args) {
        plusAssertCount(context);
        assertObjectTrue(get(args, 0));
        return VOID;
    }

    private static Object assertFalse(InternalContext context, Object[] args) {
        plusAssertCount(context);
        assertObjectFalse(get(args, 0));
        return VOID;
    }

    private static Object assertNotNull(InternalContext context, Object[] args) {
        plusAssertCount(context);
        assertNotNull(get(args, 0));
        return VOID;
    }

    private static Object assertNull(InternalContext context, Object[] args) {
        plusAssertCount(context);
        assertNull(get(args, 0));
        return VOID;
    }

    private static Object assertEquals(InternalContext context, Object[] args) {
        plusAssertCount(context);
        assertEquals(get(args, 0), get(args, 1));
        return VOID;
    }

    private static Object assertSame(InternalContext context, Object[] args) {
        plusAssertCount(context);
        assertSame(get(args, 0), get(args, 1));
        return VOID;
    }

    private static Object assertNotSame(InternalContext context, Object[] args) {
        plusAssertCount(context);
        assertNotSame(get(args, 0), get(args, 1));
        return VOID;
    }

    private static Object assertArrayEquals(InternalContext context, Object[] args) {
        plusAssertCount(context);
        assertArrayEquals(get(args, 0), get(args, 1));
        return VOID;
    }
}
