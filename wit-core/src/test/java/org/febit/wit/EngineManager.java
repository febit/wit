// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit;

import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.febit.wit.core.NativeFactory;
import org.febit.wit.exceptions.ResourceNotFoundException;
import org.febit.wit.extern.lib.cache.CachingModule;
import org.febit.wit.extern.lib.cache.impl.SimpleCache;
import org.febit.wit.extern.lib.test.AssertionModule;
import org.febit.wit.extern.lib.tld.TldModule;
import org.febit.wit.global.impl.GlobalContextRegister;
import org.febit.wit.global.impl.LocalContextRegister;
import org.febit.wit.loaders.Loader;
import org.febit.wit.loaders.Loaders;
import org.febit.wit.loaders.impl.StringLoader;
import org.febit.wit.security.RuleBasedNativeSecurity;
import org.febit.wit.test.component.TestCasesModule;
import org.febit.wit.test.component.TestConfigFlagEnginePlugin;
import org.febit.wit.test.component.TestSpiFlagEnginePlugin;
import org.junit.jupiter.api.function.Executable;

import java.time.Duration;
import java.util.List;

@Slf4j
public class EngineManager {

    private static final String EXT_WIT = ".wit";
    private static final List<String> EXT_DEPUTIES = List.of(EXT_WIT, ".whtml", ".wit2");

    private static final NativeFactory NATIVE_FACTORY = new NativeFactory(
            RuleBasedNativeSecurity.builder()
                    .allow(
                            "boolean",
                            "byte",
                            "char",
                            "short",
                            "int",
                            "long",
                            "float",
                            "double"
                    )
                    .allow(
                            "java.lang.Object",
                            "java.lang.Boolean",
                            "java.lang.Character",
                            "java.lang.Byte",
                            "java.lang.Short",
                            "java.lang.Integer",
                            "java.lang.Long",
                            "java.lang.Float",
                            "java.lang.Double",
                            "java.lang.String",
                            "java.lang.Number",
                            "java.lang.System.currentTimeMillis",
                            "org.febit.wit.test"
                    )
                    .build()
    );

    @Getter
    @Accessors(fluent = true)
    public static final Engine engine = Engine.builder()
            .loader(loader())
            // .nativeFactory(NATIVE_FACTORY)
            .predefinedVars(
                    "request", "request2",
                    "session", "session2"
            )
            .plugin(new AssertionModule())
            .plugins(
                    GlobalContextRegister.create(),
                    LocalContextRegister.create()
            )
            .plugin(new TestCasesModule())
            .plugin(new TestSpiFlagEnginePlugin())
            .plugin(new TestConfigFlagEnginePlugin())
            .plugin(CachingModule.builder()
                    .using(SimpleCache.ofLru(100))
                    .withClear(true)
                    .withRemove(true)
                    .build()
            )
            .plugin(TldModule.builder()
                    .path("tld-test.tld")
                    .prefix("tld_")
                    .build())
            .initScripts(
                    "/inits/moduleTest-a-1.init.wit",

                    "/inits/moduleTest-a-1.init.wit",
                    "/inits/moduleTest-a-2.init.wit",

                    "/inits/moduleTest-a.init.wit",
                    "/inits/moduleTest-b.init.wit",

                    "/inits/moduleTest-a-1.init.wit",
                    "/inits/moduleTest-a-2.init.wit",

                    "/auto/lib/initTest.init.wit",
                    "/auto/lib/initTest.init2.wit",
                    "/auto/lib/localTest.init.wit"
            )
            .build();

    static Loader loader() {
        var code = StringLoader.builder()
                .codeFirst(true)
                .build();

        var string = StringLoader.builder()
                .codeFirst(false)
                .build();

        var classpath = Loaders.classpath()
                .root("org/febit/wit/test/tmpls")
                .deputySuffixes(EXT_DEPUTIES)
                .missingSuffix(EXT_WIT)
                .build();

        var lib = Loaders.classpath()
                .root("org/febit/wit/test/lib")
                .deputySuffixes(EXT_DEPUTIES)
                .missingSuffix(EXT_WIT)
                .build();

        var libSub = Loaders.classpath()
                .root("org/febit/wit/test/lib-sub")
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

    public static Template template(String name) throws ResourceNotFoundException {
        return engine().template(name);
    }

    public static Executable tmplChecker(String tmpl) {
        return () -> template(tmpl)
                .reload();
    }

    public static Executable codeChecker(String code) {
        return tmplChecker("code: \n" + code);
    }
}
