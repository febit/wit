package org.febit.wit.util;

import lombok.experimental.UtilityClass;
import org.febit.wit.exception.ScriptEvaluateException;
import org.febit.wit.runtime.ast.Statement;
import org.febit.wit.runtime.iter.AbstractArrayIter;
import org.febit.wit.runtime.iter.AbstractIter;
import org.febit.wit.runtime.iter.Iter;
import org.febit.wit.runtime.iter.KeyIter;
import org.febit.wit.runtime.iter.MapKeyIter;
import org.jspecify.annotations.Nullable;

import java.lang.reflect.Array;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;

@UtilityClass
public class Iters {

    private static final KeyIter EMPTY_KEY_ITER = new KeyIter() {
        @Override
        public Object value() {
            throw new NoSuchElementException("no more next");
        }

        @Override
        public boolean hasNext() {
            return false;
        }

        @Override
        public Object next() {
            throw new NoSuchElementException("no more next");
        }

        @Override
        public int index() {
            return 0;
        }
    };

    public static KeyIter toKeyIter(@Nullable final Object o1, Statement statement) {
        if (o1 == null) {
            return EMPTY_KEY_ITER;
        }
        if (o1 instanceof Map<?, ?> map) {
            return new MapKeyIter<>(map);
        }
        throw new ScriptEvaluateException("Unsupported type to KeyIter: " + o1.getClass(), statement);
    }

    @SuppressWarnings({
            "squid:S3776" // Cognitive Complexity of methods should not be too high
    })
    public static Iter toIter(@Nullable final Object o1, Statement statement) {
        if (o1 == null) {
            return EMPTY_KEY_ITER;
        }
        if (o1 instanceof Iter iter) {
            return iter;
        }
        final Class<?> clazz = o1.getClass();
        if (clazz.isArray()) {
            if (o1 instanceof Object[] arr) {
                return of(arr);
            } else if (clazz.getComponentType().isPrimitive()) {
                return ofPrimitiveArray(o1);
            }
        } else {
            if (o1 instanceof Iterable) {
                return of(((Iterable<?>) o1).iterator());
            }
            if (o1 instanceof Iterator) {
                return of((Iterator<?>) o1);
            }
            if (o1 instanceof Enumeration) {
                return of((Enumeration<?>) o1);
            }
            if (o1 instanceof CharSequence cs) {
                return of(cs);
            }
        }
        throw new ScriptEvaluateException("Unsupported type to Iter: " + o1.getClass(), statement);
    }

    private static Iter of(Object[] array) {
        return new AbstractArrayIter(array.length - 1) {
            @Override
            public Object next() {
                return array[++cursor];
            }
        };
    }

    private static Iter of(Iterator<?> iterator) {
        return new AbstractIter() {
            @Override
            protected Object next0() {
                return iterator.next();
            }

            @Override
            public boolean hasNext() {
                return iterator.hasNext();
            }
        };
    }

    private static Iter of(Enumeration<?> enumeration) {
        return new AbstractIter() {
            @Override
            protected Object next0() {
                return enumeration.nextElement();
            }

            @Override
            public boolean hasNext() {
                return enumeration.hasMoreElements();
            }
        };
    }

    private static Iter of(CharSequence seq) {
        return new AbstractArrayIter(seq.length() - 1) {
            @Override
            public Object next() {
                return seq.charAt(++cursor);
            }
        };
    }

    private static Iter ofPrimitiveArray(Object array) {
        return new AbstractArrayIter(Array.getLength(array) - 1) {
            @Override
            public Object next() {
                return Array.get(array, ++cursor);
            }
        };
    }

    public static Iter asc(int from, int to) {
        if (from > to) {
            return asc(to, from); // NOSONAR
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

    public static Iter desc(int from, int to) {
        if (from < to) {
            return desc(to, from); // NOSONAR
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
