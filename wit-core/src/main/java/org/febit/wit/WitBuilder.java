package org.febit.wit;

import lombok.extern.slf4j.Slf4j;
import org.febit.wit.exception.NoSuchSourceException;
import org.febit.wit.io.Loader;
import org.febit.wit.io.codec.CodecFactory;
import org.febit.wit.io.codec.DefaultCodecFactory;
import org.febit.wit.io.out.DiscardOut;
import org.febit.wit.parser.NativeLayout;
import org.febit.wit.parser.TemplateTextFactory;
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
    private final List<String> setupScripts = new ArrayList<>();
    private final Set<String> predefinedVars = new HashSet<>();

    private final ComposedAccessorFactory.Builder accessorFactory = ComposedAccessorFactory.builder();

    private int features = FEATURE_DEFAULTS;

    private Charset charset = StandardCharsets.UTF_8;
    private CodecFactory codecFactory = new DefaultCodecFactory();
    private NativeLayout nativeLayout = NativeLayout.ofDefault();
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

    public WitBuilder setup(String... scripts) {
        this.setupScripts.addAll(Arrays.asList(scripts));
        return this;
    }

    public WitBuilder setup(Collection<String> scripts) {
        this.setupScripts.addAll(scripts);
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

    public WitBuilder nativeLayout(NativeLayout factory) {
        Objects.requireNonNull(factory);
        this.nativeLayout = factory;
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
                .nativeLayout(nativeLayout)
                .templateTextFactory(templateTextFactory)
                .build();

        for (var module : modules) {
            module.apply(wit);
        }
        try {
            setup(wit, setupScripts);
        } catch (NoSuchSourceException e) {
            throw new UncheckedIOException(e.getMessage(), e);
        }
        return wit;
    }

    private static void setup(Wit wit, List<String> scripts) throws NoSuchSourceException {
        var fixed = scripts.stream()
                .distinct()
                .toList();

        if (fixed.isEmpty()) {
            log.info("[INIT] Skipping setup scripts, none provided.");
            return;
        }

        var total = fixed.size();
        var staticHeaps = wit.staticHeaps();
        for (int i = 0; i < total; i++) {
            var path = fixed.get(i);
            log.info("[INIT] Applying setup scripts [{}/{}]: {}", i + 1, total, path);
            wit.script(path).eval(acceptor -> {
                acceptor.set(Presets.GLOBAL, staticHeaps.variables());
                acceptor.set(Presets.CONST, staticHeaps.constants());
            }, DiscardOut.get());
        }
        log.info("[INIT] Applied setup scripts, total: {}", total);
    }

}
