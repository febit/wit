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
package org.febit.wit.io.loader;

import org.febit.wit.io.Loaders;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SecurityLoaderDecoratorTest {

    @Test
    void allow() {
        var loader = Loaders.security(
                Loaders.string().build(),
                List.of(
                        "/a/",
                        "/b",
                        "/c/d"
                )
        );

        assertInstanceOf(StringSource.class, loader.get("/a/"));
        assertInstanceOf(StringSource.class, loader.get("/a/x.wit"));

        assertInstanceOf(StringSource.class, loader.get("/b"));
        assertInstanceOf(StringSource.class, loader.get("/b/x.wit"));
        assertInstanceOf(StringSource.class, loader.get("/bcd.wit"));

        assertInstanceOf(StringSource.class, loader.get("/c/d/x.wit"));

        assertInstanceOf(EmptySource.class, loader.get("/"));
        assertInstanceOf(EmptySource.class, loader.get("/a"));
        assertInstanceOf(EmptySource.class, loader.get("/abc"));
        assertInstanceOf(EmptySource.class, loader.get("/a.wit"));
        assertInstanceOf(EmptySource.class, loader.get("//b"));
    }

}
