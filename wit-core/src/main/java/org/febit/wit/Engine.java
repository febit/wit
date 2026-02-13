// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.febit.wit.accessor.AccessorFactory;
import org.febit.wit.core.NativeFactory;
import org.febit.wit.core.TextStatementFactory;
import org.febit.wit.exceptions.ResourceNotFoundException;
import org.febit.wit.io.codec.CodecFactory;
import org.febit.wit.loaders.Loader;
import org.febit.wit.runtime.heap.GlobalHeap;
import org.jspecify.annotations.Nullable;

import java.nio.charset.Charset;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Script engine.
 */
@Slf4j
@lombok.Builder(
        access = AccessLevel.PACKAGE,
        builderClassName = "InternalBuilder",
        builderMethodName = "internalBuilder"
)
@Accessors(fluent = true)
@RequiredArgsConstructor
public class Engine {

    private final ConcurrentMap<String, Template> cachedTemplates = new ConcurrentHashMap<>();

    @Getter
    private final GlobalHeap globalHeap = new GlobalHeap();

    @Getter
    private final int features;
    @Getter
    private final Charset charset;
    @Getter
    private final Loader loader;
    @Getter
    private final AccessorFactory accessors;
    @Getter
    private final List<String> predefinedVars;
    @Getter
    private final TextStatementFactory textStatementFactory;
    @Getter
    private final CodecFactory codecFactory;
    @Getter
    private final NativeFactory nativeFactory;

    public static EngineBuilder builder() {
        return new EngineBuilder();
    }

    public boolean isEnabled(final Feature feature) {
        return feature.isEnabled(this.features);
    }

    /**
     * Get template by refer and relative path.
     *
     * @param refer template's refer path
     * @param path  template's relative path
     * @return Template
     * @throws ResourceNotFoundException if resource not found
     */
    public Template template(@Nullable String refer, String path) throws ResourceNotFoundException {
        var finalPath = this.loader.sibling(refer, path);
        if (finalPath == null) {
            throw new ResourceNotFoundException(
                    "Illegal template path: sibling of "
                            + refer + " and " + path
            );
        }
        return template(finalPath);
    }

    /**
     * Get template by path.
     *
     * @param path template's path
     * @return Template
     * @throws ResourceNotFoundException if resource not found
     */
    public Template template(String path) throws ResourceNotFoundException {
        var template = this.cachedTemplates.get(path);
        if (template != null) {
            return template;
        }
        return createTemplateIfAbsent(path);
    }

    private Template createTemplateIfAbsent(String path) throws ResourceNotFoundException {
        var myLoader = this.loader;
        var normalized = myLoader.normalize(path);
        if (normalized == null) {
            //if normalized-path is null means not found resource.
            throw new ResourceNotFoundException("Illegal template path: " + path);
        }
        Template template;
        template = this.cachedTemplates.get(normalized);
        if (template != null) {
            return template;
        }
        // then create Template
        template = new Template(this, normalized, myLoader.get(normalized));
        if (myLoader.isCacheEnabled(normalized)) {
            Template oldTemplate = this.cachedTemplates.putIfAbsent(normalized, template);
            // if old Template exist, use the old one
            if (oldTemplate != null) {
                template = oldTemplate;
            }
        }
        return template;
    }

}
