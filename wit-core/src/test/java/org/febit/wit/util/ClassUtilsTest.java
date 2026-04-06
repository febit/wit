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

import org.febit.wit.exception.UncheckedException;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.febit.wit.util.ClassUtils.load;
import static org.junit.jupiter.api.Assertions.*;

class ClassUtilsTest {

    @Test
    void testBoxedType() {
        assertSame(Integer.class, ClassUtils.boxedType(int.class));
        assertSame(Boolean.class, ClassUtils.boxedType(boolean.class));
        assertSame(Long.class, ClassUtils.boxedType(long.class));
        assertSame(Float.class, ClassUtils.boxedType(float.class));
        assertSame(Double.class, ClassUtils.boxedType(double.class));
        assertSame(Byte.class, ClassUtils.boxedType(byte.class));
        assertSame(Character.class, ClassUtils.boxedType(char.class));
        assertSame(Short.class, ClassUtils.boxedType(short.class));
        assertSame(Void.class, ClassUtils.boxedType(void.class));

        assertNull(ClassUtils.boxedType(String.class));
    }

    @Test
    void testPrimitiveType() {
        assertSame(int.class, ClassUtils.primitiveType("int"));
        assertSame(boolean.class, ClassUtils.primitiveType("boolean"));
        assertSame(long.class, ClassUtils.primitiveType("long"));
        assertSame(float.class, ClassUtils.primitiveType("float"));
        assertSame(double.class, ClassUtils.primitiveType("double"));
        assertSame(byte.class, ClassUtils.primitiveType("byte"));
        assertSame(char.class, ClassUtils.primitiveType("char"));
        assertSame(short.class, ClassUtils.primitiveType("short"));
        assertSame(void.class, ClassUtils.primitiveType("void"));

        assertNull(ClassUtils.primitiveType(null));
        assertNull(ClassUtils.primitiveType("java.lang.String"));
    }

    @Test
    void testIsVoidType() {
        assertTrue(ClassUtils.isVoidType(void.class));
        assertTrue(ClassUtils.isVoidType(Void.class));
        assertFalse(ClassUtils.isVoidType(int.class));
    }

    @Test
    void testLoad() throws ClassNotFoundException {

        assertThrows(UncheckedException.class, () -> load("class.not.exists", 0));
        assertThrows(IllegalArgumentException.class, () -> load("int", -1));

        assertSame(void.class, load("void", 0));

        assertSame(boolean.class, load("boolean", 0));
        assertSame(boolean[].class, load("boolean", 1));
        assertSame(boolean[][].class, load("boolean", 2));

        assertSame(byte.class, load("byte", 0));
        assertSame(byte[].class, load("byte", 1));
        assertSame(byte[][].class, load("byte", 2));

        assertSame(char.class, load("char", 0));
        assertSame(char[].class, load("char", 1));
        assertSame(char[][].class, load("char", 2));

        assertSame(short.class, load("short", 0));
        assertSame(short[].class, load("short", 1));
        assertSame(short[][].class, load("short", 2));

        assertSame(int.class, load("int", 0));
        assertSame(int[].class, load("int", 1));
        assertSame(int[][].class, load("int", 2));

        assertSame(long.class, load("long", 0));
        assertSame(long[].class, load("long", 1));
        assertSame(long[][].class, load("long", 2));

        assertSame(float.class, load("float", 0));
        assertSame(float[].class, load("float", 1));
        assertSame(float[][].class, load("float", 2));

        assertSame(double.class, load("double", 0));
        assertSame(double[].class, load("double", 1));
        assertSame(double[][].class, load("double", 2));

        assertSame(Map.class, load("java.util.Map", 0));
        assertSame(Map[].class, load("java.util.Map", 1));
        assertSame(Map[][].class, load("java.util.Map", 2));
    }
}
