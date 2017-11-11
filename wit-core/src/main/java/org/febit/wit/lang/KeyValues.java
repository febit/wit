// Copyright (c) 2013-2016, febit.org. All Rights Reserved.
package org.febit.wit.lang;

/**
 *
 * @author zqq90
 */
public interface KeyValues {

    /**
     * @since 2.4.0
     */
    public static final KeyValues EMPTY = new KeyValues() {

        @Override
        public void exportTo(KeyValueAccepter accepter) {
            // Do nothing
        }
    };

    void exportTo(KeyValueAccepter accepter);

}
