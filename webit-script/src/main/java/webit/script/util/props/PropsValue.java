// Copyright (c) 2003-2013, Jodd Team (jodd.org). All Rights Reserved.
package webit.script.util.props;

/**
 * Holds original props value and generated one.
 */
final class PropsValue {

    /**
     * Original value.
     */
    String value;
    /**
     * Value with all macros resolved. May be
     * <code>null</code> when value doesn't contain anything to resolve.
     */
    String resolved;

    PropsValue(final String value) {
        this.value = value;
    }

    /**
     * Returns either resolved or real value.
     */
    String getValue() {
        return resolved != null ? resolved : value;
    }

    @Override
    public String toString() {
        return "PropsValue{" + value + (resolved == null ? "" : "}{" + resolved) + '}';
    }
}
