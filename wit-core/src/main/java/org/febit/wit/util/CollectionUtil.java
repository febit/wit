// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.util;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import org.febit.wit.core.ast.Statement;
import org.febit.wit.exceptions.ScriptRuntimeException;
import org.febit.wit.lang.Iter;
import org.febit.wit.lang.KeyIter;
import org.febit.wit.lang.iter.*;

/**
 *
 * @author zqq90
 */
public class CollectionUtil {

    private CollectionUtil() {
    }

    public static int getSize(final Object object) {
        if (object == null) {
            return 0;
        }
        if (object.getClass().isArray()) {
            return Array.getLength(object);
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

    public static boolean notEmpty(final Object object, final boolean defaultValue) {
        final int size = getSize(object);
        if (size == 0) {
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

    public static KeyIter toKeyIter(final Object o1, Statement statement) {
        if (o1 == null) {
            return null;
        }
        if (o1 instanceof Map) {
            return new MapKeyIter((Map) o1);
        }
        throw new ScriptRuntimeException("Unsupported type to KeyIter: ".concat(o1.getClass().getName()), statement);
    }

    public static Iter toIter(final Object o1, Statement statement) {
        if (o1 == null) {
            return null;
        }
        final Class<?> clazz = o1.getClass();
        if (clazz.isArray()) {
            if (o1 instanceof Object[]) {
                return createIter((Object[]) o1);
            } else if (clazz.getComponentType().isPrimitive()) {
                return createPrimitiveArrayIter(o1);
            }
        } else {
            if (o1 instanceof Iterable) {
                return createIter(((Iterable) o1).iterator());
            }
            if (o1 instanceof Iterator) {
                return createIter((Iterator) o1);
            }
            if (o1 instanceof Iter) {
                return (Iter) o1;
            }
            if (o1 instanceof Enumeration) {
                return createIter((Enumeration) o1);
            }
            if (o1 instanceof CharSequence) {
                return createIter((CharSequence) o1);
            }
        }
        throw new ScriptRuntimeException("Unsupported type to Iter: ".concat(o1.getClass().getName()), statement);
    }

    private static Iter createIter(Object[] array) {
        return new AbstractArrayIter(array.length - 1) {
            @Override
            public Object next() {
                return array[++cursor];
            }
        };
    }

    private static Iter createIter(Iterator iterator) {
        return new AbstractIter() {
            @Override
            protected Object _next() {
                return iterator.next();
            }

            @Override
            public boolean hasNext() {
                return iterator.hasNext();
            }
        };
    }

    private static Iter createIter(Enumeration enumeration) {
        return new AbstractIter() {
            @Override
            protected Object _next() {
                return enumeration.nextElement();
            }

            @Override
            public boolean hasNext() {
                return enumeration.hasMoreElements();
            }
        };
    }

    private static Iter createIter(CharSequence seq) {
        return new AbstractArrayIter(seq.length() - 1) {
            @Override
            public Object next() {
                return seq.charAt(++cursor);
            }
        };
    }

    private static Iter createPrimitiveArrayIter(Object array) {
        return new AbstractArrayIter(Array.getLength(array) - 1) {
            @Override
            public Object next() {
                return Array.get(array, ++cursor);
            }
        };
    }

    public static Iter createIntAscIter(final int from, final int to) {
        if (from > to) {
            return createIntAscIter(to, from);
        }
        return new Iter() {
            private int current = from - 1;

            @Override
            public boolean hasNext() {
                return current < to;
            }

            @Override
            public Integer next() {
                if (current >= to) {
                    throw new NoSuchElementException("no more next");
                }
                return ++current;
            }

            @Override
            public int index() {
                return current - from;
            }
        };
    }

    public static Iter createIntDescIter(final int from, final int to) {
        if (from < to) {
            return createIntDescIter(to, from);
        }
        return new Iter() {
            private int current = from + 1;

            @Override
            public boolean hasNext() {
                return current > to;
            }

            @Override
            public Integer next() {
                if (current <= to) {
                    throw new NoSuchElementException("no more next");
                }
                return --current;
            }

            @Override
            public int index() {
                return from - current;
            }
        };
    }

}
