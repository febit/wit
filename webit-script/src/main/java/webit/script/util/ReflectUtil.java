package webit.script.util;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Zqq
 */
public class ReflectUtil {

    public static Class<?> getUnboxedClass(Class<?> type) {
        if (type == Boolean.class) {
            return boolean.class;
        } else if (type == Character.class) {
            return char.class;
        } else if (type == Byte.class) {
            return byte.class;
        } else if (type == Short.class) {
            return short.class;
        } else if (type == Integer.class) {
            return int.class;
        } else if (type == Long.class) {
            return long.class;
        } else if (type == Float.class) {
            return float.class;
        } else if (type == Double.class) {
            return double.class;
        } else {
            return type;
        }
    }

    public static Class<?> getBoxedClass(Class<?> type) {
        if (type == boolean.class) {
            return Boolean.class;
        } else if (type == char.class) {
            return Character.class;
        } else if (type == byte.class) {
            return Byte.class;
        } else if (type == short.class) {
            return Short.class;
        } else if (type == int.class) {
            return Integer.class;
        } else if (type == long.class) {
            return Long.class;
        } else if (type == float.class) {
            return Float.class;
        } else if (type == double.class) {
            return Double.class;
        } else {
            return type;
        }
    }
    private static final Map<String, String> BASE_TYPE_ARRAY_PRE_MAP;

    static {
        Map<String, String> array_pre = new HashMap<String, String>();

        array_pre.put("void", "V");
        array_pre.put("boolean", "Z");
        array_pre.put("byte", "B");
        array_pre.put("char", "C");
        array_pre.put("double", "D");
        array_pre.put("float", "F");
        array_pre.put("int", "I");
        array_pre.put("long", "J");
        array_pre.put("short", "S");

        BASE_TYPE_ARRAY_PRE_MAP = array_pre;
    }

    public static Class<?> getClassByName(String pureName, int arrayDepth) throws ClassNotFoundException {
        if (arrayDepth <= 0) {
            return getClassByName(pureName);
        } else {
            String shortName = BASE_TYPE_ARRAY_PRE_MAP.get(pureName);
            StringBuilder sb;
            if (shortName == null) {
                sb = new StringBuilder(pureName.length() + 2 + arrayDepth);
                int i = arrayDepth;
                while (i-- > 0) {
                    sb.append('[');
                }
                sb.append('L').append(pureName).append(';');
            } else {
                sb = new StringBuilder(arrayDepth + 1);
                int i = arrayDepth;
                while (i-- > 0) {
                    sb.append('[');
                }
                sb.append(shortName);
            }
            return getClassByName(sb.toString());
        }
    }

    public static Class<?> getClassByName(String name) throws ClassNotFoundException {
        return Class.forName(name, true, Thread.currentThread().getContextClassLoader());
    }

    public static Method searchMethod(Class<?> currentClass, String name, Class<?>[] parameterTypes, boolean boxed) throws NoSuchMethodException {
        if (currentClass == null) {
            throw new NoSuchMethodException("class == null");
        }
        try {
            return currentClass.getMethod(name, parameterTypes);
        } catch (NoSuchMethodException e) {
            Method likeMethod = null;
            for (Method method : currentClass.getMethods()) {
                if (method.getName().equals(name)
                        && parameterTypes.length == method.getParameterTypes().length
                        && Modifier.isPublic(method.getModifiers())) {
                    if (parameterTypes.length > 0) {
                        Class<?>[] types = method.getParameterTypes();
                        boolean eq = true;
                        boolean like = true;
                        for (int i = 0; i < parameterTypes.length; i++) {
                            Class<?> type = types[i];
                            Class<?> parameterType = parameterTypes[i];
                            if (type != null && parameterType != null
                                    && !type.equals(parameterType)) {
                                eq = false;
                                if (boxed) {
                                    type = ReflectUtil.getBoxedClass(type);
                                    parameterType = ReflectUtil.getBoxedClass(parameterType);
                                }
                                if (!type.isAssignableFrom(parameterType)) {
                                    eq = false;
                                    like = false;
                                    break;
                                }
                            }
                        }
                        if (!eq) {
                            if (like && (likeMethod == null || likeMethod.getParameterTypes()[0]
                                    .isAssignableFrom(method.getParameterTypes()[0]))) {
                                likeMethod = method;
                            }
                            continue;
                        }
                    }
                    return method;
                }
            }
            if (likeMethod != null) {
                return likeMethod;
            }
            throw e;
        }
    }
}
