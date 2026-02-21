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
public class WitBuilder {

    private static final int FEATURE_DEFAULTS = Feature.collectFeatureDefaults();

    private final List<WitModule> modules = new ArrayList<>();
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

    public WitBuilder predefinedVars(String... vars) {
        this.predefinedVars.addAll(List.of(vars));
        return this;
    }

    public WitBuilder predefinedVars(Collection<String> vars) {
        this.predefinedVars.addAll(vars);
        return this;
    }

    public WitBuilder initScripts(String... scripts) {
        this.initScripts.addAll(Arrays.asList(scripts));
        return this;
    }

    public WitBuilder initScripts(Collection<String> scripts) {
        this.initScripts.addAll(scripts);
        return this;
    }

    public WitBuilder configureAccessors(Consumer<ComposedAccessorFactory.Builder> consumer) {
        Objects.requireNonNull(consumer);
        consumer.accept(this.accessorFactory);
        return this;
    }

    public WitBuilder loader(Loader loader) {
        Objects.requireNonNull(loader);
        this.loader = loader;
        return this;
    }

    public WitBuilder templateTextFactory(TemplateTextFactory factory) {
        Objects.requireNonNull(factory);
        this.templateTextFactory = factory;
        return this;
    }

    public WitBuilder codecFactory(CodecFactory factory) {
        Objects.requireNonNull(factory);
        this.codecFactory = factory;
        return this;
    }

    public WitBuilder nativeFactory(NativeFactory factory) {
        Objects.requireNonNull(factory);
        this.nativeFactory = factory;
        return this;
    }

    public <T> WitBuilder accessor(Class<T> type, Accessor<? extends T> accessor) {
        this.accessorFactory.accessor(type, accessor);
        return this;
    }

    public WitBuilder enable(Feature feature) {
        this.features = feature.enable(this.features);
        return this;
    }

    public WitBuilder disable(Feature feature) {
        this.features = feature.disable(this.features);
        return this;
    }

    public WitBuilder charset(Charset charset) {
        this.charset = charset;
        return this;
    }

    public WitBuilder modules(List<WitModule> modules) {
        this.modules.addAll(modules);
        return this;
    }

    public WitBuilder modules(WitModule... modules) {
        return modules(Arrays.asList(modules));
    }

    public WitBuilder module(WitModule module) {
        this.modules.add(module);
        return this;
    }

    public Wit build() {
        if (this.loader == null) {
            throw new IllegalArgumentException("Loader is not provided.");
        }

        var accessors = this.accessorFactory.build();

        var vars = new ArrayList<>(predefinedVars);
        vars.sort(String::compareTo);

        var wit = Wit.internalBuilder()
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
            module.apply(wit);
        }
        try {
            initScripts(wit, initScripts);
        } catch (SourceNotFoundException e) {
            throw new UncheckedIOException(e.getMessage(), e);
        }
        return wit;
    }

    private static void initScripts(Wit wit, List<String> scripts) throws SourceNotFoundException {
        var fixed = scripts.stream()
                .distinct()
                .toList();

        if (fixed.isEmpty()) {
            log.info("[INIT] no init scripts to apply.");
            return;
        }
        log.info("[INIT] applying init scripts: total={}", fixed.size());

        var total = fixed.size();
        var staticHeaps = wit.staticHeaps();

        for (int i = 0; i < total; i++) {
            var tmpl = fixed.get(i);
            log.info("[INIT] applying init scripts [{}/{}]: {}", i + 1, total, tmpl);
            wit.script(tmpl).eval(acceptor -> {
                acceptor.set("GLOBAL", staticHeaps.variables());
                acceptor.set("CONST", staticHeaps.constants());
            }, DiscardOut.get());
        }
    }

}
