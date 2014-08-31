// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script.loaders.impl;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import webit.script.Engine;
import webit.script.Initable;
import webit.script.loaders.Loader;
import webit.script.loaders.Resource;
import webit.script.util.ArrayUtil;
import webit.script.util.ClassEntry;
import webit.script.util.StringUtil;

/**
 *
 * @since 1.4.0
 * @author zqq90
 */
public class RouteLoader implements Loader, Initable {

    protected String loaders;
    protected ClassEntry defaultLoader;

    protected Loader _defaultLoader;
    protected LoaderEntry[] entrys;
    protected String[] _rules;

    public void init(final Engine engine) {
        try {
            //init Route rules
            final String[] raws = StringUtil.splitAndRemoveBlank(this.loaders);
            final int size = raws.length;
            final String[] rules = this._rules = new String[size];
            final Map<String, LoaderEntry> loaderMap = new HashMap<String, LoaderEntry>();
            for (int i = 0; i < size; i++) {
                final String raw = raws[i];
                final int index = raw.indexOf(' ');
                if (index < 0) {
                    throw new RuntimeException("Illegal rule: ".concat(raw));
                }
                final String rule = rules[i] = raw.substring(0, index);
                loaderMap.put(rule, new LoaderEntry(rule,
                        (Loader) engine.getComponent(ClassEntry.wrap(raw.substring(index + 1).trim()))));
            }
            Arrays.sort(rules);
            ArrayUtil.invert(rules);
            final LoaderEntry[] loaderEntrys = this.entrys = new LoaderEntry[size];
            for (int i = 0; i < size; i++) {
                loaderEntrys[i] = loaderMap.get(rules[i]);
            }
            //default Loader
            _defaultLoader = (Loader) engine.getComponent(defaultLoader != null ? defaultLoader : ClassEntry.wrap(ClasspathLoader.class));
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    protected LoaderEntry getLoaderEntry(String resourceName) {
        final String[] rules = this._rules;
        for (int i = 0, len = rules.length; i < len; i++) {
            if (resourceName.startsWith(rules[i])) {
                return this.entrys[i];
            }
        }
        return null;
    }

    public Resource get(String name) {
        final LoaderEntry entry;
        if ((entry = getLoaderEntry(name)) != null) {
            return entry.get(name);
        }
        return this._defaultLoader.get(name);
    }

    public String concat(String parent, String name) {
        final LoaderEntry entry;
        final LoaderEntry parentEntry;
        if ((entry = getLoaderEntry(name)) != null) {
            return entry.normalize(name);
        }
        if ((parentEntry = getLoaderEntry(parent)) != null) {
            return parentEntry.concat(parent, name);
        }
        return this._defaultLoader.concat(parent, name);
    }

    public String normalize(String name) {
        final LoaderEntry entry;
        if ((entry = getLoaderEntry(name)) != null) {
            return entry.normalize(name);
        }
        return this._defaultLoader.normalize(name);
    }

    public void setLoaders(String loaders) {
        this.loaders = loaders;
    }

    public void setDefault(ClassEntry defaultLoader) {
        this.defaultLoader = defaultLoader;
    }

    public boolean isEnableCache(String name) {
        final LoaderEntry entry;
        if ((entry = getLoaderEntry(name)) != null) {
            return entry.isEnableCache(name);
        }
        return this._defaultLoader.isEnableCache(name);
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
