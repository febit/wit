package org.febit.wit;

import lombok.extern.slf4j.Slf4j;
import org.febit.wit.exception.SourceNotFoundException;
import org.febit.wit.io.DiscardOut;
import org.febit.wit.io.codec.CodecFactory;
import org.febit.wit.io.codec.DefaultCodecFactory;
import org.febit.wit.loader.Loader;
import org.febit.wit.parser.NativeFactory;
import org.febit.wit.parser.TemplateTextFactory;
import org.febit.wit.parser.security.NoneNativeSecurity;
import org.febit.wit.parser.template.AdaptiveTemplateTextFactory;
import org.febit.wit.runtime.accessor.Accessor;
import org.febit.wit.runtime.accessor.ComposedAccessorFactory;
import org.jspecify.annotations.Nullable;

import java.io.UncheckedIOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;

@Slf4j
public class EngineBuilder {

    private static final int FEATURE_DEFAULTS = Feature.collectFeatureDefaults();

    private final List<EngineModule> modules = new ArrayList<>();
    private final List<String> initScripts = new ArrayList<>();
    private final Set<String> predefinedVars = new HashSet<>();

    private final ComposedAccessorFactory.Builder accessorFactory = ComposedAccessorFactory.builder();

    private int features = FEATURE_DEFAULTS;

    private Charset charset = StandardCharsets.UTF_8;
    private CodecFactory codecFactory = new DefaultCodecFactory();
    private NativeFactory nativeFactory = new NativeFactory(new NoneNativeSecurity());
    private TemplateTextFactory templateTextFactory = new AdaptiveTemplateTextFactory();

    @Nullable
    private Loader loader;

    public EngineBuilder predefinedVars(String... vars) {
        this.predefinedVars.addAll(List.of(vars));
        return this;
    }

    public EngineBuilder predefinedVars(Collection<String> vars) {
        this.predefinedVars.addAll(vars);
        return this;
    }

    public EngineBuilder initScripts(String... scripts) {
        this.initScripts.addAll(Arrays.asList(scripts));
        return this;
    }

    public EngineBuilder initScripts(Collection<String> scripts) {
        this.initScripts.addAll(scripts);
        return this;
    }

    public EngineBuilder configureAccessors(Consumer<ComposedAccessorFactory.Builder> consumer) {
        Objects.requireNonNull(consumer);
        consumer.accept(this.accessorFactory);
        return this;
    }

    public EngineBuilder loader(Loader loader) {
        Objects.requireNonNull(loader);
        this.loader = loader;
        return this;
    }

    public EngineBuilder templateTextFactory(TemplateTextFactory factory) {
        Objects.requireNonNull(factory);
        this.templateTextFactory = factory;
        return this;
    }

    public EngineBuilder codecFactory(CodecFactory factory) {
        Objects.requireNonNull(factory);
        this.codecFactory = factory;
        return this;
    }

    public EngineBuilder nativeFactory(NativeFactory factory) {
        Objects.requireNonNull(factory);
        this.nativeFactory = factory;
        return this;
    }

    public <T> EngineBuilder accessor(Class<T> type, Accessor<? extends T> accessor) {
        this.accessorFactory.accessor(type, accessor);
        return this;
    }

    public EngineBuilder enable(Feature feature) {
        this.features = feature.enable(this.features);
        return this;
    }

    public EngineBuilder disable(Feature feature) {
        this.features = feature.disable(this.features);
        return this;
    }

    public EngineBuilder charset(Charset charset) {
        this.charset = charset;
        return this;
    }

    public EngineBuilder modules(List<EngineModule> modules) {
        this.modules.addAll(modules);
        return this;
    }

    public EngineBuilder modules(EngineModule... modules) {
        return modules(Arrays.asList(modules));
    }

    public EngineBuilder module(EngineModule module) {
        this.modules.add(module);
        return this;
    }

    public Engine build() {
        if (this.loader == null) {
            throw new IllegalArgumentException("Loader is not provided.");
        }

        var accessors = this.accessorFactory.build();

        var vars = new ArrayList<>(predefinedVars);
        vars.sort(String::compareTo);

        var engine = Engine.internalBuilder()
                .predefinedVars(List.copyOf(vars))
                .features(features)
                .charset(charset)
                .loader(loader)
                .accessors(accessors)
                .codecFactory(codecFactory)
                .nativeFactory(nativeFactory)
                .templateTextFactory(templateTextFactory)
                .build();

        for (var module : modules) {
            module.apply(engine);
        }
        try {
            initScripts(engine, initScripts);
        } catch (SourceNotFoundException e) {
            throw new UncheckedIOException(e.getMessage(), e);
        }
        return engine;
    }

    private static void initScripts(Engine engine, List<String> scripts) throws SourceNotFoundException {
        var fixed = scripts.stream()
                .distinct()
                .toList();

        if (fixed.isEmpty()) {
            log.info("[INIT] no init scripts to apply.");
            return;
        }
        log.info("[INIT] applying init scripts: total={}", fixed.size());

        var total = fixed.size();
        var staticHeaps = engine.staticHeaps();

        for (int i = 0; i < total; i++) {
            var tmpl = fixed.get(i);
            log.info("[INIT] applying init scripts [{}/{}]: {}", i + 1, total, tmpl);
            engine.script(tmpl).eval(acceptor -> {
                acceptor.set("GLOBAL", staticHeaps.variant());
                acceptor.set("CONST", staticHeaps.constant());
            }, new DiscardOut());
        }
    }

}
