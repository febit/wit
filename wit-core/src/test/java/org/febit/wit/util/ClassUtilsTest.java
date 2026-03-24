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

import static org.febit.wit.util.ClassUtils.loadByName;
import static org.junit.jupiter.api.Assertions.*;

class ClassUtilsTest {

    @Test
    void testToBoxed() {
        assertSame(Integer.class, ClassUtils.toBoxed(int.class));
        assertSame(Boolean.class, ClassUtils.toBoxed(boolean.class));
        assertSame(Long.class, ClassUtils.toBoxed(long.class));
        assertSame(Float.class, ClassUtils.toBoxed(float.class));
        assertSame(Double.class, ClassUtils.toBoxed(double.class));
        assertSame(Byte.class, ClassUtils.toBoxed(byte.class));
        assertSame(Character.class, ClassUtils.toBoxed(char.class));
        assertSame(Short.class, ClassUtils.toBoxed(short.class));
        assertSame(Void.class, ClassUtils.toBoxed(void.class));

        assertNull(ClassUtils.toBoxed(String.class));
    }

    @Test
    void testFindPrimitiveClass() {
        assertSame(int.class, ClassUtils.findPrimitiveClass("int"));
        assertSame(boolean.class, ClassUtils.findPrimitiveClass("boolean"));
        assertSame(long.class, ClassUtils.findPrimitiveClass("long"));
        assertSame(float.class, ClassUtils.findPrimitiveClass("float"));
        assertSame(double.class, ClassUtils.findPrimitiveClass("double"));
        assertSame(byte.class, ClassUtils.findPrimitiveClass("byte"));
        assertSame(char.class, ClassUtils.findPrimitiveClass("char"));
        assertSame(short.class, ClassUtils.findPrimitiveClass("short"));
        assertSame(void.class, ClassUtils.findPrimitiveClass("void"));

        assertNull(ClassUtils.findPrimitiveClass(null));
        assertNull(ClassUtils.findPrimitiveClass("java.lang.String"));
    }

    @Test
    void testIsVoidType() {
        assertTrue(ClassUtils.isVoidType(void.class));
        assertTrue(ClassUtils.isVoidType(Void.class));
        assertFalse(ClassUtils.isVoidType(int.class));
    }

    @Test
    void testLoadByName() throws ClassNotFoundException {

        assertThrows(UncheckedException.class, () -> loadByName("class.not.exists", 0));
        assertThrows(IllegalArgumentException.class, () -> loadByName("int", -1));

        assertSame(void.class, loadByName("void", 0));

        assertSame(boolean.class, loadByName("boolean", 0));
        assertSame(boolean[].class, loadByName("boolean", 1));
        assertSame(boolean[][].class, loadByName("boolean", 2));

        assertSame(byte.class, loadByName("byte", 0));
        assertSame(byte[].class, loadByName("byte", 1));
        assertSame(byte[][].class, loadByName("byte", 2));

        assertSame(char.class, loadByName("char", 0));
        assertSame(char[].class, loadByName("char", 1));
        assertSame(char[][].class, loadByName("char", 2));

        assertSame(short.class, loadByName("short", 0));
        assertSame(short[].class, loadByName("short", 1));
        assertSame(short[][].class, loadByName("short", 2));

        assertSame(int.class, loadByName("int", 0));
        assertSame(int[].class, loadByName("int", 1));
        assertSame(int[][].class, loadByName("int", 2));

        assertSame(long.class, loadByName("long", 0));
        assertSame(long[].class, loadByName("long", 1));
        assertSame(long[][].class, loadByName("long", 2));

        assertSame(float.class, loadByName("float", 0));
        assertSame(float[].class, loadByName("float", 1));
        assertSame(float[][].class, loadByName("float", 2));

        assertSame(double.class, loadByName("double", 0));
        assertSame(double[].class, loadByName("double", 1));
        assertSame(double[][].class, loadByName("double", 2));

        assertSame(Map.class, loadByName("java.util.Map", 0));
        assertSame(Map[].class, loadByName("java.util.Map", 1));
        assertSame(Map[][].class, loadByName("java.util.Map", 2));
    }
}
