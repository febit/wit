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
package org.febit.wit.extern.asm;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class AsmUtilsTest {

    static String internalNameOf(final Class<?> c) {
        return AsmUtils.internalNameOf(c.getName());
    }

    @Test
    void internalNameOf() {
        assertEquals("java/lang/Integer", internalNameOf(Integer.class));
        assertEquals("java/lang/String", internalNameOf(String.class));
        assertEquals("java/util/Map", internalNameOf(Map.class));

        assertEquals("int", internalNameOf(int.class));
        assertEquals("boolean", internalNameOf(boolean.class));
        assertEquals("void", internalNameOf(Void.TYPE));
        assertEquals("java/lang/Void", internalNameOf(Void.class));

        assertEquals("[I", internalNameOf(int[].class));
        assertEquals("[[I", internalNameOf(int[][].class));
        assertEquals("[[[I", internalNameOf(int[][][].class));

        assertEquals("[Ljava/lang/Integer;", internalNameOf(Integer[].class));
        assertEquals("[[Ljava/lang/Integer;", internalNameOf(Integer[][].class));
        assertEquals("[[[Ljava/lang/Integer;", internalNameOf(Integer[][][].class));

    }

    @Test
    void testBoxedInternalNameOf() {
        assertEquals("java/lang/Integer", AsmUtils.boxedInternalNameOf(Integer.class));
        assertEquals("java/lang/String", AsmUtils.boxedInternalNameOf(String.class));
        assertEquals("java/util/Map", AsmUtils.boxedInternalNameOf(Map.class));

        assertEquals("java/lang/Integer", AsmUtils.boxedInternalNameOf(int.class));
        assertEquals("java/lang/Boolean", AsmUtils.boxedInternalNameOf(boolean.class));
        assertEquals("java/lang/Void", AsmUtils.boxedInternalNameOf(Void.TYPE));
        assertEquals("java/lang/Void", AsmUtils.boxedInternalNameOf(Void.class));

        assertEquals("[I", AsmUtils.boxedInternalNameOf(int[].class));
        assertEquals("[[I", AsmUtils.boxedInternalNameOf(int[][].class));
        assertEquals("[[[I", AsmUtils.boxedInternalNameOf(int[][][].class));
    }

    @Test
    void descriptorOf() {

        assertEquals("I", AsmUtils.descriptorOf(int.class));
        assertEquals("J", AsmUtils.descriptorOf(long.class));
        assertEquals("Z", AsmUtils.descriptorOf(boolean.class));
        assertEquals("B", AsmUtils.descriptorOf(byte.class));
        assertEquals("C", AsmUtils.descriptorOf(char.class));

        assertEquals("Ljava/lang/Integer;", AsmUtils.descriptorOf(Integer.class));
        assertEquals("Ljava/lang/String;", AsmUtils.descriptorOf(String.class));
        assertEquals("Ljava/util/Map;", AsmUtils.descriptorOf(Map.class));

        assertEquals("[I", AsmUtils.descriptorOf(int[].class));
        assertEquals("[[I", AsmUtils.descriptorOf(int[][].class));
        assertEquals("[[[I", AsmUtils.descriptorOf(int[][][].class));

        assertEquals("[Ljava/lang/Integer;", AsmUtils.descriptorOf(Integer[].class));
        assertEquals("[[Ljava/lang/Integer;", AsmUtils.descriptorOf(Integer[][].class));
        assertEquals("[[[Ljava/lang/Integer;", AsmUtils.descriptorOf(Integer[][][].class));
    }

}
