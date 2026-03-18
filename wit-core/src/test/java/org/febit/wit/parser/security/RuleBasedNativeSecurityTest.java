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
package org.febit.wit.parser.security;

import org.junit.jupiter.api.Test;

import static org.febit.wit.parser.security.RuleBasedNativeSecurity.ROOT_PATH;
import static org.junit.jupiter.api.Assertions.*;

class RuleBasedNativeSecurityTest {

    @Test
    void test() {

        var builder = RuleBasedNativeSecurity.builder()
                .deny("a")
                .allow("a")
                .allow("b")
                .deny("b")
                .allow("c")
                .deny("c.d")
                .allow("c.d.e.f")
                .deny("c.d.e.f.g");

        var security = builder.build();

        assertFalse(security.allowed(ROOT_PATH));
        assertFalse(security.allowed("x"));
        assertFalse(security.allowed("x.yz"));

        assertFalse(security.allowed("a"));
        assertFalse(security.allowed("a.aa"));
        assertFalse(security.allowed("a.aa.aaa"));

        assertFalse(security.allowed("b"));
        assertFalse(security.allowed("b.b"));
        assertFalse(security.allowed("b.c"));

        assertTrue(security.allowed("c"));
        assertTrue(security.allowed("c.cc"));
        assertTrue(security.allowed("c.cc.ccc"));
        assertFalse(security.allowed("c.d"));
        assertFalse(security.allowed("c.d.e"));
        assertTrue(security.allowed("c.d.e.f"));
        assertFalse(security.allowed("c.d.e.f.g"));
        assertFalse(security.allowed("c.d.e.f.g.h"));

        security = builder.allowRoot().build();
        assertTrue(security.allowed(ROOT_PATH));
        assertTrue(security.allowed("x"));
        assertTrue(security.allowed("x.yz"));
        assertFalse(security.allowed("a"));
        assertFalse(security.allowed("a.aa"));
        assertFalse(security.allowed("a.aa.aaa"));
        assertTrue(security.allowed("c"));

        security = builder.denyRoot().build();
        assertFalse(security.allowed(ROOT_PATH));
        assertFalse(security.allowed("x"));
        assertFalse(security.allowed("x.yz"));
        assertFalse(security.allowed("a"));
        assertFalse(security.allowed("a.aa"));
        assertFalse(security.allowed("a.aa.aaa"));
        assertTrue(security.allowed("c"));

    }
}
