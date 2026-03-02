// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit;

import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.febit.wit.exception.NoSuchSourceException;
import org.febit.wit.extern.lib.cache.CachingModule;
import org.febit.wit.extern.lib.cache.impl.SimpleCache;
import org.febit.wit.extern.lib.context.GlobalContextRegister;
import org.febit.wit.extern.lib.context.LocalContextRegister;
import org.febit.wit.extern.lib.test.AssertionModule;
import org.febit.wit.extern.lib.tld.TldModule;
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
public class TestWit {

    private static final String EXT_WIT = ".wit";
    private static final List<String> EXT_DEPUTIES = List.of(EXT_WIT, ".whtml", ".wit2");

    @Getter
    @Accessors(fluent = true)
    public static final Wit WIT = Wit.builder()
            .loader(loader())
            .predefinedVars(
                    "request", "request2",
                    "session", "session2"
            )
            .module(new AssertionModule())
            .modules(
                    GlobalContextRegister.create(),
                    LocalContextRegister.create()
            )
            .module(new TestCasesModule())
            .module(new TestSpiFlagModule())
            .module(new TestConfigFlagModule())
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
            .initScripts(
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

        var classpath = Loaders.classpath()
                .root("org/febit/wit/test/tmpls")
                .beginWith(Source.BeginWith.SCRIPT)
                .deputySuffixes(EXT_DEPUTIES)
                .missingSuffix(EXT_WIT)
                .build();

        var lib = Loaders.classpath()
                .root("org/febit/wit/test/lib")
                .beginWith(Source.BeginWith.SCRIPT)
                .deputySuffixes(EXT_DEPUTIES)
                .missingSuffix(EXT_WIT)
                .build();

        var libSub = Loaders.classpath()
                .root("org/febit/wit/test/lib-sub")
                .beginWith(Source.BeginWith.SCRIPT)
                .deputySuffixes(EXT_DEPUTIES)
                .missingSuffix(EXT_WIT)
                .build();

        var lazyLoader = Loaders.debounce(classpath, Duration.ofSeconds(10));
        return Loaders.dispatcher()
                .rule("code:", code)
                .rule("string:", string)
                .rule("classpath:", classpath)
                .rule("lib-test:", lazyLoader)
                .rule("lib:", lib)
                .rule("lib:sub:", libSub)
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
