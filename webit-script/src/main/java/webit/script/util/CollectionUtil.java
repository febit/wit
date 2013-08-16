package webit.script.util;

import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import webit.script.exceptions.ScriptRuntimeException;
import webit.script.util.collection.*;

/**
 *
 * @author Zqq
 */
public class CollectionUtil {

    public static int getSize(Object object) {
        if (object == null) {
            return 0;
        }
        Class cls = object.getClass();

        if (object instanceof Object[]) {
            return ((Object[]) object).length;
        } else if (cls == int[].class) {
            return ((int[]) object).length;
        } else if (cls == long[].class) {
            return ((long[]) object).length;
        } else if (cls == float[].class) {
            return ((float[]) object).length;
        } else if (cls == double[].class) {
            return ((double[]) object).length;
        } else if (cls == short[].class) {
            return ((short[]) object).length;
        } else if (cls == byte[].class) {
            return ((byte[]) object).length;
        } else if (cls == char[].class) {
            return ((char[]) object).length;
        } else if (cls == boolean[].class) {
            return ((boolean[]) object).length;
        } else if (object instanceof Collection<?>) {
            return ((Collection<?>) object).size();
        } else if (object instanceof Map<?, ?>) {
            return ((Map<?, ?>) object).size();
        } else if (object instanceof CharSequence) {
            return ((CharSequence) object).length();
        }
        return -1;
    }

    public static Object getByIndex(Object o1, Object key) {
        if (o1 == null || key == null) {
            throw new ScriptRuntimeException("value is null");
        }


        //ARRAY

        Class clazz = o1.getClass();
        if (clazz.isArray() && key instanceof Number) {
            int index = ((Number) key).intValue();
            try {

                if (o1 instanceof Object[]) {
                    return ((Object[]) o1)[index];
                } //
                else if (clazz == int[].class) {
                    return ((int[]) o1)[index];
                } //
                else if (clazz == boolean[].class) {
                    return ((boolean[]) o1)[index];
                } //
                else if (clazz == char[].class) {
                    return ((char[]) o1)[index];
                } //
                else if (clazz == float[].class) {
                    return ((float[]) o1)[index];
                } //
                else if (clazz == double[].class) {
                    return ((double[]) o1)[index];
                } //
                else if (clazz == long[].class) {
                    return ((long[]) o1)[index];
                } //
                else if (clazz == short[].class) {
                    return ((short[]) o1)[index];
                } //
                else if (clazz == byte[].class) {
                    return ((byte[]) o1)[index];
                } //
            } catch (ArrayIndexOutOfBoundsException e) {
                throw new ScriptRuntimeException("Array index out of bounds, index=" + index);
            }
        }
        if (o1 instanceof List && key instanceof Number) {
            return ((List) o1).get(((Number) key).intValue());
        }
        if (o1 instanceof CharSequence && key instanceof Number) {
            return ((CharSequence) o1).charAt(((Number) key).intValue());
        }
        if (o1 instanceof Map) {
            return ((Map) o1).get(key);
        }
        throw new ScriptRuntimeException("Unsupported type: " + o1.getClass().getName());
    }

    public static void setByIndex(Object o1, Object key, Object value) {
        if (o1 == null || key == null) {
            throw new ScriptRuntimeException("value is null");
        }

        if (o1 instanceof Map) {
            ((Map) o1).put(key, value);
            return;
        }
        if (o1 instanceof List && key instanceof Number) {
            ((List) o1).set(((Number) key).intValue(), value);
            return;
        }

        //if (o1 instanceof CharSequence && key instanceof Number) {
        //    throw new ScriptRuntimeException("CharSequence isn't resetable");
        //}
        //ARRAY

        Class clazz = o1.getClass();
        if (clazz.isArray() && key instanceof Number) {
            int index = ((Number) key).intValue();
            try {

                if (o1 instanceof Object[]) {
                    ((Object[]) o1)[index] = value;
                    return;
                } //
                else if (clazz == int[].class) {
                    ((int[]) o1)[index] = ((Number) value).intValue();
                    return;
                } //
                else if (clazz == boolean[].class) {
                    ((boolean[]) o1)[index] = ALU.toBoolean(value);
                    return;
                } //
                else if (clazz == char[].class) {
                    ((char[]) o1)[index] = (Character) value;
                    return;
                } //
                else if (clazz == float[].class) {
                    ((float[]) o1)[index] = ((Number) value).floatValue();
                    return;
                } //
                else if (clazz == double[].class) {
                    ((double[]) o1)[index] = ((Number) value).doubleValue();
                    return;
                } //
                else if (clazz == long[].class) {
                    ((long[]) o1)[index] = ((Number) value).longValue();
                    return;
                } //
                else if (clazz == short[].class) {
                    ((short[]) o1)[index] = ((Number) value).shortValue();
                    return;
                } //
                else if (clazz == byte[].class) {
                    ((byte[]) o1)[index] = ((Number) value).byteValue();
                    return;
                } //
            } catch (ArrayIndexOutOfBoundsException e) {
                throw new ScriptRuntimeException("Array index out of bounds, index=" + index);
            }
        }

        throw new ScriptRuntimeException("Unsupported type: " + o1.getClass().getName());
    }

    public static String arrayToString(Object o1) {
        if (o1 == null) {
            return null;
        }

        Class clazz = o1.getClass();
        if (clazz.isArray()) {
            if (o1 instanceof Object[]) {
                return Arrays.toString((Object[]) o1);
            } //
            else if (clazz == int[].class) {
                return Arrays.toString((int[]) o1);
            } //
            else if (clazz == boolean[].class) {
                return Arrays.toString((boolean[]) o1);
            } //
            else if (clazz == char[].class) {
                return Arrays.toString((char[]) o1);
            } //
            else if (clazz == float[].class) {
                return Arrays.toString((float[]) o1);
            } //
            else if (clazz == double[].class) {
                return Arrays.toString((double[]) o1);
            } else if (clazz == long[].class) {
                return Arrays.toString((long[]) o1);
            } //
            else if (clazz == short[].class) {
                return Arrays.toString((short[]) o1);
            } //
            else if (clazz == byte[].class) {
                return Arrays.toString((byte[]) o1);
            } //
        }

        throw new ScriptRuntimeException("Unsupported type: " + o1.getClass().getName());
    }

    public static Iter toIter(Object o1) {
        if (o1 == null) {
            return null;
        }
        
        else if (o1 instanceof Iter) {
            return (Iter)o1;
        }

        else if (o1 instanceof Iterable) {
            return new IteratorIterAdapter(((Iterable) o1).iterator());
        }

        else if (o1 instanceof Iterator) {
            return new IteratorIterAdapter((Iterator) o1);
        }

        else if (o1 instanceof Enumeration) {
            return new EnumerationIterAdapter((Enumeration) o1);
        }

        else if (o1 instanceof CharSequence) {
            return new CharSequenceIterAdapter((CharSequence) o1);
        }

        Class clazz = o1.getClass();
        if (clazz.isArray()) {
            if (o1 instanceof Object[]) {
                return new ArrayIterAdapter((Object[]) o1);
            } //
            else if (clazz == int[].class) {
                return new IntArrayIterAdapter((int[]) o1);
            } //
            else if (clazz == boolean[].class) {
                return new BooleanArrayIterAdapter((boolean[]) o1);
            } //
            else if (clazz == char[].class) {
                return new CharArrayIterAdapter((char[]) o1);
            } //
            else if (clazz == float[].class) {
                return new FloatArrayIterAdapter((float[]) o1);
            } //
            else if (clazz == double[].class) {
                return new DoubleArrayIterAdapter((double[]) o1);
            } else if (clazz == long[].class) {
                return new LongArrayIterAdapter((long[]) o1);
            } //
            else if (clazz == short[].class) {
                return new ShortArrayIterAdapter((short[]) o1);
            } //
            else if (clazz == byte[].class) {
                return new ByteArrayIterAdapter((byte[]) o1);
            } //
        }

        throw new ScriptRuntimeException("Unsupported type: " + o1.getClass().getName());
    }

    public static Iter toIter(Set o1) {
        return new IteratorIterAdapter(o1.iterator());
    }

    public static boolean notEmpty(Object object, boolean defaultValue) {
        int size = getSize(object);
        if (size == 0) {
            return false;
        } else if (size > 0) {
            return true;
        } else {

            if (object instanceof Iterable) {
                return ((Iterable) object).iterator().hasNext();
            }

            if (object instanceof Iterator) {
                return ((Iterator) object).hasNext();
            }

            if (object instanceof Enumeration) {
                return ((Enumeration) object).hasMoreElements();
            }
        }

        return defaultValue;
    }

    public static Map toMap(Object o1) {
        if (o1 == null) {
            return null;
        }
        if (o1 instanceof Map) {
            return (Map) o1;
        }
        throw new ScriptRuntimeException("Uncable convert type to Map:" + o1.getClass().getName());
    }
}
