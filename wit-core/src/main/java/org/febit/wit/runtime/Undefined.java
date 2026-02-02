// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.runtime;

import lombok.EqualsAndHashCode;

import java.io.Serializable;

@EqualsAndHashCode
public final class Undefined implements Serializable {

    public static final Undefined UNDEFINED = new Undefined();

    private Undefined() {
        // single instance
    }
}
