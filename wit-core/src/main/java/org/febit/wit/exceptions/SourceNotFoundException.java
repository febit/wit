// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.exceptions;

import java.io.IOException;

public class SourceNotFoundException extends IOException {

    public SourceNotFoundException(String message) {
        super(message);
    }
}
