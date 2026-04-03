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
import lombok.Setter;
import lombok.experimental.Accessors;
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

import static org.febit.wit.util.Defaults.nvl;

@Slf4j
@Accessors(fluent = true, chain = true)
public class WitBuilder {

    private static final int FEATURE_DEFAULTS = Feature.collectFeatureDefaults();

    private final List<WitModule> modules = new ArrayList<>();
    private final List<String> setupScripts = new ArrayList<>();
    private final Set<String> predefinedVars = new HashSet<>();

    private final ComposedAccessorFactory.Builder accessorFactory = ComposedAccessorFactory.builder();

    private int features = FEATURE_DEFAULTS;

    @Getter
    @Setter
    @Nullable
    private Charset charset;
    @Getter
    @Setter
    @Nullable
    private CodecFactory codecFactory;
    @Getter
    @Setter
    @Nullable
    private NativeLayout nativeLayout;
    @Getter
    @Setter
    @Nullable
    private TemplateTextFactory templateTextFactory;
    @Getter
    @Setter
    @Nullable
    private Loader loader;

    public WitBuilder predefinedVars(String... vars) {
        return predefinedVars(List.of(vars));
    }

    public WitBuilder predefinedVars(Collection<String> vars) {
        this.predefinedVars.addAll(vars);
        return this;
    }

    public WitBuilder setup(String... scripts) {
        return setup(Arrays.asList(scripts));
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
                .loader(loader)
                .accessors(accessors)
                .charset(nvl(charset, StandardCharsets.UTF_8))
                .codecFactory(nvl(codecFactory, DefaultCodecFactory::new))
                .nativeLayout(nvl(nativeLayout, NativeLayout::ofDefault))
                .templateTextFactory(nvl(templateTextFactory, AdaptiveTemplateTextFactory::new))
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
