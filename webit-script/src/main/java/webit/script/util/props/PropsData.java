// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.
package webit.script.util.props;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Props data storage for base and profile properties. Properties can be
 * lookuped and modified only through this class.
 */
final class PropsData {

    private final HashMap<String, PropsEntry> baseProperties;
    private final HashMap<String, Map<String, PropsEntry>> profileProperties;

    PropsData() {
        this.baseProperties = new HashMap<String, PropsEntry>();
        this.profileProperties = new HashMap<String, Map<String, PropsEntry>>();
    }

    /**
     * Puts key-value pair into the map, with respect of appending duplicate
     * properties
     */
    private void put(final String profile, final Map<String, PropsEntry> map, final String key, final String value, final boolean append) {
        String realValue = value;
        boolean realAppend = append;
        if (append) {
            PropsEntry pv = map.get(key);
            if (pv != null) {
                realValue = pv.value + ',' + realValue;
                realAppend = pv.append;
            }
        }
        PropsEntry propsEntry = new PropsEntry(key, realValue, profile, realAppend);

        // add to the map
        map.put(key, propsEntry);
    }

    /**
     * Adds base property.
     */
    void putBaseProperty(final String key, final String value, final boolean append) {
        put(null, baseProperties, key, value, append);
    }

    void merge(PropsData src) {
        //baseProperties
        for (Map.Entry<String, PropsEntry> entry : src.baseProperties.entrySet()) {
            PropsEntry propsEntry = entry.getValue();
            putBaseProperty(entry.getKey(), propsEntry.value, propsEntry.append);
        }
        //profileProperties
        for (Map.Entry<String, Map<String, PropsEntry>> entry : this.profileProperties.entrySet()) {
            String profile = entry.getKey();
            for (Map.Entry<String, PropsEntry> entry1 : entry.getValue().entrySet()) {
                PropsEntry propsEntry = entry1.getValue();
                putProfileProperty(entry1.getKey(), propsEntry.value, profile, propsEntry.append);
            }
        }
    }

    /**
     * Returns base property or <code>null</code> if it doesn't exist.
     */
    String getBaseProperty(final String key) {
        final PropsEntry propsEntry;
        return (propsEntry = baseProperties.get(key)) != null
                ? propsEntry.getValue()
                : null;
    }

    String popBaseProperty(final String key) {
        final PropsEntry propsEntry;
        return (propsEntry = baseProperties.remove(key)) != null
                ? propsEntry.getValue()
                : null;
    }

    /**
     * Adds profile property.
     */
    void putProfileProperty(final String key, final String value, final String profile, final boolean append) {
        Map<String, PropsEntry> map = profileProperties.get(profile);
        if (map == null) {
            map = new HashMap<String, PropsEntry>();
            profileProperties.put(profile, map);
        }
        put(profile, map, key, value, append);
    }

    /**
     * Lookup props value through profiles and base properties.
     */
    String lookupValue(final String key, final String... profiles) {
        if (profiles != null) {
            for (String profile : profiles) {
                while (true) {
                    final Map<String, PropsEntry> profileMap = this.profileProperties.get(profile);
                    if (profileMap != null) {
                        final PropsEntry value = profileMap.get(key);
                        if (value != null) {
                            return value.getValue();
                        }
                    }
                    // go back with profile
                    final int ndx = profile.lastIndexOf('.');
                    if (ndx == -1) {
                        break;
                    }
                    profile = profile.substring(0, ndx);
                }
            }
        }
        return getBaseProperty(key);
    }

    /**
     * Resolves all macros in this props set. Called once on initialization.
     */
    void resolveMacros(String[] activeProfiles) {
        // start parsing
        int loopCount = 0;
        while (loopCount++ < 100) { //Note: MAX_INNER_MACROS
            boolean replaced = resolveMacros(this.baseProperties, activeProfiles);

            for (final Map.Entry<String, Map<String, PropsEntry>> entry : profileProperties.entrySet()) {
                replaced = resolveMacros(entry.getValue(), activeProfiles) || replaced;
            }

            if (!replaced) {
                break;
            }
        }
    }

    private boolean resolveMacros(final Map<String, PropsEntry> map, final String[] profiles) {
        boolean replaced = false;

        final StringTemplateParser.MacroResolver macroResolver = new StringTemplateParser.MacroResolver() {
            public String resolve(String macroName) {
                return lookupValue(macroName, profiles);
            }
        };

        Iterator<Map.Entry<String, PropsEntry>> iterator = map.entrySet().iterator();

        while (iterator.hasNext()) {
            Map.Entry<String, PropsEntry> entry = iterator.next();
            final PropsEntry pv = entry.getValue();
            final String newValue = StringTemplateParser.parse(pv.value, macroResolver);

            if (!newValue.equals(pv.value)) {
                if (newValue.length() == 0) {
                    iterator.remove();
                    replaced = true;
                    continue;
                }

                pv.resolved = newValue;
                replaced = true;
            } else {
                pv.resolved = null;
            }
        }
        return replaced;
    }

    // ---------------------------------------------------------------- extract
    /**
     * Extract props to target map.
     */
    void extract(final Map target, final String[] profiles, final String[] wildcardPatterns) {
        if (profiles != null) {
            for (String profile : profiles) {
                while (true) {
                    final Map<String, PropsEntry> map = this.profileProperties.get(profile);
                    if (map != null) {
                        extractMap(target, map, wildcardPatterns);
                    }

                    final int ndx = profile.indexOf('.');
                    if (ndx == -1) {
                        break;
                    }
                    profile = profile.substring(0, ndx);
                }
            }
        }
        extractMap(target, this.baseProperties, wildcardPatterns);
    }

    @SuppressWarnings("unchecked")
    void extractMap(final Map target, final Map<String, PropsEntry> map, final String[] wildcardPatterns) {
        for (Map.Entry<String, PropsEntry> entry : map.entrySet()) {
            final String key = entry.getKey();

            if (!target.containsKey(key)) {
                target.put(key, entry.getValue().getValue());
            }
        }
    }
}
