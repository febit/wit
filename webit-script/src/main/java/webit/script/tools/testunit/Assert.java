// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script.tools.testunit;

import java.lang.reflect.Array;
import webit.script.Context;
import webit.script.method.MethodDeclare;
import webit.script.util.ArrayUtil;

/**
 *
 * @author zqq90
 */
public class Assert {

    public static final String ASSERT_COUNT_KEY = "$$LIB_ASSERT_COUNT";

    public static final MethodDeclare assertTrue = new MethodDeclare() {

        public Object invoke(Context context, Object[] args) {
            plusAssertCount(context);
            assertObjectTrue(ArrayUtil.get(args, 0, null));
            return null;
        }
    };

    public static final MethodDeclare assertFalse = new MethodDeclare() {

        public Object invoke(Context context, Object[] args) {
            plusAssertCount(context);
            assertObjectFalse(ArrayUtil.get(args, 0, null));
            return null;
        }
    };

    public static final MethodDeclare assertNotNull = new MethodDeclare() {

        public Object invoke(Context context, Object[] args) {
            plusAssertCount(context);
            assertNotNull(ArrayUtil.get(args, 0, null));
            return null;
        }
    };

    public static final MethodDeclare assertNull = new MethodDeclare() {

        public Object invoke(Context context, Object[] args) {
            plusAssertCount(context);
            assertNull(ArrayUtil.get(args, 0, null));
            return null;
        }
    };

    public static final MethodDeclare assertEquals = new MethodDeclare() {

        public Object invoke(Context context, Object[] args) {
            plusAssertCount(context);
            args = ArrayUtil.ensureMinSize(args, 2);
            assertEquals(args[0], args[1]);
            return null;
        }
    };

    public static final MethodDeclare assertSame = new MethodDeclare() {

        public Object invoke(Context context, Object[] args) {
            plusAssertCount(context);
            args = ArrayUtil.ensureMinSize(args, 2);
            assertSame(args[0], args[1]);
            return null;
        }
    };

    public static final MethodDeclare assertNotSame = new MethodDeclare() {

        public Object invoke(Context context, Object[] args) {
            plusAssertCount(context);
            args = ArrayUtil.ensureMinSize(args, 2);
            assertNotSame(args[0], args[1]);
            return null;
        }
    };

    public static final MethodDeclare assertArrayEquals = new MethodDeclare() {

        public Object invoke(Context context, Object[] args) {
            plusAssertCount(context);
            args = ArrayUtil.ensureMinSize(args, 2);
            assertArrayEquals(args[0], args[1]);
            return null;
        }
    };

    private static void plusAssertCount(Context context) {
        context = context.topContext;
        Integer count = (Integer) context.getLocalVar(ASSERT_COUNT_KEY);
        if (count == null) {
            context.setLocalVar(ASSERT_COUNT_KEY, 1);
        } else {
            context.setLocalVar(ASSERT_COUNT_KEY, count + 1);
        }
    }

    private static void assertObjectTrue(Object condition) {
        if (condition instanceof Boolean) {
            assertTrue(((Boolean) condition).booleanValue());
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
        if (expected == null && actual == null) {
            return;
        }
        if (expected != null && expected.equals(actual)) {
            return;
        } else {
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

    private static void assertArrayEquals(Object expecteds, Object actuals) {
        if (expecteds == actuals) {
            return;
        }
        final int expectedsLength = assertArraysAreSameLength(expecteds, actuals);
        for (int i = 0; i < expectedsLength; i++) {
            try {
                assertEquals(Array.get(expecteds, i), Array.get(actuals, i));
            } catch (AssertionError e) {
                fail("arrays first differed at element " + i);
            }
        }
    }

    private static int assertArraysAreSameLength(Object expecteds, Object actuals) {
        if (expecteds == null) {
            Assert.fail("expected array was null");
        }
        if (actuals == null) {
            Assert.fail("actual array was null");
        }
        int actualsLength = Array.getLength(actuals);
        int expectedsLength = Array.getLength(expecteds);
        if (actualsLength != expectedsLength) {
            Assert.fail("array lengths differed, expected.length="
                    + expectedsLength + " actual.length=" + actualsLength);
        }
        return expectedsLength;
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

}
