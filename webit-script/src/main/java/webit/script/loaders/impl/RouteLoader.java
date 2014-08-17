// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script.loaders.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import webit.script.Engine;
import webit.script.Initable;
import webit.script.loaders.Loader;
import webit.script.loaders.Resource;
import webit.script.util.ClassEntry;
import webit.script.util.StringUtil;

/**
 *
 * @since 1.4.0
 * @author zqq90
 */
public class RouteLoader implements Loader, Initable {

    private final static char[] DELIMITERS = "\n\r,".toCharArray();
    protected String loaders;
    protected ClassEntry defaultLoader;

    protected Loader _defaultLoader;
    protected LoaderEntry[] _loLoaderEntrys;
    protected String[] _rules;

    public void init(final Engine engine) {
        try {
            //init Route rules
            initLoaderRoute(engine);
            //default Loader
            _defaultLoader = (Loader) engine.getComponent(defaultLoader != null ? defaultLoader : ClassEntry.wrap(ClasspathLoader.class));
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    protected void initLoaderRoute(Engine engine) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        if (loaders != null) {
            final String[] raws = StringUtil.splitc(this.loaders, DELIMITERS);
            final List<String> rules = new ArrayList<String>();
            final Map<String, LoaderEntry> loaderMap = new HashMap<String, LoaderEntry>();
            for (int i = 0, len = raws.length; i < len; i++) {
                final String raw = raws[i].trim();
                if (raw.length() > 0) {
                    final int index = raw.indexOf(' ');
                    if (index < 0) {
                        throw new RuntimeException("Illegal rule: ".concat(raw));
                    } else {
                        final String rule = raw.substring(0, index);
                        boolean inserted = false;
                        for (int j = 0; j < rules.size(); j++) {
                            if (rule.startsWith(rules.get(j))) {
                                rules.add(j, rule);
                                inserted = true;
                                break;
                            }
                        }
                        if (inserted == false) {
                            rules.add(rule);
                        }
                        loaderMap.put(rule,
                                new LoaderEntry(rule, (Loader) engine.getComponent(
                                                ClassEntry.wrap(
                                                        raw.substring(index + 1).trim()))));

                    }
                }
            }
            final int size = rules.size();
            rules.toArray(this._rules = new String[size]);
            this._loLoaderEntrys = new LoaderEntry[size];
            for (int i = 0; i < size; i++) {
                this._loLoaderEntrys[i] = loaderMap.get(this._rules[i]);
            }
        } else {
            this._rules = new String[0];
        }
    }

    protected LoaderEntry getLoaderEntry(String resourceName) {
        final String[] rules;
        for (int i = 0, len = (rules = this._rules).length; i < len; i++) {
            if (resourceName.startsWith(rules[i])) {
                return _loLoaderEntrys[i];
            }
        }
        return null;
    }

    public Resource get(String name) {
        final LoaderEntry entry = getLoaderEntry(name);
        if (entry != null) {
            return entry.get(name);
        } else {
            return this._defaultLoader.get(name);
        }
    }

    public String concat(String parent, String name) {
        final LoaderEntry entry;
        final LoaderEntry parentLoaderEntry;
        if ((entry = getLoaderEntry(name)) != null) {
            return entry.normalize(name);
        } else if ((parentLoaderEntry = getLoaderEntry(parent)) != null) {
            return parentLoaderEntry.concat(parent, name);
        } else {
            return this._defaultLoader.concat(parent, name);
        }
    }

    public String normalize(String name) {
        final LoaderEntry entry = getLoaderEntry(name);
        if (entry != null) {
            return entry.normalize(name);
        } else {
            return this._defaultLoader.normalize(name);
        }
    }

    public void setLoaders(String loaders) {
        this.loaders = loaders;
    }

    public void setDefault(ClassEntry defaultLoader) {
        this.defaultLoader = defaultLoader;
    }

    public boolean isEnableCache(String name) {
        final LoaderEntry entry = getLoaderEntry(name);
        if (entry != null) {
            return entry.isEnableCache(name);
        } else {
            return this._defaultLoader.isEnableCache(name);
        }
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
            return name != null
                    ? this.prefix.concat(name)
                    : null;
        }

        private boolean isEnableCache(String name) {
            return this.loader.isEnableCache(name.substring(this.prefixLength));
        }
    }
}
