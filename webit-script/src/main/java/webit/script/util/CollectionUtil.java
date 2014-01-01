// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script.util;

import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;
import webit.script.exceptions.ScriptRuntimeException;
import webit.script.util.collection.*;

/**
 *
 * @author Zqq
 */
public class CollectionUtil {

    public static int getSize(final Object object) {
        if (object == null) {
            return 0;
        }//
        else if (object.getClass().isArray()) {
            return ArrayUtil.getSize(object);
        }//
        else if (object instanceof Collection) {
            return ((Collection) object).size();
        }//
        else if (object instanceof Map) {
            return ((Map) object).size();
        }//
        else if (object instanceof CharSequence) {
            return ((CharSequence) object).length();
        }//
        else {
            return -1;
        }
    }

    @SuppressWarnings("unchecked")
    public static Iter toIter(final Object o1) {
        final Class clazz;
        if (o1 == null) {
            return null;
        }//
        else if (o1 instanceof Iterable) {
            return new IteratorIterAdapter(((Iterable) o1).iterator());
        }//
        else if ((clazz = o1.getClass()).isArray()) {
            if (o1 instanceof Object[]) {
                return new ArrayIterAdapter((Object[]) o1);
            }//
            else if (clazz == int[].class) {
                return new IntArrayIterAdapter((int[]) o1);
            }//
            else if (clazz == boolean[].class) {
                return new BooleanArrayIterAdapter((boolean[]) o1);
            }//
            else if (clazz == char[].class) {
                return new CharArrayIterAdapter((char[]) o1);
            }//
            else if (clazz == float[].class) {
                return new FloatArrayIterAdapter((float[]) o1);
            }//
            else if (clazz == double[].class) {
                return new DoubleArrayIterAdapter((double[]) o1);
            }//
            else if (clazz == long[].class) {
                return new LongArrayIterAdapter((long[]) o1);
            }//
            else if (clazz == short[].class) {
                return new ShortArrayIterAdapter((short[]) o1);
            }//
            else if (clazz == byte[].class) {
                return new ByteArrayIterAdapter((byte[]) o1);
            }//
        }//
        else if (o1 instanceof Iterator) {
            return new IteratorIterAdapter((Iterator) o1);
        }//
        else if (o1 instanceof Iter) {
            return (Iter) o1;
        }//
        else if (o1 instanceof Enumeration) {
            return new EnumerationIterAdapter((Enumeration) o1);
        }//
        else if (o1 instanceof CharSequence) {
            return new CharSequenceIterAdapter((CharSequence) o1);
        }

        throw new ScriptRuntimeException("Unsupported type: ".concat(o1.getClass().getName()));
    }

    public static <T> Iter<T> toIter(final Iterable<T> o1) {
        return new IteratorIterAdapter<T>(o1.iterator());
    }

    public static boolean notEmpty(final Object object, final boolean defaultValue) {
        final int size;
        if ((size = getSize(object)) == 0) {
            return false;
        } else if (size > 0) {
            return true;
        }//
        else if (object instanceof Iterable) {
            return ((Iterable) object).iterator().hasNext();
        }//
        else if (object instanceof Iterator) {
            return ((Iterator) object).hasNext();
        }//
        else if (object instanceof Enumeration) {
            return ((Enumeration) object).hasMoreElements();
        }

        return defaultValue;
    }
}
