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

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import org.febit.wit.exception.NoSuchSourceException;
import org.febit.wit.io.Loader;
import org.febit.wit.io.Out;
import org.febit.wit.io.codec.CodecFactory;
import org.febit.wit.io.out.OutputStreamOut;
import org.febit.wit.io.out.WriterOut;
import org.febit.wit.parser.NativeLayout;
import org.febit.wit.parser.TemplateTextFactory;
import org.febit.wit.runtime.accessor.AccessorFactory;
import org.febit.wit.runtime.heap.GlobalHeaps;
import org.jspecify.annotations.Nullable;

import java.io.OutputStream;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static org.febit.wit.util.Defaults.nvl;

/**
 * Wit script engine.
 */
@lombok.Builder(
        access = AccessLevel.PACKAGE,
        builderClassName = "InternalBuilder",
        builderMethodName = "internalBuilder"
)
@Accessors(fluent = true)
@RequiredArgsConstructor
public class Wit {

    private final ConcurrentMap<String, Script> cachedScripts = new ConcurrentHashMap<>();

    @Getter
    private final GlobalHeaps globals = new GlobalHeaps();

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
    private final TemplateTextFactory templateTextFactory;
    @Getter
    private final CodecFactory codecFactory;
    @Getter
    private final NativeLayout nativeLayout;

    public static WitBuilder builder() {
        return new WitBuilder();
    }

    public boolean isEnabled(final Feature feature) {
        return feature.isEnabled(this.features);
    }

    /**
     * Get script by sibling path.
     *
     * @param refer    script's refer path
     * @param relative script's relative path
     * @return Script
     * @throws NoSuchSourceException if source not found
     */
    public Script script(@Nullable String refer, String relative) throws NoSuchSourceException {
        var path = this.loader.sibling(refer, relative);
        if (path == null) {
            throw new NoSuchSourceException(
                    "Illegal script path: sibling of " + refer + " and " + relative);
        }
        return script(path);
    }

    /**
     * Get script by path.
     *
     * @param path script's path
     * @return Script
     * @throws NoSuchSourceException if source not found
     */
    public Script script(String path) throws NoSuchSourceException {
        var script = this.cachedScripts.get(path);
        if (script != null) {
            return script;
        }
        return loadScriptIfAbsent(path);
    }

    public Out asOut(Writer writer) {
        return asOut(writer, null);
    }

    public Out asOut(Writer writer, @Nullable Charset charset) {
        return new WriterOut(writer, nvl(charset, this.charset), codecFactory);
    }

    public Out asOut(OutputStream output) {
        return asOut(output, null);
    }

    public Out asOut(OutputStream output, @Nullable Charset charset) {
        return new OutputStreamOut(output, nvl(charset, this.charset), codecFactory);
    }

    private Script loadScriptIfAbsent(String path) throws NoSuchSourceException {
        var myLoader = this.loader;
        var normalized = myLoader.normalize(path);
        if (normalized == null) {
            //if normalized-path is null means not found source.
            throw new NoSuchSourceException("Illegal source path: " + path);
        }
        var script = this.cachedScripts.get(normalized);
        if (script != null) {
            return script;
        }
        // then create script
        script = new ScriptImpl(this, normalized, myLoader.get(normalized));
        if (myLoader.isCacheEnabled(normalized)) {
            var present = this.cachedScripts.putIfAbsent(normalized, script);
            // Use the present script if exists
            if (present != null) {
                script = present;
            }
        }
        return script;
    }

}
