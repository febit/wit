// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.febit.wit.core.NativeFactory;
import org.febit.wit.core.TextStatementFactory;
import org.febit.wit.exception.SourceNotFoundException;
import org.febit.wit.io.codec.CodecFactory;
import org.febit.wit.loaders.Loader;
import org.febit.wit.runtime.ScriptImpl;
import org.febit.wit.runtime.accessor.AccessorFactory;
import org.febit.wit.runtime.heap.StaticHeaps;
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

    private final ConcurrentMap<String, Script> cachedScripts = new ConcurrentHashMap<>();

    @Getter
    private final StaticHeaps staticHeaps = new StaticHeaps();

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
     * Get script by refer and relative path.
     *
     * @param refer script's refer path
     * @param path  script's relative path
     * @return Script
     * @throws SourceNotFoundException if source not found
     */
    public Script script(@Nullable String refer, String path) throws SourceNotFoundException {
        var finalPath = this.loader.sibling(refer, path);
        if (finalPath == null) {
            throw new SourceNotFoundException(
                    "Illegal script path: sibling of "
                            + refer + " and " + path
            );
        }
        return script(finalPath);
    }

    /**
     * Get script by path.
     *
     * @param path script's path
     * @return Script
     * @throws SourceNotFoundException if source not found
     */
    public Script script(String path) throws SourceNotFoundException {
        var script = this.cachedScripts.get(path);
        if (script != null) {
            return script;
        }
        return loadScriptIfAbsent(path);
    }

    private Script loadScriptIfAbsent(String path) throws SourceNotFoundException {
        var myLoader = this.loader;
        var normalized = myLoader.normalize(path);
        if (normalized == null) {
            //if normalized-path is null means not found source.
            throw new SourceNotFoundException("Illegal source path: " + path);
        }
        var script = this.cachedScripts.get(normalized);
        if (script != null) {
            return script;
        }
        // then create script
        script = new ScriptImpl(this, normalized, myLoader.get(normalized));
        if (myLoader.isCacheEnabled(normalized)) {
            var oldScript = this.cachedScripts.putIfAbsent(normalized, script);
            // if old script exist, use the old one
            if (oldScript != null) {
                script = oldScript;
            }
        }
        return script;
    }

}
