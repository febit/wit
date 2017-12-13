// Copyright (c) 2013-2016, febit.org. All Rights Reserved.
package org.febit.wit;

/**
 *
 * @author zqq90
 * @since 2.4.0
 */
@FunctionalInterface
public interface Vars {

    public static final Vars EMPTY = (accepter) -> {
        // Do nothing
    };

    @FunctionalInterface
    public static interface Accepter {

        void set(String key, Object value);
    }

    void exportTo(Accepter accepter);

}
