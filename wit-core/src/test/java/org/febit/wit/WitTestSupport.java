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

import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.febit.wit.exception.NoSuchSourceException;
import org.febit.wit.extern.lib.cache.CachingModule;
import org.febit.wit.extern.lib.cache.impl.SimpleCache;
import org.febit.wit.extern.lib.context.GlobalContextRegistry;
import org.febit.wit.extern.lib.context.LocalContextRegistry;
import org.febit.wit.extern.lib.std.TypesModule;
import org.febit.wit.extern.lib.test.AssertionModule;
import org.febit.wit.extern.lib.tld.TldModule;
import org.febit.wit.extern.servlet.ServletAccessors;
import org.febit.wit.extern.servlet.ServletContextLoader;
import org.febit.wit.io.Loader;
import org.febit.wit.io.Loaders;
import org.febit.wit.io.Source;
import org.febit.wit.test.component.TestCasesModule;
import org.febit.wit.test.component.TestConfigFlagModule;
import org.febit.wit.test.component.TestSpiFlagModule;
import org.junit.jupiter.api.function.Executable;

import java.time.Duration;
import java.util.List;

@Slf4j
public class WitTestSupport {

    private static final String EXT_WIT = ".wit";
    private static final String EXT_WHTML = ".whtml";
    private static final List<String> EXT_DEPUTIES = List.of(EXT_WIT, ".whtml", ".wit2");

    @Getter
    @Accessors(fluent = true)
    public static final Wit WIT = Wit.builder()
            .loader(loader())
            .predefinedVars(
                    "request", "request2",
                    "session", "session2"
            )
            .configureAccessors(ServletAccessors::registerAll)
            .module(new AssertionModule())
            .modules(
                    GlobalContextRegistry.create(),
                    LocalContextRegistry.create()
            )
            .module(new TestCasesModule())
            .module(new TestSpiFlagModule())
            .module(new TestConfigFlagModule())
            .module(new TypesModule())
            .module(CachingModule.builder()
                    .using(SimpleCache.ofLru(100))
                    .withClear(true)
                    .withRemove(true)
                    .build()
            )
            .module(TldModule.builder()
                    .path("tld-test.tld")
                    .prefix("tld_")
                    .build())
            .setup(
                    "/inits/module-a-1.init.wit",

                    "/inits/module-a-1.init.wit",
                    "/inits/module-a-2.init.wit",

                    "/inits/module-a.init.wit",
                    "/inits/module-b.init.wit",

                    "/inits/module-a-1.init.wit",
                    "/inits/module-a-2.init.wit",

                    "/auto/lib/global.init.wit",
                    "/auto/lib/global.init2.wit",
                    "/auto/lib/local.init.wit"
            )
            .build();

    static Loader loader() {
        var code = Loaders.string()
                .beginWith(Source.BeginWith.SCRIPT)
                .build();

        var string = Loaders.string()
                .beginWith(Source.BeginWith.TEMPLATE)
                .build();

        var cachedString = Loaders.string()
                .beginWith(Source.BeginWith.TEMPLATE)
                .cacheEnabled(true)
                .build();

        var classpath = Loaders.classpath()
                .root("org/febit/wit/test/tmpls")
                .beginWith(Source.BeginWith.SCRIPT)
                .candidateSuffixes(EXT_DEPUTIES)
                .completeMissingSuffix(EXT_WIT)
                .build();

        var cachedClasspath = Loaders.classpath()
                .root("org/febit/wit/test/tmpls")
                .beginWith(Source.BeginWith.SCRIPT)
                .cacheEnabled(true)
                .build();

        var lib = Loaders.classpath()
                .root("org/febit/wit/test/lib")
                .beginWith(Source.BeginWith.SCRIPT)
                .candidateSuffixes(EXT_DEPUTIES)
                .completeMissingSuffix(EXT_WIT)
                .build();

        var libSub = Loaders.classpath()
                .root("org/febit/wit/test/lib-sub")
                .beginWith(Source.BeginWith.SCRIPT)
                .candidateSuffixes(EXT_DEPUTIES)
                .completeMissingSuffix(EXT_WIT)
                .build();

        var servlet = ServletContextLoader.builder()
                .context(ServletTestSupport.context())
                .beginWith(Source.BeginWith.TEMPLATE)
                .build();

        var lazyLoader = Loaders.debouncing(classpath, Duration.ofSeconds(10));
        return Loaders.dispatch()
                .rule("code:", code)
                .rule("string:", string)
                .rule("classpath:", classpath)
                .rule("lib-test:", lazyLoader)
                .rule("lib:", lib)
                .rule("lib:sub:", libSub)
                .rule("servlet:", servlet)
                .rule("cached:", cachedClasspath)
                .rule("cached-string:", cachedString)
                .fallback(lazyLoader)
                .build();
    }

    public static Script script(String name) throws NoSuchSourceException {
        return WIT().script(name);
    }

    public static Executable tmplChecker(String tmpl) {
        return () -> script(tmpl)
                .reload();
    }

    public static Executable codeChecker(String code) {
        return tmplChecker("code: \n" + code);
    }

}
