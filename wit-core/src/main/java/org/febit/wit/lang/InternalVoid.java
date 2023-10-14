// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.lang;

import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * @author zqq90
 */
@EqualsAndHashCode
public final class InternalVoid implements Serializable {

    public static final InternalVoid VOID = new InternalVoid();

    private InternalVoid() {
        // single instance
    }
}
