/*
 * Copyright 2013-present febit.org (support@febit.org)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.febit.wit.util;

import lombok.experimental.UtilityClass;
import org.febit.wit.exception.UncheckedException;
import org.jspecify.annotations.Nullable;

import java.util.Arrays;

@UtilityClass
public class ClassUtils {

    public static String nameOf(@Nullable Object targetObj) {
        return targetObj != null ? targetObj.getClass().getName() : "null";
    }

    public static ClassLoader loader() {
        return Thread.currentThread().getContextClassLoader();
    }

    public static boolean isVoidType(Class<?> cls) {
        return cls == void.class || cls == Void.class;
    }

    @Nullable
    public static Class<?> boxedType(Class<?> type) {
        if (!type.isPrimitive()) {
            return null;
        }
        if (type == int.class) {
            return Integer.class;
        }
        if (type == boolean.class) {
            return Boolean.class;
        }
        if (type == long.class) {
            return Long.class;
        }
        if (type == double.class) {
            return Double.class;
        }
        if (type == float.class) {
            return Float.class;
        }
        if (type == short.class) {
            return Short.class;
        }
        if (type == char.class) {
            return Character.class;
        }
        if (type == byte.class) {
            return Byte.class;
        }
        if (type == void.class) {
            return Void.class;
        }
        throw new IllegalStateException("Unknown primitive type: " + type.getName());
    }

    public static char primitiveTypeCode(final String name) {
        return switch (name) {
            case "int" -> 'I';
            case "long" -> 'J';
            case "short" -> 'S';
            case "boolean" -> 'Z';
            case "char" -> 'C';
            case "double" -> 'D';
            case "float" -> 'F';
            case "byte" -> 'B';
            case "void" -> 'V';
            default -> '\0';
        };
    }

    @Nullable
    public static Class<?> primitiveType(@Nullable final String name) {
        if (name == null) {
            return null;
        }
        return switch (name) {
            case "int" -> int.class;
            case "long" -> long.class;
            case "short" -> short.class;
            case "boolean" -> boolean.class;
            case "char" -> char.class;
            case "double" -> double.class;
            case "float" -> float.class;
            case "byte" -> byte.class;
            case "void" -> void.class;
            default -> null;
        };
    }

    private static Class<?> load0(String name) {
        try {
            return Class.forName(name, true, loader());
        } catch (ClassNotFoundException ex) {
            throw new UncheckedException(ex);
        }
    }

    public static Class<?> load(String name) {
        var cls = primitiveType(name);
        return cls != null ? cls : load0(name);
    }

    public static Class<?> load(String name, int arrayDepth) {
        if (arrayDepth == 0) {
            return load(name);
        }
        char code = primitiveTypeCode(name);
        final char[] chars;
        if (code == '\0') {
            chars = new char[name.length() + 2 + arrayDepth];
            Arrays.fill(chars, 0, arrayDepth, '[');
            chars[arrayDepth] = 'L';
            name.getChars(0, name.length(), chars, arrayDepth + 1);
            chars[chars.length - 1] = ';';
        } else {
            chars = new char[arrayDepth + 1];
            Arrays.fill(chars, 0, arrayDepth, '[');
            chars[arrayDepth] = code;
        }
        return load0(new String(chars));
    }

}
