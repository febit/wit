// Copyright (c) 2013-2016, febit.org. All Rights Reserved.
package org.febit.wit;

/**
 *
 * @author zqq90
 * @since 2.4.0
 */
public interface Vars {

    public static final Vars EMPTY = new Vars() {

        @Override
        public void exportTo(Accepter accepter) {
            // Do nothing
        }
    };

    public static interface Accepter {

        void set(String key, Object value);
    }

    void exportTo(Accepter accepter);

}
