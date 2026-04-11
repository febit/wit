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
package org.febit.wit;

import org.febit.wit.extern.asm.AsmBeanAccessor;
import org.febit.wit.extern.asm.AsmBeanAccessorFactory;
import org.febit.wit.io.CodecFactory;
import org.febit.wit.io.Loader;
import org.febit.wit.io.codec.DefaultCodecFactory;
import org.febit.wit.parser.NativeLayout;
import org.febit.wit.parser.TemplateTextFactory;
import org.febit.wit.parser.template.AdaptiveTemplateTextFactory;
import org.febit.wit.runtime.accessor.CachingAccessorFactory;
import org.febit.wit.runtime.accessor.CompositeAccessorFactory;
import org.febit.wit.runtime.accessor.Getter;
import org.febit.wit.runtime.accessor.Setter;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class WitBuilderTest {

    @Test
    void loaderMustProvided() {
        var builder = new WitBuilder();
        var ex = assertThrows(IllegalArgumentException.class, builder::build);
        assertEquals("Loader is not provided.", ex.getMessage());
    }

    @Test
    void defaults() {
        var loader = mock(Loader.class);
        var wit = new WitBuilder()
                .loader(loader)
                .build();

        assertNotNull(wit);
        assertNotNull(wit.globals());

        assertInstanceOf(CachingAccessorFactory.class, wit.accessors());
        assertInstanceOf(DefaultCodecFactory.class, wit.codecFactory());
        assertInstanceOf(AdaptiveTemplateTextFactory.class, wit.templateTextFactory());
        assertInstanceOf(NativeLayout.class, wit.nativeLayout());

        assertTrue(wit.predefinedVars().isEmpty());
        assertEquals(StandardCharsets.UTF_8, wit.charset());

        for (var f : Feature.values()) {
            assertEquals(f.isEnabledByDefault(), wit.isEnabled(f));
        }
    }

    public record Foo(String name) {

        interface FooAccessor extends Getter<Foo>, Setter<Foo> {

        }
    }

    public record Bar(String name) {

        interface BarAccessor extends Getter<Bar>, Setter<Bar> {

        }
    }

    public record Baz(String name) {
    }

    @Test
    void accessors() {
        var fooAccessor = mock(Foo.FooAccessor.class);
        var barAccessor = mock(Bar.BarAccessor.class);

        var wit = new WitBuilder()
                .loader(mock(Loader.class))
                .accessor(Foo.class, fooAccessor)
                .configureAccessors(builder -> {
                    builder.accessor(Bar.class, barAccessor);
                    builder.fallback(AsmBeanAccessorFactory.get());
                })
                .build();

        var accessors = wit.accessors();
        assertInstanceOf(CachingAccessorFactory.class, accessors);
        assertInstanceOf(CompositeAccessorFactory.class, ((CachingAccessorFactory) accessors).delegate());

        assertSame(fooAccessor, accessors.getter(Foo.class));
        assertSame(fooAccessor, accessors.setter(Foo.class));
        assertSame(barAccessor, accessors.getter(Bar.class));
        assertSame(barAccessor, accessors.setter(Bar.class));
        assertInstanceOf(AsmBeanAccessor.class, accessors.getter(Baz.class));
    }

    @Test
    void components() {
        var loader = mock(Loader.class);
        var templateTextFactory = mock(TemplateTextFactory.class);
        var codecFactory = mock(CodecFactory.class);
        var nativeLayout = mock(NativeLayout.class);

        var wit = new WitBuilder()
                .loader(loader)
                .codecFactory(codecFactory)
                .nativeLayout(nativeLayout)
                .templateTextFactory(templateTextFactory)
                .charset(StandardCharsets.UTF_16BE)
                .predefinedVars("a", "b")
                .predefinedVars(List.of("x", "y"))
                .enable(Feature.LOOSE_SEMICOLON)
                .disable(Feature.TRIM_CODE_BLOCK_BLANK_LINE)
                .build();

        assertSame(loader, wit.loader());
        assertSame(codecFactory, wit.codecFactory());
        assertSame(nativeLayout, wit.nativeLayout());
        assertSame(templateTextFactory, wit.templateTextFactory());

        assertEquals(StandardCharsets.UTF_16BE, wit.charset());
        assertEquals(List.of("a", "b", "x", "y"), wit.predefinedVars());
        assertTrue(wit.isEnabled(Feature.LOOSE_SEMICOLON));
        assertFalse(wit.isEnabled(Feature.TRIM_CODE_BLOCK_BLANK_LINE));
        assertTrue(wit.isEnabled(Feature.SHARE_ROOT_PARAMS));
    }

}
