// Copyright (c) 2013-2015, Webit Team. All Rights Reserved.
package webit.script.loaders.impl;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import webit.script.Engine;
import webit.script.Init;
import webit.script.loaders.Loader;
import webit.script.loaders.Resource;
import webit.script.util.ArrayUtil;
import webit.script.util.StringUtil;

/**
 *
 * @since 1.4.0
 * @author zqq90
 */
public class RouteLoader implements Loader {

    protected String loaders;
    protected Loader defaultLoader;
    
    protected LoaderEntry[] entrys;
    protected String[] rules;

    @Init
    public void init(final Engine engine) {
        try {
            //init Route rules
            final String[] raws = StringUtil.toArray(this.loaders);
            final int size = raws.length;
            final String[] prefixes = new String[size];
            final Map<String, LoaderEntry> loaderMap = new HashMap<>();
            for (int i = 0; i < size; i++) {
                final String raw = raws[i];
                final int index = raw.indexOf(' ');
                if (index < 0) {
                    throw new RuntimeException("Illegal rule: ".concat(raw));
                }
                final String rule = prefixes[i] = raw.substring(0, index);
                loaderMap.put(rule, new LoaderEntry(rule,
                        (Loader) engine.get(raw.substring(index + 1).trim())));
            }
            Arrays.sort(prefixes);
            ArrayUtil.invert(prefixes);
            final LoaderEntry[] loaderEntrys = new LoaderEntry[size];
            for (int i = 0; i < size; i++) {
                loaderEntrys[i] = loaderMap.get(prefixes[i]);
            }
            this.rules = prefixes;
            this.entrys = loaderEntrys;
            //default Loader
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    protected LoaderEntry getLoaderEntry(String resourceName) {
        final String[] prefixes = this.rules;
        for (int i = 0, len = prefixes.length; i < len; i++) {
            if (resourceName.startsWith(prefixes[i])) {
                return this.entrys[i];
            }
        }
        return null;
    }

    @Override
    public Resource get(String name) {
        final LoaderEntry entry;
        if ((entry = getLoaderEntry(name)) != null) {
            return entry.get(name);
        }
        return this.defaultLoader.get(name);
    }

    @Override
    public String concat(String parent, String name) {
        final LoaderEntry entry;
        final LoaderEntry parentEntry;
        if ((entry = getLoaderEntry(name)) != null) {
            return entry.normalize(name);
        }
        if ((parentEntry = getLoaderEntry(parent)) != null) {
            return parentEntry.concat(parent, name);
        }
        return this.defaultLoader.concat(parent, name);
    }

    @Override
    public String normalize(String name) {
        final LoaderEntry entry;
        if ((entry = getLoaderEntry(name)) != null) {
            return entry.normalize(name);
        }
        return this.defaultLoader.normalize(name);
    }

    @Override
    public boolean isEnableCache(String name) {
        final LoaderEntry entry;
        if ((entry = getLoaderEntry(name)) != null) {
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
