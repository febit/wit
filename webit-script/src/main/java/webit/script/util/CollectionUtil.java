// Copyright (c) 2013-2015, Webit Team. All Rights Reserved.
package webit.script.util;

import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;
import webit.script.core.ast.Statement;
import webit.script.exceptions.ScriptRuntimeException;
import webit.script.lang.Iter;
import webit.script.lang.KeyIter;
import webit.script.lang.iter.*;

/**
 *
 * @author zqq90
 */
public class CollectionUtil {

    public static int getSize(final Object object) {
        if (object == null) {
            return 0;
        }
        if (object.getClass().isArray()) {
            return ArrayUtil.getSize(object);
        }
        if (object instanceof Collection) {
            return ((Collection) object).size();
        }
        if (object instanceof Map) {
            return ((Map) object).size();
        }
        if (object instanceof CharSequence) {
            return ((CharSequence) object).length();
        }
        return -1;
    }

    @SuppressWarnings("unchecked")
    public static Iter toIter(final Object o1, Statement statement) {
        final Class clazz;
        if (o1 == null) {
            return null;
        }
        if (o1 instanceof Iterable) {
            return new IteratorIter(((Iterable) o1).iterator());
        }
        if ((clazz = o1.getClass()).isArray()) {
            if (o1 instanceof Object[]) {
                return new ArrayIter((Object[]) o1);
            } else if (clazz == int[].class) {
                return new IntArrayIter((int[]) o1);
            } else if (clazz == boolean[].class) {
                return new BooleanArrayIter((boolean[]) o1);
            } else if (clazz == char[].class) {
                return new CharArrayIter((char[]) o1);
            } else if (clazz == float[].class) {
                return new FloatArrayIter((float[]) o1);
            } else if (clazz == double[].class) {
                return new DoubleArrayIter((double[]) o1);
            } else if (clazz == long[].class) {
                return new LongArrayIter((long[]) o1);
            } else if (clazz == short[].class) {
                return new ShortArrayIter((short[]) o1);
            } else if (clazz == byte[].class) {
                return new ByteArrayIter((byte[]) o1);
            }
        } else if (o1 instanceof Iterator) {
            return new IteratorIter((Iterator) o1);
        } else if (o1 instanceof Iter) {
            return (Iter) o1;
        } else if (o1 instanceof Enumeration) {
            return new EnumerationIter((Enumeration) o1);
        } else if (o1 instanceof CharSequence) {
            return new CharSequenceIter((CharSequence) o1);
        }
        throw new ScriptRuntimeException("Unsupported type: ".concat(o1.getClass().getName()), statement);
    }

    public static KeyIter toKeyIter(final Object o1, Statement statement) {
        if (o1 == null) {
            return null;
        }
        if (o1 instanceof Map) {
            return new MapKeyIter((Map) o1);
        }
        throw new ScriptRuntimeException("Unsupported type: ".concat(o1.getClass().getName()), statement);
    }

    public static boolean notEmpty(final Object object, final boolean defaultValue) {
        final int size;
        if ((size = getSize(object)) == 0) {
            return false;
        }
        if (size > 0) {
            return true;
        }
        if (object instanceof Iterable) {
            return ((Iterable) object).iterator().hasNext();
        }
        if (object instanceof Iterator) {
            return ((Iterator) object).hasNext();
        }
        if (object instanceof Enumeration) {
            return ((Enumeration) object).hasMoreElements();
        }

        return defaultValue;
    }
}
