// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.
package webit.script.util.props;

/**
 * Holds original props value and generated one.
 */
final class PropsEntry {

    /**
     * Original value.
     */
    final String value;

    final boolean append;

    /**
     * Value with all macros resolved. May be <code>null</code> when value
     * doesn't contain anything to resolve.
     */
    String resolved;

    PropsEntry(final String key, final String value, String profile, boolean append) {
        this.value = value;
        this.append = append;
    }

    /**
     * Returns either resolved or real value.
     */
    String getValue() {
        return resolved != null ? resolved : value;
    }
}
