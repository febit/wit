// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.loaders.impl;

import org.febit.wit.Engine;
import org.febit.wit.Init;
import org.febit.wit.exceptions.IllegalConfigException;
import org.febit.wit.lang.Resource;
import org.febit.wit.loaders.Loader;
import org.febit.wit.util.StringUtil;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;

/**
 * @author zqq90
 * @since 1.4.0
 */
@SuppressWarnings({
        "WeakerAccess"
})
public class RouteLoader implements Loader {

    protected String loaders;
    protected Loader defaultLoader;

    protected LoaderEntry[] entries;
    protected String[] rules;

    @Init
    public void init(final Engine engine) {
        //init Route rules
        var raws = StringUtil.toArray(this.loaders);
        var size = raws.length;
        var prefixes = new String[size];
        var loaderMap = new HashMap<String, LoaderEntry>();
        for (int i = 0; i < size; i++) {
            var raw = raws[i];
            var index = raw.indexOf(' ');
            if (index < 0) {
                throw new IllegalConfigException("Illegal RouteLoader rule: ".concat(raw));
            }
            var rule = raw.substring(0, index);
            prefixes[i] = rule;
            loaderMap.put(rule, new LoaderEntry(rule,
                    (Loader) engine.get(raw.substring(index + 1).trim())));
        }
        Arrays.sort(prefixes, Comparator.reverseOrder());
        var loaderEntries = new LoaderEntry[size];
        for (int i = 0; i < size; i++) {
            loaderEntries[i] = loaderMap.get(prefixes[i]);
        }
        this.rules = prefixes;
        this.entries = loaderEntries;
        //default Loader
    }

    protected LoaderEntry getLoaderEntry(String resourceName) {
        var prefixes = this.rules;
        for (int i = 0, len = prefixes.length; i < len; i++) {
            if (resourceName.startsWith(prefixes[i])) {
                return this.entries[i];
            }
        }
        return null;
    }

    @Override
    public Resource get(String name) {
        var entry = getLoaderEntry(name);
        if (entry != null) {
            return entry.get(name);
        }
        return this.defaultLoader.get(name);
    }

    @Override
    public String concat(String parent, String name) {
        var entry = getLoaderEntry(name);
        if (entry != null) {
            return entry.normalize(name);
        }
        var parentEntry = getLoaderEntry(parent);
        if (parentEntry != null) {
            return parentEntry.concat(parent, name);
        }
        return this.defaultLoader.concat(parent, name);
    }

    @Override
    public String normalize(String name) {
        var entry = getLoaderEntry(name);
        if (entry != null) {
            return entry.normalize(name);
        }
        return this.defaultLoader.normalize(name);
    }

    @Override
    public boolean isEnableCache(String name) {
        var entry = getLoaderEntry(name);
        if (entry != null) {
            return entry.isEnableCache(name);
        }
        return this.defaultLoader.isEnableCache(name);
    }

    protected static class LoaderEntry {

        private final String prefix;
        private final Loader loader;
        private final int prefixLength;

        public LoaderEntry(String prefix, Loader loader) {
            this.prefix = prefix;
            this.loader = loader;
            this.prefixLength = prefix.length();
        }

        public Resource get(String name) {
            return this.loader.get(name.substring(this.prefixLength));
        }

        public String concat(String parent, String name) {
            return fix(this.loader.concat(parent.substring(this.prefixLength), name));
        }

        public String normalize(String name) {
            return fix(this.loader.normalize(name.substring(this.prefixLength)));
        }

        public String fix(final String name) {
            if (name != null) {
                return this.prefix.concat(name);
            }
            return null;
        }

        private boolean isEnableCache(String name) {
            return this.loader.isEnableCache(name.substring(this.prefixLength));
        }
    }
}
