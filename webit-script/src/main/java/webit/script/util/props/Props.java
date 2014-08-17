// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.
package webit.script.util.props;

import java.util.Map;
import webit.script.util.StringUtil;

/**
 * Super properties: fast, configurable, supports (ini) sections, profiles.
 * <p/>
 * Basic parsing rules:
 * <ul>
 * <li> By default, props files are UTF8 encoded.
 * <li> Leading and trailing spaces will be trimmed from section names and
 * property names.
 * <li> Leading and/or trailing spaces may be trimmed from property values.
 * <li> You can use either equal sign (=) or colon (:) to assign property values
 * <li> Comments begin with either a semicolon (;), or a sharp sign (#) and
 * extend to the end of line. It doesn't have to be the first character.
 * <li> A backslash (\) escapes the next character (e.g., \# is a literal #, \\
 * is a literal \).
 * <li> If the last character of a line is backslash (\), the value is continued
 * on the next line with new line character included.
 * <li> \\uXXXX is encoded as character
 * <li> \t, \r and \f are encoded as characters
 * </ul>
 * <p/>
 * Sections rules:
 * <ul>
 * <li> Section names are enclosed between [ and ].
 * <li> Properties following a section header belong to that section. Section
 * name is added as a prefix to section properties.
 * <li> Section ends with empty section definition [] or with new section start
 * </ul>
 * <p/>
 * Profiles rules:
 * <ul>
 * <li> Profile names are enclosed between &lt; and &gt; in property key.
 * <li> Each property key may contain zero, one or more profile definitions.
 * </ul>
 * <p/>
 * Macro rules:
 * <ul>
 * <li> Profile values may contain references to other properties using ${ and }
 * <li> Inner references are supported
 * <li> References are resolved first in the profile context and then in the
 * base props context.
 * </ul>
 */
public final class Props {

    private final PropsParser parser;
    private final PropsData data;
    private String[] activeProfiles;
    private boolean initialized;

    /**
     * Creates new props.
     */
    public Props() {
        this.data = (this.parser = new PropsParser()).propsData;
    }

    /**
     * Parses input string and loads provided properties map.
     */
    private void parse(final char[] data) {
        initialized = false;
        parser.parse(data);
    }

    /**
     * Loads props from the string.
     */
    public void load(final char[] data) {
        parse(data);
    }

    public void load(final String name, final String value) {
        data.putBaseProperty(name, value, false);
    }

    public void merge(final Props props) {
        data.merge(props.data);
    }

    public String getBaseProperty(final String name) {
        return data.getBaseProperty(name);
    }

    public String popBaseProperty(final String name) {
        return data.popBaseProperty(name);
    }

    public void append(final String name, final String value) {
        data.putBaseProperty(name, value, true);
    }

    public void setSkipEmptyProps(boolean skipEmptyProps) {
        this.parser.setSkipEmptyProps(skipEmptyProps);
    }

    /**
     * Returns value of property, using active profiles.
     */
    public String getValue(final String key) {
        initialize();
        return data.lookupValue(key, activeProfiles);
    }

    /**
     * Extracts props belonging to active profiles.
     */
    public void extractProps(final Map target) {
        initialize();
        data.extract(target, activeProfiles, null);
    }

    /**
     * Initializes props by replacing macros in values with the lookup values.
     */
    private void initialize() {
        if (initialized == false) {
            resolveActiveProfiles();
            data.resolveMacros(activeProfiles);
            initialized = true;
        }
    }

    /**
     * Resolves active profiles from property. If default active property is not
     * defined, nothing happens. Otherwise,
     */
    private void resolveActiveProfiles() {

        final String value = data.getBaseProperty("@profiles");
        if (value == null) {
            // no active profile set as the property, exit
            return;
        }

        if (StringUtil.isBlank(value)) {
            activeProfiles = null;
            return;
        }
        StringUtil.trimAll(activeProfiles = StringUtil.splitc(value, ','));
    }

}
