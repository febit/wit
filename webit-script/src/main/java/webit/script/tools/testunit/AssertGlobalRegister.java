// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script.tools.testunit;

import java.lang.reflect.Array;
import webit.script.Context;
import webit.script.global.GlobalManager;
import webit.script.global.GlobalRegister;
import webit.script.lang.MethodDeclare;
import webit.script.util.ArrayUtil;

/**
 *
 * @author zqq90
 */
public class AssertGlobalRegister implements GlobalRegister {

    public static final String ASSERT_COUNT_KEY = "$$LIB_ASSERT_COUNT";

    protected static final MethodDeclare assertTrue = new MethodDeclare() {

        public Object invoke(Context context, Object[] args) {
            plusAssertCount(context);
            assertObjectTrue(ArrayUtil.get(args, 0, null));
            return null;
        }
    };

    protected static final MethodDeclare assertFalse = new MethodDeclare() {

        public Object invoke(Context context, Object[] args) {
            plusAssertCount(context);
            assertObjectFalse(ArrayUtil.get(args, 0, null));
            return null;
        }
    };

    protected static final MethodDeclare assertNotNull = new MethodDeclare() {

        public Object invoke(Context context, Object[] args) {
            plusAssertCount(context);
            assertNotNull(ArrayUtil.get(args, 0, null));
            return null;
        }
    };

    protected static final MethodDeclare assertNull = new MethodDeclare() {

        public Object invoke(Context context, Object[] args) {
            plusAssertCount(context);
            assertNull(ArrayUtil.get(args, 0, null));
            return null;
        }
    };

    protected static final MethodDeclare assertEquals = new MethodDeclare() {

        public Object invoke(Context context, Object[] args) {
            plusAssertCount(context);
            args = ArrayUtil.ensureMinSize(args, 2);
            assertEquals(args[0], args[1]);
            return null;
        }
    };

    protected static final MethodDeclare assertSame = new MethodDeclare() {

        public Object invoke(Context context, Object[] args) {
            plusAssertCount(context);
            args = ArrayUtil.ensureMinSize(args, 2);
            assertSame(args[0], args[1]);
            return null;
        }
    };

    protected static final MethodDeclare assertNotSame = new MethodDeclare() {

        public Object invoke(Context context, Object[] args) {
            plusAssertCount(context);
            args = ArrayUtil.ensureMinSize(args, 2);
            assertNotSame(args[0], args[1]);
            return null;
        }
    };

    protected static final MethodDeclare assertArrayEquals = new MethodDeclare() {

        public Object invoke(Context context, Object[] args) {
            plusAssertCount(context);
            args = ArrayUtil.ensureMinSize(args, 2);
            assertArrayEquals(args[0], args[1]);
            return null;
        }
    };

    public void regist(final GlobalManager manager) {
        manager.setConst("assertTrue", assertTrue);
        manager.setConst("assertFalse", assertFalse);
        manager.setConst("assertNull", assertNull);
        manager.setConst("assertNotNull", assertNotNull);
        manager.setConst("assertSame", assertSame);
        manager.setConst("assertNotSame", assertNotSame);
        manager.setConst("assertEquals", assertEquals);
        manager.setConst("assertArrayEquals", assertArrayEquals);
    }

    private static void plusAssertCount(Context context) {
        Integer count = (Integer) context.getLocalVar(ASSERT_COUNT_KEY);
        context.setLocalVar(ASSERT_COUNT_KEY, count != null ? count + 1 : 1);
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
            fail("expected array was null");
        }
        if (actuals == null) {
            fail("actual array was null");
        }
        int actualsLength = Array.getLength(actuals);
        int expectedsLength = Array.getLength(expecteds);
        if (actualsLength != expectedsLength) {
            fail("array lengths differed, expected.length="
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
